package cz.mendelu.genetika.rest.jetty;

import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.dao.jasdb.EmbeddedJasDB;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.genoms.resources.NCBI;
import cz.mendelu.genetika.palindrome.CompositePalindromeDetector;
import cz.mendelu.genetika.palindrome.PalindromeDetector;
import cz.mendelu.genetika.palindrome.PalindromeDetectorBuilder;
import cz.mendelu.genetika.palindrome.Simple2PalindromeDetector;
import cz.mendelu.genetika.user.User;
import cz.mendelu.genetika.user.UserService;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Honza on 23. 7. 2015.
 */
public class JettyContext {

    private static PalindromeDetectorBuilder palindromeDetectorBuilder;

    public static PalindromeDetectorBuilder getPalindromeDetectorBuilder() {
        return CompositePalindromeDetector.palindromeDetectorBuilder();
    }

    static void initBeforeCreateJetty() {
        ncbi = new NCBI( // Init service for NCBI database.
                Config.ncbi.restriction_timing(),
                Config.ncbi.url(),
                Config.ncbi.db(),
                Config.ncbi.retmode(),
                Config.ncbi.rettype());
    }

    public static JasSession getJasSession(User user) {
        return EmbeddedJasDB.getEmbeddedJasDB().getSession(user);
    }

    private static NCBI ncbi;
    public static NCBI getNCBI() {
        return ncbi;
    }

    private static UserService userService;
    public static UserService getUserService() {
        return new UserService();
    }
}
