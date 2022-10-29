package cz.mendelu.genetika.rest;

import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.dao.jasdb.JasAnalysePalindromeDao;
import cz.mendelu.genetika.dao.jasdb.JasGenomeDao;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.dao.jasdb.JasStoredPalindromeDao;
import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.format.ConvertorFactory;
import cz.mendelu.genetika.genoms.format.FormatConverter;
import cz.mendelu.genetika.palindrome.AnalysePalindrome;
import cz.mendelu.genetika.palindrome.StoredPalindrome;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Date;
import java.util.List;


@Path("genoms")
public class GenomsPath extends RestService {

    private static final Logger LOG = LoggerFactory.getLogger(GenomsPath.class);

    public static final JsonObjectBuilder jsonGenomHeader(Genome genome) {
        return Json.createObjectBuilder()
                .add("id", genome.getId())
                .add("name", genome.getName())
                .add("date", Config.server.time().format(new Date(genome.getDate())))
                .add("length", genome.getLength())
                .add("info", genome.getInfo());
    }

    public static final JsonObjectBuilder jsonGenomHeaderPart(Genome genome, int pos, int len) {
        JsonObjectBuilder json = jsonGenomHeader(genome);
        Sequence sequence = genome.sequence();
        if (sequence.existsSubSequence(pos, len)) {
            json.add("chromosome", sequence.getSequence(pos, len).toString());
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response headers() {
        LOG.info("Get genomes headers");
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();

            // Prevod do JSON
            JsonArrayBuilder entity = Json.createArrayBuilder();
            List<Genome> genomes = genomeDao.findAll();
            genomes.forEach(g -> entity.add(jsonGenomHeader(g)));

            return Response
                    .ok()
                    .entity(entity.build().toString())
                    .build();
        }
    }

    private void importSequence(JasGenomeDao genomeDao, String name, InputStream data, String format) throws IOException {
        File tmpFile = File.createTempFile("genome","");

        Genome genome = new Genome();
        genome.setName(name);
        genome.setDate(new Date().getTime());
        genome.setFile(tmpFile);

        // Convert format to internal representation
        FormatConverter converter = ConvertorFactory.convertor(format);
        StringBuilder info = new StringBuilder();

        try(OutputStream genomeOutputStream = new FileOutputStream(tmpFile)) {
            if(!converter.convert(data, genomeOutputStream, info)) {
                throw new IOException("Format conversion failed");
            }
        }

        genome.setInfo(info.toString());
        genome.setLength(tmpFile.length());
        genomeDao.store(genome, true);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doUpload(@FormDataParam("file") InputStream data,
                             @FormDataParam("format") String format,
                             @FormDataParam("name") String name) {
        LOG.info("Upload new genome {}, format: {}, data: {}", name, format, data);
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            try {
                JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
                importSequence(genomeDao, name, data, format);
            } catch(Exception e) {
                return Response
                        .status(412)
                        .entity(message("Input data is not in %s format", format))
                        .build();
            }

            return Response
                    .ok()
                    .build();
        } catch (Exception e) {
            return Response
                    .status(415)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doTextImport(TextImportQuery query) {
        LOG.info("Upload new genome {}, data: {}", query.getName(), query.getSequence());
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
            try {
                InputStream is = new ByteArrayInputStream(query.getSequence().getBytes());
                importSequence(genomeDao, query.getName(), is, FormatConverter.PLAIN);
            } catch(Exception e) {
                return Response
                        .status(412)
                        .entity(message("Input data is not in valid format"))
                        .build();
            }
            return Response
                    .ok()
                    .build();
        } catch (Exception e) {
            return Response
                    .status(415)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response genomes(@PathParam("id") String id,
                           @DefaultValue("0") @QueryParam("pos") int pos,
                           @DefaultValue("0") @QueryParam("len") int len) {
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
            Genome genome = genomeDao.get(id);

            if (pos == 0 && len == 0) {
                LOG.info("Get genome {} header", id);
                return Response
                        .ok()
                        .entity(jsonGenomHeader(genome).build().toString())
                        .build();
            } else {
                LOG.info("Get genome {} header with partial chromosome from {} of length {}.", id, pos, len);
                return Response
                        .ok()
                        .entity(jsonGenomHeaderPart(genome, pos, len).build().toString())
                        .build();
            }
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteGenomes(@PathParam("id") String id) {
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
            JasAnalysePalindromeDao analysisDao = dbSession.getAnalysePalindromeDao();
            JasStoredPalindromeDao palindromeDao = dbSession.getStoredPalindromeDao();

            Genome genome = genomeDao.get(id);

            LOG.info("Delete genome by id {}", id);
            genomeDao.delete(genome);

            List<AnalysePalindrome> analyses = analysisDao.findByGenomeId(id);
            analyses.forEach(a -> {
                //smazat palindromy
                List<StoredPalindrome> palindromes = palindromeDao.findByAnalysis(a.getId());
                palindromeDao.deleteAll(palindromes);

                //smazat analyzu
                analysisDao.delete(a);
            });

            return Response
                    .ok()
                    .build();
        }
    }

    public static class TextImportQuery {

        private String sequence;
        private String name;

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
