package cz.mendelu.genetika.rest;


import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.dao.jasdb.JasAnalysePalindromeDao;
import cz.mendelu.genetika.dao.jasdb.JasGenomeDao;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.dao.jasdb.JasStoredPalindromeDao;
import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.format.FastaFormatConverter;
import cz.mendelu.genetika.palindrome.*;
import cz.mendelu.genetika.palindrome.stability.NNModelStabilityCalculator;
import cz.mendelu.genetika.palindrome.stability.StabilityCalculator;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * API for the analysis of palindromes.
 */
@Path("analyze")
public class AnalysePalindromePath extends RestService {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysePalindromePath.class);

    private static final StabilityCalculator STABILITY_CALCULATOR = new NNModelStabilityCalculator();
    private static final String FORMAT_SIGNATURE = "%d-%d-%d";
    private PalindromeDetectorBuilder palindromeDetectorBuilder = JettyContext.getPalindromeDetectorBuilder();

    private static JsonArrayBuilder jsonArrayPlaindromes(Iterable<Palindrome> matcher) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        if(matcher != null) {
            matcher.forEach(item -> {
                if (item != null) {
                    json.add(jsonPalindrome(item));
                }
            });
        }
        return json;
    }

    private static JsonObjectBuilder jsonPalindrome(Palindrome palindrome) {
        double linear = STABILITY_CALCULATOR.calculateLinearForm(palindrome);
        double cruciform = STABILITY_CALCULATOR.calculateCruciForm(palindrome) * 2.0;
        double delta = cruciform - linear;
        return Json.createObjectBuilder()
                .add("before", palindrome.getBefore().toString())
                .add("sequence", palindrome.getSequence().toString())
                .add("spacer", palindrome.getSpacer().toString())
                .add("opposite", palindrome.getOpposite().toString())
                .add("after", palindrome.getAfter().toString())
                .add("position", palindrome.getPosition())
                .add("mismatches", palindrome.getMismatches())
                .add("signature", String.format(FORMAT_SIGNATURE,
                        palindrome.getSequence().getLength(),
                        palindrome.getSpacer().getLength(),
                        palindrome.getMismatches()))
                .add("stability_NNModel", Json.createObjectBuilder()
                        .add("cruciform", cruciform)
                        .add("linear", linear)
                        .add("delta", delta));
    }

    private static JsonObjectBuilder jsonAnalysePalindrome(AnalysePalindrome analyse) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", analyse.getId())
            .add("status", String.valueOf(analyse.getStatus()));
        if (analyse.getStatus() == AnalysePalindrome.Status.WAITS) {
            return json;
        } else {
            json.add("genomeId", analyse.getGenomeId())
                    .add("name", analyse.getName())
                    .add("length", analyse.getLength())
                    .add("count", analyse.getCount())
                    .add("size", analyse.getSize())
                    .add("mismatches", analyse.getMismatches())
                    .add("spacer", analyse.getSpacer())
                    .add("cycle", Boolean.parseBoolean(analyse.getCycle()))
                    .add("date", Config.server.time().format(new Date(analyse.getDate())));
        }
        return json;
    }

    private void storePalindromeAnalysisResult(Genome genome, PalindromeMatcher matcher, AnalyzeQuery query) {
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasAnalysePalindromeDao analyseDao = dbSession.getAnalysePalindromeDao();
            JasStoredPalindromeDao palindromeDao = dbSession.getStoredPalindromeDao();

            AnalysePalindrome analysisResult = new AnalysePalindrome();
            analysisResult.setGenomeId(genome.getId());
            analysisResult.setLength(genome.getLength());
            analysisResult.setName(genome.getName());
            analysisResult.setCount(matcher.getCount());
            analysisResult.setDate(new Date().getTime());
            analysisResult.setSize(query.getSize());
            analysisResult.setMismatches(query.getMismatches());
            analysisResult.setSpacer(query.getSpacer());
            analysisResult.setCycle(query.getCycle() ? "true" : "false");
            analysisResult.setStatus(AnalysePalindrome.Status.DONE);

            analyseDao.store(analysisResult);

            //napojit palindromy na analyzu
            List<StoredPalindrome> foundPalindromes = new ArrayList<StoredPalindrome>();
            matcher.forEach(p -> {
                StoredPalindrome sp = new StoredPalindrome();
                sp.setAnalysisParentId(analysisResult.getId());
                sp.setMismatches(p.getMismatches());
                sp.setPosition(p.getPosition());
                sp.setSize(p.getSequence().getLength());
                sp.setSpacerSize(p.getSpacer().getLength());
                foundPalindromes.add(sp);
            });
            palindromeDao.storeAll(foundPalindromes);
        }
    }

    /**
     * Creating a new analysis of the saved genome.
     *
     * @param id stored genome. A list of stored genomes can be obtained at @{link GenomsPath#headers}
     * @param query
     * @return
     */
    @POST
    @Path("palindrome/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeGenom(@PathParam("id") String id, AnalyzeQuery query) {
        LOG.info("User {} call analyze, id: {}, size: {}, spacer: {}, mismatches: {}",
                (getUser() != null) ? getUser().getEmail() : "anonymous",
                query.getSequence(),
                query.getSize(),
                query.getSpacer(),
                query.getMismatches());
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();

            long time = System.currentTimeMillis();
            PalindromeDetector palindromeDetector = palindromeDetectorBuilder.getPalindromeDetector(
                    query.size.getValues(),
                    query.spacer.getValues(),
                    query.mismatches.getValues());
            if (query.getCycle()) palindromeDetector.setCycleMode(true);
            palindromeDetector.setAtatFilter(query.getDinucleotide());

            Genome genome = genomeDao.get(id);

            LOG.info("Genome id: {} found as: {}", id, genome.getName());

            PalindromeMatcher matcher = palindromeDetector.findPalindrome(genome.sequence());
            LOG.info("AnalysePalindrome done in {}s, found {} palindromes.", (System.currentTimeMillis() - time), matcher.getCount());

            if (matcher.getCount() > Config.palindrome.analyzaLimit()) {
                return Response
                        .status(400)
                        .entity(message("Too many matches: " + matcher.getCount()))
                        .build();
            }

            //ulozit log analyzy do DB
            if(query.getStoreResult()) {
                storePalindromeAnalysisResult(genome, matcher, query);
            }

            return Response
                    .ok()
                    .entity(Json.createObjectBuilder()
                            .add("size", genome.getLength())
                            .add("genomeId", genome.getId())
                            .add("palindromes", jsonArrayPlaindromes(matcher))
                            .build().toString())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    /**
     * Analyse string input.
     * @param query
     * @return
     */
    @POST
    @Path("/palindrome")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeInput(AnalyzeQuery query) {
        LOG.info("User {} call analyze, genome length: {}, size: {}, spacer: {}, mismatches: {}",
                (getUser() != null) ? getUser().getEmail() : "anonymous",
                query.getSequence(),
                query.getSize(),
                query.getSpacer(),
                query.getMismatches());
        long time = System.currentTimeMillis();

        // Ocisteni sekvence
        LOG.debug("Start convert input in FASTA to valid sequence.");
        ByteArrayOutputStream cleanStream = new ByteArrayOutputStream();
        ByteArrayInputStream dirtyStream = new ByteArrayInputStream(query.getSequence().getBytes());
        try {
            boolean convert = new FastaFormatConverter().convert(dirtyStream, cleanStream, null);
            if (!convert) {
                LOG.info("Invalid input data: {}", query.getSequence());
                return Response
                        .status(400) // Bad Request
                        .entity(message("Invalid input data"))
                        .build();
            }
        } catch (IOException e) {
            LOG.error("Illegal input data: {}", query.getSequence());
            return Response
                    .serverError()
                    .entity(message(e.getMessage()))
                    .build();
        }

        // Analyza palindromu
        Sequence sequence = new Sequence(cleanStream.toByteArray());
        LOG.debug("Start analyze input sequence: {}", sequence.toString());

        PalindromeDetector palindromeDetector = palindromeDetectorBuilder.getPalindromeDetector(
                query.size.getValues(),
                query.spacer.getValues(),
                query.mismatches.getValues());
        if (query.getCycle()) palindromeDetector.setCycleMode(true);
        palindromeDetector.setAtatFilter(query.getDinucleotide());
        PalindromeMatcher matcher = palindromeDetector.findPalindrome(sequence);
        LOG.info("AnalysePalindrome done in {}s, found {} palindromes.", (System.currentTimeMillis() - time), matcher.getCount());

        if (matcher.getCount() > Config.palindrome.analyzaLimit()) {
            return Response
                    .status(400)
                    .entity(message("Too many matches: " + matcher.getCount()))
                    .build();
        }

        return Response
                .ok()
                .entity(Json.createObjectBuilder()
                        .add("size", sequence.getLength())
                        .add("sequence", sequence.toString())
                        .add("palindromes", jsonArrayPlaindromes(matcher))
                        .build().toString())
                .build();

    }

    /**
     * vraci seznam ulozenych analyz palindromu z DB
     * @return
     */
    @GET
    @Path("/palindrome")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPalindromeAnalysis() {
        LOG.info("List palindrome analysis");
        try (JasSession jasSession = JettyContext.getJasSession(getUser())) {
            JasAnalysePalindromeDao analyseDao = jasSession.getAnalysePalindromeDao();

            JsonArrayBuilder entity = Json.createArrayBuilder();
            List<AnalysePalindrome> analyses = analyseDao.findAll();
            analyses.forEach(a -> entity.add(jsonAnalysePalindrome(a)));

            return Response
                    .ok()
                    .entity(entity.build().toString())
                    .build();

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    /**
     * detail ulozene analyzy palindromu
     * @param id
     * @return
     */
    @GET
    @Path("/palindrome/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPalindomeAnalysisDetail(@PathParam("id") String id) {
        LOG.info("Call palindrome result by id: {}", id);
        try (JasSession jasSession = JettyContext.getJasSession(getUser())) {
            JasAnalysePalindromeDao analyseDao = jasSession.getAnalysePalindromeDao();
            JasGenomeDao genomeDao = jasSession.getJasGenomeDao();
            JasStoredPalindromeDao palindromeDao = jasSession.getStoredPalindromeDao();

            AnalysePalindrome analysis = analyseDao.get(id);
            if (analysis == null)
                return Response
                        .status(NOT_FOUND)
                        .entity(message("Analysis not found."))
                        .build();

            //nacteme genom, ze ktereho potom postavime palindromy
            Genome genome = genomeDao.get(analysis.getGenomeId());
            if (genome == null)
                return Response
                        .status(NOT_FOUND)
                        .entity(message("Analysis not found."))
                        .build();

            //palindromy
            JsonArrayBuilder entityPalindromes = Json.createArrayBuilder();
            List<StoredPalindrome> palindromes = palindromeDao.findByAnalysis(analysis.getId());
            palindromes.forEach(sp -> {
                Palindrome p = sp.makePalindrome(genome);
                entityPalindromes.add(jsonPalindrome(p));
            });

            return Response
                    .ok()
                    .entity(Json.createObjectBuilder()
                            .add("size", analysis.getLength())
                            .add("genomeId", analysis.getGenomeId())
                            .add("analysis", jsonAnalysePalindrome(analysis))
                            .add("palindromes", entityPalindromes.build())
                            .build().toString())
                    .build();

        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    /**
     * smaze analyzu palindromu
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("/palindrome/{id}")
    public Response deletePalindromeAnalysis(@PathParam("id") String id) {
        LOG.info("Delete palindrome analysus result by id: {}", id);
        try (JasSession jasSession = JettyContext.getJasSession(getUser())) {
            JasAnalysePalindromeDao analyseDao = jasSession.getAnalysePalindromeDao();
            JasStoredPalindromeDao palindromeDao = jasSession.getStoredPalindromeDao();

            AnalysePalindrome analysis = analyseDao.get(id);
            if (analysis == null)
                return Response
                        .status(NOT_FOUND)
                        .entity(message("Analysis not found."))
                        .build();

            //palindromy
            List<StoredPalindrome> palindromes = palindromeDao.findByAnalysis(analysis.getId());
            palindromeDao.deleteAll(palindromes);

            analyseDao.delete(id);

            return Response
                    .ok()
                    .build();
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    /**
     * Genome query json: <code>
     * {
     * "size":"NumberRange",
     * "space":"NumberRAnge"
     * }
     * <p>
     * </code>
     */
    public static class AnalyzeQuery {

        private NumberRange size = new NumberRange("6-30");

        private NumberRange spacer = new NumberRange("0-10");

        private NumberRange mismatches = new NumberRange("0,1");

        private String sequence;

        private Boolean cycle;

        private Boolean dinucleotide;

        private Boolean storeResult;

        public String getSize() {
            return size.toString();
        }

        public void setSize(NumberRange size) {
            this.size = size;
        }

        public void setSize(String size) {
            this.size = new NumberRange(size);
            if (this.size.getValues().contains(0) || this.size.getValues().contains(1)) {
                throw new IllegalArgumentException();
            }
        }

        public void setSpacer(NumberRange spacer) {
            this.spacer = spacer;
        }

        public void setMismatches(NumberRange mismatches) {
            this.mismatches = mismatches;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public String getSpacer() {
            return spacer.toString();
        }

        public void setSpacer(String spacer) {
            this.spacer = new NumberRange(spacer);
        }

        public String getMismatches() {
            return mismatches.toString();
        }

        public void setMismatches(String mismatches) {
            this.mismatches = new NumberRange(mismatches);
        }

        public Boolean getCycle() {
            return (cycle == null) ? Boolean.FALSE : cycle;
        }

        public void setCycle(Boolean cycle) {
            this.cycle = cycle;
        }

        public Boolean getDinucleotide() {
            return (dinucleotide == null) ? Boolean.TRUE : dinucleotide;
        }

        public void setDinucleotide(Boolean dinucleotide) {
            this.dinucleotide = dinucleotide;
        }

        public Boolean getStoreResult() { return (storeResult == null) ? Boolean.FALSE : storeResult; }

        public void setStoreResult(Boolean storeResult) { this.storeResult = storeResult; }
    }

}
