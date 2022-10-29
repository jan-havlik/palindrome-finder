package cz.mendelu.genetika.user;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Created by xkoloma1 on 06.01.2016.
 */
public class BasicAuthentication {

    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthentication.class);

    private BasicAuthentication() {}

    public static User credentialsWithBasicAuthentication(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                        LOG.debug("Credentials: " + credentials);
                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            String login = credentials.substring(0, p).trim();
                            String password = credentials.substring(p + 1).trim();

                            return new User(login, password);
                        } else {
                            LOG.error("Invalid authentication token");
                        }
                    } catch (UnsupportedEncodingException e) {
                        LOG.warn("Couldn't retrieve authentication", e);
                    }
                }
            }
        }

        return null;
    }
}
