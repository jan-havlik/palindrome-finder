package cz.mendelu.genetika.rest.jetty;

import cz.mendelu.genetika.rest.UserPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by xkoloma1 on 07.01.2016.
 */
public class SecureServerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SecureServerFilter.class);
    private Map<Pattern, Rule> rules = new LinkedHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("secureFilterRules.properties")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;

                String[] splitLine = line.split("=", 2);
                if (splitLine.length == 1) continue;
                if (splitLine.length > 2) throw new InstantiationError("Detect ignored rule: " + line);

                // If the pattern does not contain the ':' add ':. *' at the end, to accept all methods
                if (!splitLine[0].contains("#")) {
                    splitLine[0] = splitLine[0].trim() + "#.*";
                }

                Pattern pattern = Pattern.compile(splitLine[0].trim());
                Rule rule = Rule.valueOf(splitLine[1].trim().toUpperCase());
                rules.put(pattern, rule);
            }

            LOG.info("Found secure rules {}.", rules);
        } catch (IOException e1) {
            throw new InstantiationError("secureFilterRules.properties not found");
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String url = request.getPathInfo();
        String method = request.getMethod();
        Rule rule = findRule(url, method);

        if (rule == Rule.PUBLIC) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession();
        if (session.getAttribute(UserPath.SESSION_ATTR_USER) == null) {
            response.getWriter().write("Access denied");
            response.setStatus(401); // Unauthorized
            return;
        }

        chain.doFilter(req, res);
    }

    private Rule findRule(String url, String method) {
        String with = url + "#" + method;
        for (Map.Entry<Pattern, Rule> rule : rules.entrySet()) {
            if (rule.getKey().matcher(with).find()) {
                return rule.getValue();
            }
        }
        return Rule.PRIVATE;
    }

    @Override
    public void destroy() {

    }

    enum Rule {PUBLIC, PRIVATE}
}
