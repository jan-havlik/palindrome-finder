package cz.mendelu.genetika.rest.jetty;

import cz.mendelu.genetika.rest.UserPath;
import cz.mendelu.genetika.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by xkoloma1 on 07.01.2016.
 */
public class SecureDesktopFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SecureDesktopFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * This method set session attribute @{link UserPath#SESSION_ATTR_USER} to @{link User#DEFAULT}.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        if (session.getAttribute(UserPath.SESSION_ATTR_USER) != User.DEFAULT) {
            session.setAttribute(UserPath.SESSION_ATTR_USER, User.DEFAULT);
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }

}
