package cz.mendelu.genetika.user;

import cz.mendelu.genetika.dao.jasdb.*;
import cz.mendelu.genetika.genoms.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xkoloma1 on 29.02.2016.
 */
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private EmbeddedJasDB jasDB = EmbeddedJasDB.getEmbeddedJasDB();

    public UserService() {
    }

    public void copyUserData(User fromUser, User toUser) {
        JasSession fromJasSesstion = jasDB.getSession(fromUser);
        JasSession toJasSession = jasDB.getSession(toUser);


        // Copy genomes
        Map<String, String> genomesMap = new HashMap<>();
        JasGenomeDao fromGenomeDao = fromJasSesstion.getJasGenomeDao();
        JasGenomeDao toGenomeDao = toJasSession.getJasGenomeDao();
        fromGenomeDao.findAll().forEach(genome -> {
            String fId = genome.getId();
            genome.setId(null);
            genome = toGenomeDao.store(genome);
            String tId = genome.getId();
            genomesMap.put(fId, tId);
        });

        // Copy analysis
        Map<String, String> analysisMap = new HashMap<>();
        JasAnalysePalindromeDao fromAnalyseDao = fromJasSesstion.getAnalysePalindromeDao();
        JasAnalysePalindromeDao toAnalyseDao = toJasSession.getAnalysePalindromeDao();
        fromAnalyseDao.findAll().forEach(analyse -> {
            String fId = analyse.getId();
            analyse.setId(null);
            analyse.setGenomeId(genomesMap.get(analyse.getGenomeId()));
            analyse = toAnalyseDao.store(analyse);
            String tId = analyse.getId();
            analysisMap.put(fId, tId);
        });

        // Copy palindromes
        Map<String, String> palindromesMap = new HashMap<>();
        JasStoredPalindromeDao fromPalindromeDao = fromJasSesstion.getStoredPalindromeDao();
        JasStoredPalindromeDao storedPalindromeDao = toJasSession.getStoredPalindromeDao();
        fromPalindromeDao.findAll().forEach(palindrome -> {
            String fId = palindrome.getId();
            palindrome.setId(null);
            palindrome.setAnalysisParentId(analysisMap.get(palindrome.getAnalysisParentId()));
            palindrome = storedPalindromeDao.store(palindrome);
            String tId = palindrome.getId();
            palindromesMap.put(fId, tId);
        });

        LOG.info("Copy user data from {} to {}. Copy genomes: {}, analysis: {}, palindromes: {}", fromUser.getEmail(), toUser.getEmail(), genomesMap.size(), analysisMap.size(), palindromesMap.size());
    }



}
