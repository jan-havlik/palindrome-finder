package cz.mendelu.genetika.rest;

import cz.mendelu.genetika.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

/**
 * Created by Honza on 20.01.2016.
 */
public abstract class RestService {

    public static final String SESSION_ATTR_USER = "user";

    @Context
    protected HttpServletRequest httpRequest;

    protected final User getUser() {
        HttpSession session = httpRequest.getSession();
        return (User) session.getAttribute(UserPath.SESSION_ATTR_USER);
    }

    private static final String MESSAGE_FORMAT = "{\"message\":\"%s\"}";

    protected final String message(String msg) {
        return String.format(MESSAGE_FORMAT, msg);
    }

    protected final String message(String format, Object... params) {
        return String.format(MESSAGE_FORMAT, String.format(format, params));
    }
}
