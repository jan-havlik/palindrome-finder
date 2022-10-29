package cz.mendelu.genetika;

import cz.mendelu.genetika.bulk.analysis.BulkAnalysisCSV;
import cz.mendelu.genetika.bulk.analysis.BulkAnalysisFolder;
import cz.mendelu.genetika.bulk.analysis.NCBIDownloader;
import cz.mendelu.genetika.bulk.analysis.helpers.FileList;
import cz.mendelu.genetika.dao.jasdb.EmbeddedJasDB;
import cz.mendelu.genetika.rest.jetty.EmbeddedJetty;
import javafx.util.Pair;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

/**
 * Created by xkoloma1 on 04.01.2016.
 */
public class Start {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    private static final Logger LOG = LoggerFactory.getLogger("Genetika");
    private static Service jasDBService;
    private static Service jettyService;

    private static void startServer() throws Exception {
        System.out.println(ANSI_GREEN + "   ____                 _   _ _         \n" +
                "  / ___| ___ _ __   ___| |_(_) | ____ _ \n" +
                " | |  _ / _ \\ '_ \\ / _ \\ __| | |/ / _` |\n" +
                " | |_| |  __/ | | |  __/ |_| |   < (_| |\n" +
                "  \\____|\\___|_| |_|\\___|\\__|_|_|\\_\\__,_|\n" +
                ANSI_YELLOW + "=========================================\n" + ANSI_RESET);

        // Create jasDB threads;
        if (Config.db.Type.JASDB_EMBEDDED == Config.db.type()) {
            jasDBService = EmbeddedJasDB.getEmbeddedJasDB();
            synchronized (jasDBService.getStartUpLock()) {
                jasDBService.start();
                jasDBService.getStartUpLock().wait();
            }
        } else {
            throw ConfigException.unsupportedConfigration("db.type", Config.db.type());
        }

        // Create jetty threads;
        jettyService = EmbeddedJetty.getEmbeddedJetty();
        synchronized (jettyService.getStartUpLock()) {
            jettyService.start();
            jettyService.getStartUpLock().wait();
        }

        // Join main thread to jetty
        LOG.info("ready for usage...\n{}", StringUtils.repeat("=", 40));

        if (Config.app.Mode.DESKTOP == Config.app.mode()) {
            showInWebBrowser();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jasDBService.terminate();
            jettyService.terminate();
        }));

        System.in.read();
        System.exit(0);
    }

    private static void startBulkAnalysisFasta(String inputFilePath, String outputPath, String extension,int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        BulkAnalysisFolder ba = new BulkAnalysisFolder(inputFilePath, outputPath, extension, minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);
        try {
            ba.runFasta();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startBulkAnalysisRaw(String inputFilePath, String outputPath, String extension, int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        BulkAnalysisFolder ba = new BulkAnalysisFolder(inputFilePath, outputPath, extension, minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);
        try {
            ba.runRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startNCBIDownload(String csvFilePath, String targetPath) throws IOException {
        LOG.info("Downloading genomes to {}.", targetPath);

        NCBIDownloader downloader = new NCBIDownloader(csvFilePath, targetPath);
        Stream<Pair<String, String>> idStream = downloader.download();
        idStream.forEach(info -> {});
    }

    private static void startNCBIFolderDownload(String csvFolderPath, String targetPath) {
        Stream<File> files = FileList.listCSVFiles(csvFolderPath);
        files.forEach(batchFile -> {
            try {
                startNCBIDownload(batchFile.getAbsolutePath(), targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Options buildCommandLineOptions() {
        Options options = new Options();

        Option inputFasta = new Option("df", "dir-fasta", true, "Path to FASTA files for bulk analysis.");
        Option inputRaw = new Option("dr", "dir-raw", true, "Path to raw files for bulk analysis.");
        Option cyclic = new Option("c", "circular", false, "Use this switch to set circular mode.");
        Option filterDinucleotide = new Option("fdin", "filter-dinucleotide", false, "Use this switch to filter ATATAT sequences.");
        Option extension = new Option("e", "extension", true, "File extension, default is 'fasta' or 'txt' for raw.");
        Option downloadCSV = new Option("dlcsv", "download-csv", true, "Download genomes and feature tables from NCBI, input is CSV file with name and ID. Remember to set target parameter.");
        Option downloadFolder = new Option("dldir", "download-dir", true, "Download genomes and feature tables from NCBI, input is folder with CSV files with name and ID. Remember to set target parameter.");
        Option analyseCSV = new Option("acsv", "analyse-csv", true, "Download and analyse genomes and feature tables from NCBI, input is CSV file with name and ID. Remember to set target parameter.");
        Option analyseFolder = new Option("adir", "analyse-dir", true, "Download and analyse genomes and feature tables from NCBI, input is folder with CSV files with name and ID. Remember to set target parameter.");
        Option target = new Option("t", "target", true, "Set target path for results. Mandatory for download.");
        Option minSpacer = new Option("smin", "min-spacer", true, "Min spacer of IR");
        Option maxSpacer = new Option("smax", "max-spacer", true, "Max spacer of IR");
        Option mismatches = new Option("mis", "mismatches", true, "IR mismatches (as string separated by a comma e.g. '0,1')");
        Option minIRLength = new Option("irmin", true, "Min length of IR");
        Option maxIRLength = new Option("irmax", true, "Max length of IR");
        Option help = new Option("h", "help", false, "Print this help message.");

        options.addOption(inputRaw);
        options.addOption(inputFasta);
        options.addOption(cyclic);
        options.addOption(filterDinucleotide);
        options.addOption(extension);
        options.addOption(downloadCSV);
        options.addOption(downloadFolder);
        options.addOption(analyseCSV);
        options.addOption(analyseFolder);
        options.addOption(target);
        options.addOption(minIRLength);
        options.addOption(maxIRLength);
        options.addOption(minSpacer);
        options.addOption(maxSpacer);
        options.addOption(mismatches);
        options.addOption(maxIRLength);
        options.addOption(help);

        return options;
    }

    private static CommandLine parseArguments(Options options, String ... args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }
    }

    public static void main(String ... args) throws Exception {
        if(args.length > 0) {
            Options options = buildCommandLineOptions();
            CommandLine cmd = parseArguments(options, args);

            // palindrome analysis size setup
            int minLength = 6;
            int maxLength = 30;
            if (cmd.hasOption("irmin")) {
                minLength = Integer.valueOf(cmd.getOptionValue("irmin"));
            }
            if (cmd.hasOption("irmax")) {
                maxLength = Integer.valueOf(cmd.getOptionValue("irmax"));
            }

            // palindrome analysis spacer setup
            int minSpacer = 0;
            int maxSpacer = 10;
            if (cmd.hasOption("min-spacer")) {
                minSpacer = Integer.valueOf(cmd.getOptionValue("min-spacer"));
            }
            if (cmd.hasOption("max-spacer")) {
                maxSpacer = Integer.valueOf(cmd.getOptionValue("max-spacer"));
            }

            // palindrome analysis mismatches setup
            String mismatches = "0,1";
            if (cmd.hasOption("mismatches")) {
                mismatches = String.valueOf(cmd.getOptionValue("mismatches"));
            }

            // analysis flags - circular, filter, ... set to true if present
            boolean circular = false;
            if (cmd.hasOption("circular")) {
                circular = true;
            }

            boolean filterAT = false;
            if (cmd.hasOption("filter-dinucleotide")) {
                filterAT = true;
            }

            if(cmd.hasOption("dir-fasta")) {

                //analyse fasta files in folder
                String inputFilePath = cmd.getOptionValue("dir-fasta");
                String extension = "fasta";
                if (cmd.hasOption("extension")) {
                    extension = cmd.getOptionValue("extension");
                }
                String outputPath = inputFilePath;
                if (cmd.hasOption("target")) {
                    outputPath = cmd.getOptionValue("target");
                }
                startBulkAnalysisFasta(
                    inputFilePath,
                    outputPath,
                    extension,
                    minLength,
                    maxLength,
                    minSpacer,
                    maxSpacer,
                    mismatches,
                    circular,
                    filterAT
                );

            } else if(cmd.hasOption("dir-raw")) {

                //analyse raw files in folder
                String inputFilePath = cmd.getOptionValue("dir-raw");
                String extension = "txt";
                if (cmd.hasOption("extension")) {
                    extension = cmd.getOptionValue("extension");
                }
                String outputPath = inputFilePath;
                if (cmd.hasOption("target")) {
                    outputPath = cmd.getOptionValue("target");
                }
                startBulkAnalysisRaw(
                    inputFilePath,
                    outputPath,
                    extension,
                    minLength,
                    maxLength,
                    minSpacer,
                    maxSpacer,
                    mismatches,
                    circular,
                    filterAT
                );

            } else if(cmd.hasOption("download-dir")) {

                //jen stazeni
                if(!cmd.hasOption("target")) {
                    LOG.error("Target parameter not specified.");
                    return;
                }
                startNCBIFolderDownload(cmd.getOptionValue("download-dir"), cmd.getOptionValue("target"));

            } else if(cmd.hasOption("download-csv")) {

                //jen stazeni
                if (!cmd.hasOption("target")) {
                    LOG.error("Target parameter not specified.");
                    return;
                }
                File csvFile = new File(cmd.getOptionValue("download-csv"));
                if (csvFile.isFile()) {
                    startNCBIDownload(cmd.getOptionValue("download-csv"), cmd.getOptionValue("target"));
                } else {
                    LOG.error("File {} not found.", csvFile.getName());
                }

            } else if(cmd.hasOption("analyse-csv")) {

                if(!cmd.hasOption("target")) {
                    LOG.error("Target parameter not specified.");
                    return;
                }
                File csvFile = new File(cmd.getOptionValue("analyse-csv"));
                if (csvFile.isFile()) {
                    BulkAnalysisCSV.analyseCSVFile(
                        csvFile,
                        cmd.getOptionValue("target"),
                        minLength,
                        maxLength,
                        minSpacer,
                        maxSpacer,
                        mismatches,
                        circular,
                        filterAT
                    );
                } else {
                    LOG.error("File {} not found.", csvFile.getName());
                }

            } else if(cmd.hasOption("analyse-dir")) {

                if(!cmd.hasOption("target")) {
                    LOG.error("Target parameter not specified.");
                    return;
                }
                BulkAnalysisCSV.analyseCSVFolder(
                    cmd.getOptionValue("analyse-dir"),
                    cmd.getOptionValue("target"),
                    minLength,
                    maxLength,
                    minSpacer,
                    maxSpacer,
                    mismatches,
                    circular,
                    filterAT
                );

            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("utility-name", options);
            }
        } else {
            startServer();
        }
    }

    private static void showInWebBrowser() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL("http://localhost:8080").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static abstract class Service extends Thread {

        protected final Object startUpMonitor = new Object();

        protected boolean ready = false;

        protected Service(String name) {
            super(name);
        }

        public boolean isReady() {
            return ready;
        }

        public Object getStartUpLock() {
            return startUpMonitor;
        }

        public abstract void terminate();
    }

}
