package cz.mendelu.genetika;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by xkoloma1 on 11.12.2015.
 */
public class Config {
    static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileReader("config.properties"));
        } catch (IOException ioe) {
            try {
                PROPERTIES.load(ClassLoader.getSystemResourceAsStream("config.properties"));
            } catch (Exception e) {
                throw new InstantiationError("Config.properties not found");
            }
        }
    }

    private Config() {
    }

    public static final class app {
        private app() {}

        public enum Mode {DESKTOP, SERVER}

        public static final Mode mode() {
            return Mode.valueOf(PROPERTIES.getProperty("app.mode", "DESKTOP").toUpperCase());
        }

    }

    public static final class server {
        private server() {}

        public static final int port() {
            return Integer.valueOf(PROPERTIES.getProperty("server.port", "8080"));
        }

        public static final String contextPath() {
            return PROPERTIES.getProperty("server.context.path", "/");
        }

        public static final String restPath() {
            return PROPERTIES.getProperty("server.rest.path", "/rest/*");
        }

        public static final SimpleDateFormat time() { return  new SimpleDateFormat(PROPERTIES.getProperty("server.time.format", "YYYY-MM-dd'T'HH:mm:ss"));}
    }

    public static final class palindrome {
        private palindrome() {}

        public static final int analyzaLimit() {
            return Integer.valueOf(PROPERTIES.getProperty("palindrome.analyse.limit", "1028"));
        }

    }

    public static final class db {

        private static final String DB_DEFAULT_DISABLED = "Disabled";
        private db() {}

        public enum Type {JASDB_EMBEDDED}

        public static final Type type() {
            return Type.valueOf(PROPERTIES.getProperty("db.type", "jasDB_embedded").toUpperCase());
        }

        public static final String genomeDir() {
            return PROPERTIES.getProperty("db.genomeDir", "data");
        }

        public static final String directory() {
            return PROPERTIES.getProperty("db.directory", "jasdb");
        }

        public static final String defaultData() {
            String value = PROPERTIES.getProperty("db.default", DB_DEFAULT_DISABLED);
            return (DB_DEFAULT_DISABLED.equalsIgnoreCase(value)) ? null : value;
        }
    }

    public static final class ncbi {
        private ncbi() {}

        public static final String url() {
            return PROPERTIES.getProperty("ncbi.url", "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
        }

        public static final String db() {
            return PROPERTIES.getProperty("ncbi.db", "nucleotide");
        }

        public static final String retmode () {
            return PROPERTIES.getProperty("ncbi.retmode", "text");
        }

        public static final String rettype() {
            return PROPERTIES.getProperty("ncbi.rettype", "fasta");
        }

        public static final long restriction_timing() {
            return Long.valueOf(PROPERTIES.getProperty("ncbi.restriction_timing", "1000"));
        }
    }

    public static final class ncbiFeatureTable {
        private ncbiFeatureTable() {}

        public static final String url() {
            return PROPERTIES.getProperty("ncbi.ft.url", "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
        }

        public static final String db() {
            return PROPERTIES.getProperty("ncbi.ft.db", "nuccore");
        }

        public static final String retmode () {
            return PROPERTIES.getProperty("ncbi.ft.retmode", "text");
        }

        public static final String rettype() {
            return PROPERTIES.getProperty("ncbi.ft.rettype", "ft");
        }
    }

    public static final class mail {
        public static final String smtp() { return PROPERTIES.getProperty("mail.smtp", "localhost"); }

        public static final String from() { return PROPERTIES.getProperty("mail.from", "palindrome@ibp.cz"); }

        public static final int port() { return Integer.valueOf(PROPERTIES.getProperty("mail.port", "25")); }
    }
}
