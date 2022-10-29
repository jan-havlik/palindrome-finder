package cz.mendelu.genetika.genoms.helpers;

import cz.mendelu.genetika.genoms.format.ConvertorFactory;
import cz.mendelu.genetika.genoms.format.FormatConverter;
import cz.mendelu.genetika.genoms.resources.NCBI;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * stahne NCBI genomy podle IDcek do cilove slozky
 *
 * stahuje i tzv. feature table soubory a uklada je
 *
 * Created by Jiří Lýsek on 16.11.2016.
 */
public class NCBIMultiDownloader {

    public static final String DOWNLOADED_FILE_EXT = ".txt";
    public static final String FASTA_FILE_EXT = ".fasta";

    private static final Logger LOG = LoggerFactory.getLogger(NCBIMultiDownloader.class);

    private String targetPath;

    private NCBI ncbiGenomes;
    private NCBI ncbiFeatureTable;

    private boolean storeFasta = true;

    public NCBIMultiDownloader(String targetPath, NCBI ncbiGenomes, NCBI ncbiFeatureTable) {
        this.targetPath = targetPath;
        this.ncbiGenomes = ncbiGenomes;
        this.ncbiFeatureTable = ncbiFeatureTable;
    }

    public void setStoreFasta(boolean v) {
        storeFasta = v;
    }

    public void init() {
        File targetDir = new File(this.targetPath);
        targetDir.mkdirs();
    }

    private void storeFastaFile(File targetFile, byte[] bytes) throws IOException {
        try (OutputStream genomeOutputStream = new FileOutputStream(targetFile)) {
            StringBuilder info = new StringBuilder();
            genomeOutputStream.write(bytes);
            genomeOutputStream.close();
        } catch (Exception e) {
            targetFile.delete();
            throw e;
        }
    }

    private void storePlainFile(File targetFile, FormatConverter converter, InputStream formNCBI) throws IOException {
        try (OutputStream genomeOutputStream = new FileOutputStream(targetFile)) {
            StringBuilder info = new StringBuilder();
            converter.convert(formNCBI, genomeOutputStream, info);
            genomeOutputStream.close();
        } catch (Exception e) {
            targetFile.delete();
            throw e;
        }
    }

    private boolean downloadGenome(String id, FormatConverter converter) {
        File targetFilePlain = new File(this.targetPath + "/" + id + DOWNLOADED_FILE_EXT);
        if (targetFilePlain.exists()) {
            LOG.warn("Genome with id {} already exists", id);
            return true;
        } else {
            InputStream formNCBI = ncbiGenomes.getResourceByID(id);
            try {
                byte[] bytes = IOUtils.toByteArray(formNCBI);
                if(storeFasta) {
                    File targetFileFasta = new File(this.targetPath + "/" + id + FASTA_FILE_EXT);
                    storeFastaFile(targetFileFasta, bytes);
                }
                storePlainFile(targetFilePlain, converter, new ByteArrayInputStream(bytes));
                return true;
            } catch(IOException e) {
                LOG.error("Genome file with id {} was not in correct format", id);
            }
        }
        return false;
    }

    private boolean downloadFeatureTable(String id) {
        File targetFile = new File(this.targetPath + "/" + id + "_ft" + DOWNLOADED_FILE_EXT);
        if(targetFile.exists()) {
            LOG.warn("Feature table file with id {} already exists", id);
            return true;
        } else {
            InputStream formNCBI = ncbiFeatureTable.getResourceByID(id);
            byte[] bytes = new byte[0];
            try {
                bytes = IOUtils.toByteArray(formNCBI);
                storeFastaFile(targetFile, bytes);
                return true;
            } catch (IOException e) {
                LOG.error("Feature table file with id {} was not in correct format", id);
            }
        }
        return false;
    }

    public Stream<String> downloadGenomes(Collection<String> IDs) {
        FormatConverter converter = ConvertorFactory.convertor(FormatConverter.FASTA);
        return IDs.stream().map(id -> {
            LOG.info("Download new genome from NCBI by id: {}", id);

            if(downloadGenome(id, converter) && downloadFeatureTable(id)) {
                return id;
            }

            return null;
        }).filter(id -> id != null);
    }

}
