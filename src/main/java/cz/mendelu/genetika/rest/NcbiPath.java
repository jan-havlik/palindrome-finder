package cz.mendelu.genetika.rest;

import cz.mendelu.genetika.dao.jasdb.JasGenomeDao;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.genoms.format.ConvertorFactory;
import cz.mendelu.genetika.genoms.format.FormatConverter;
import cz.mendelu.genetika.genoms.resources.NCBI;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Honza on 23.01.2016.
 */
@Path("ncbi")
public class NcbiPath extends RestService {

    private static final Logger LOG = LoggerFactory.getLogger(NcbiPath.class);

    private final NCBI ncbi = JettyContext.getNCBI();

    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") @NotNull String id) {
        LOG.info("Download new genome from ncbi by id: {}", id);
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
            File tmpFile = File.createTempFile("ncbi","");

            InputStream formNCBI = ncbi.getResourceByID(id);

            // Convert format to internal representation
            FormatConverter converter = ConvertorFactory.convertor(FormatConverter.FASTA);
            StringBuilder info = new StringBuilder();
            try (OutputStream genomeOutputStream = new FileOutputStream(tmpFile)) {
                if (!converter.convert(formNCBI, genomeOutputStream, info)) {
                    return Response
                            .status(412)
                            .entity(message("Input data is not in %s format", FormatConverter.FASTA))
                            .build();
                }
            }

            Genome genome = new Genome();
            genome.setName(info.substring(info.lastIndexOf("|") + 1).trim());
            genome.setDate(new Date().getTime());
            genome.setFile(tmpFile);
            genome.setInfo(info.toString());
            genome.setLength(tmpFile.length());
            genomeDao.store(genome, true);

            return Response
                    .ok()
                    .entity(GenomsPath.jsonGenomHeader(genome).build().toString())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(415)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }


}
