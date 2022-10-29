package cz.mendelu.genetika.rest;

import cz.mendelu.genetika.dao.jasdb.JasAnalysePalindromeDao;
import cz.mendelu.genetika.dao.jasdb.JasGenomeDao;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.dao.jasdb.JasStoredPalindromeDao;
import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.palindrome.AnalysePalindrome;
import cz.mendelu.genetika.palindrome.Palindrome;
import cz.mendelu.genetika.palindrome.StoredPalindrome;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * exporty dat z analyzy
 * TODO: nahradit export pres string nejakou knihovnou
 *
 * Created by Jiří Lýsek on 29.2.2016.
 */
@Path("export")
public class ExportPath extends RestService {

    private static final Logger LOG = LoggerFactory.getLogger(ExportPath.class);
    private static final String CSV_SEPARATOR = ",";

    private void incrementMapCount(SortedMap<Long, Integer> map, long key) {
        Integer val = 0;
        val = map.get(key);
        if(val == null) {
            map.put(key, 1);
        } else {
            map.put(key, val + 1);
        }
    }

    /**
     * vygeneruje nazev vhodny pro poslani pres HTTP
     *
     * @param g
     * @return
     */
    private String getNormalisedSequenceName(Genome g) {
        if(g != null) {
            String n = g.getName();
            n = n.toLowerCase();
            n = n.replaceAll("[^a-z ]", "");
            n = n.replace(" ", "-");
            return n;
        } else {
            return "analysis";
        }
    }

    /**
     * export jen hlavicky
     *
     * @param id
     * @return
     */
    @GET
    @Path("csv/analysis/palindrome/{id}/header")
    public Response exportPalindromeAnalysisHeader(@PathParam("id") String id) {
        LOG.info("Called export palindrome analysis header, id: {}", id);
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            //vygenerovat CSV data
            JasAnalysePalindromeDao analyseDao = dbSession.getAnalysePalindromeDao();
            JasStoredPalindromeDao palindromeDao = dbSession.getStoredPalindromeDao();
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();

            SortedMap<Long, Integer> sizeCount = new TreeMap<Long, Integer>();
            SortedMap<Long, Integer> spacerCount = new TreeMap<Long, Integer>();
            SortedMap<Long, Integer> mismatchCount = new TreeMap<Long, Integer>();

            AnalysePalindrome analysis = analyseDao.get(id);
            if (analysis == null)
                return Response
                        .status(NOT_FOUND)
                        .entity(message("Analysis not found."))
                        .build();

            //palindromy
            List<StoredPalindrome> palindromes = palindromeDao.findByAnalysis(analysis.getId());
            for(StoredPalindrome sp : palindromes) {
                incrementMapCount(sizeCount, sp.getSize());
                incrementMapCount(spacerCount, sp.getSpacerSize());
                incrementMapCount(mismatchCount, sp.getMismatches());
            }

            //zahlavi
            String result = "Sequence size" + CSV_SEPARATOR +
                    "Amount\r\n";
            for(Long key : sizeCount.keySet()) {
                result += key + CSV_SEPARATOR + sizeCount.get(key) + "\r\n";
            }

            result += "Spacer size" + CSV_SEPARATOR +
                    "Amount\r\n";
            for(Long key : spacerCount.keySet()) {
                result += key + CSV_SEPARATOR + spacerCount.get(key) + "\r\n";
            }

            result += "Mismatch count" + CSV_SEPARATOR +
                    "Amount\r\n";
            for(Long key : mismatchCount.keySet()) {
                result += key + CSV_SEPARATOR + mismatchCount.get(key) + "\r\n";
            }

            Genome genome = genomeDao.get(analysis.getGenomeId());

            return Response
                    .ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=\"" + getNormalisedSequenceName(genome) + "_summary.csv\"")
                    .entity(result)
                    .build();
        }
    }

    /**
     * export do CSV cele analyzy
     *
     * @param id
     * @return
     */
    @GET
    @Path("csv/analysis/palindrome/{id}")
    public Response exportPalindromeAnalysis(@PathParam("id") String id) {
        LOG.info("Called export palindrome analysis, id: {}", id);
        try (JasSession dbSession = JettyContext.getJasSession(getUser())) {
            //vygenerovat CSV data
            JasAnalysePalindromeDao analyseDao = dbSession.getAnalysePalindromeDao();
            JasGenomeDao genomeDao = dbSession.getJasGenomeDao();
            JasStoredPalindromeDao palindromeDao = dbSession.getStoredPalindromeDao();

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

            //zahlavi CSV
            String result = "Sequence" + CSV_SEPARATOR +
                            "Spacer" + CSV_SEPARATOR +
                            "Opposite" + CSV_SEPARATOR +
                            "Length" + CSV_SEPARATOR +
                            "Spacer length" + CSV_SEPARATOR +
                            "Mismatch count" + CSV_SEPARATOR +
                            "Position\r\n";

            //palindromy
            JsonArrayBuilder entityPalindromes = Json.createArrayBuilder();
            List<StoredPalindrome> palindromes = palindromeDao.findByAnalysis(analysis.getId());
            for(StoredPalindrome sp : palindromes) {
                Palindrome p = sp.makePalindrome(genome);
                long pos = p.getPosition() + 1;
                result += p.getSequence() + CSV_SEPARATOR +
                          p.getSpacer() + CSV_SEPARATOR +
                          p.getOpposite() + CSV_SEPARATOR +
                          p.getSize() + CSV_SEPARATOR +
                          p.getSpacer().getLength() + CSV_SEPARATOR +
                          p.getMismatches() + CSV_SEPARATOR +
                          pos +
                          "\r\n";
            }

            return Response
                    .ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=\"" + getNormalisedSequenceName(genome) + ".csv\"")
                    .entity(result)
                    .build();
        }
    }
}
