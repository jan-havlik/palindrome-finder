package cz.mendelu.genetika.rest;

import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.dao.jasdb.JasSession;
import cz.mendelu.genetika.dao.jasdb.JasUserDao;
import cz.mendelu.genetika.dao.jasdb.JasUserSessionDao;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import cz.mendelu.genetika.user.BasicAuthentication;
import cz.mendelu.genetika.user.Password;
import cz.mendelu.genetika.user.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
@Path("user")
public class UserPath extends RestService {

    private static final Logger LOG = LoggerFactory.getLogger(UserPath.class);


    private static JsonArrayBuilder jsonArrayUsers(List<User> users) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        users.forEach(u -> json.add(jsonUser(u)));
        return json;
    }

    private static JsonObjectBuilder jsonUser(User user) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (user.getId() == null) {
            builder.addNull("id");
        } else {
            builder.add("id", user.getId());
        }

        builder.add("email", user.getEmail());
        return builder;
    }

    private void addUserToSession(User user, HttpServletRequest httpRequest, JasUserSessionDao sessionDao) {
        HttpSession session = httpRequest.getSession();
        session.setAttribute(SESSION_ATTR_USER, user);
        try {
            sessionDao.store(user, session.getId());
        } catch (Exception e) {
            LOG.error(String.format("Add user %s to session %s failed.", user.getEmail(), session.getId()), e);
        }
    }

    private void removeUsertFormSessino(HttpSession session, JasUserSessionDao sessionDao) {
        if (session != null && session.getAttribute(SESSION_ATTR_USER) != null) {
            session.invalidate();
            sessionDao.delete(session.getId());
        }
    }

    private String getSessionId(HttpServletRequest httpRequest) {
        for (Cookie cookie : httpRequest.getCookies()) {
            if (cookie.getName().equalsIgnoreCase("JSESSIONID")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userList() {
        LOG.info("Get user list.");
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserDao userDao = jasSession.getUserDao();

            List<User> users = userDao.findAll();
            return Response
                    .ok()
                    .entity(jsonArrayUsers(users)
                            .build()
                            .toString())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User request) {
        LOG.info("Create new user {}", request.getEmail());
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserDao userDao = jasSession.getUserDao();

            String email = request.getEmail();
            String password = request.getPassword();

            if (!Password.precondition(password)) {
                LOG.warn("Password preconditions failed.");
                return Response
                        .status(400) // Bad Request
                        .entity(message("Password preconditions failed."))
                        .build();
            }

            User current = userDao.findByEmail(email);
            if (current != null) {
                String msg = String.format("Email %s already exists, choose another one.", email);
                LOG.warn(msg);
                return Response
                        .status(400) // Bad Request
                        .entity(message(msg))
                        .build();
            }

            password = Password.getSaltedHash(password);
            User newUser = userDao.store(new User(email, password));

            if (Config.db.defaultData() != null) {
                User defaultUser = userDao.findByEmail(Config.db.defaultData());
                if (defaultUser == null) {
                    LOG.warn("The default user {} doesn't exists, the data can not be copied.", Config.db.defaultData());
                } else {
                    JettyContext.getUserService().copyUserData(defaultUser, newUser);
                }
            }

            return Response
                    .ok()
                    .entity(jsonUser(newUser)
                            .build()
                            .toString())
                    .build();

        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    private void sendResetPassMail(String newPass, User u) throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName(Config.mail.smtp());
        email.setSmtpPort(Config.mail.port());
        email.setFrom(Config.mail.from());
        email.setSubject("Password reset");
        email.setMsg("Your account:" + u.getEmail() + "\r\nYour new password is: " + newPass);
        email.addTo(u.getEmail());
        email.send();
    }

    @POST
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(ResetPasswordQuery query) {
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserDao userDao = jasSession.getUserDao();

            User user = userDao.findByEmail(query.getEmail());
            if(user != null) {
                String newPassword = RandomStringUtils.randomAlphanumeric(8);
                user.setPassword(Password.getSaltedHash(newPassword));
                userDao.store(user);

                sendResetPassMail(newPassword, user);

                return Response
                        .ok()
                        .build();
            } else {
                String msg = String.format("User with email %s not found.", query.getEmail());
                return Response
                        .status(404) // Not Found
                        .entity(message(msg))
                        .build();
            }
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePasswordQuery changePassQuery) {
        LOG.info("Change password");
        try (JasSession dbSession = JettyContext.getJasSession(null)) {
            User user = getUser();
            if(user != null) {
                JasUserDao userDao = dbSession.getUserDao();
                String newPassword = changePassQuery.getNewPassword();
                String oldPassword = changePassQuery.getOldPassword();

                if (!Password.precondition(newPassword)) {
                    LOG.warn("Password preconditions failed.");
                    return Response
                            .status(400) // Bad Request
                            .entity(message("Password preconditions failed."))
                            .build();
                }

                if(Password.check(changePassQuery.getOldPassword(), user.getPassword())) {
                    user.setPassword(Password.getSaltedHash(newPassword));
                    userDao.store(user);

                    sendResetPassMail(newPassword, user);

                    return Response
                            .ok()
                            .build();
                } else {
                    return Response
                            .status(500)
                            .entity(message("Current password does not match."))
                            .build();
                }
            } else {
                return Response
                        .status(500)
                        .entity(message("Not logged in."))
                        .build();
            }
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/login")
    public Response login(@Context HttpServletRequest httpRequest) {
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserDao userDao = jasSession.getUserDao();
            JasUserSessionDao sessionDao = jasSession.getUserSessionDao();
            HttpSession session = httpRequest.getSession();

            User request = BasicAuthentication.credentialsWithBasicAuthentication(httpRequest);
            LOG.info("Login user {}", request.getEmail());

            User stored = userDao.findByEmail(request.getEmail());
            if (stored == null) {
                String msg = String.format("Email %s not found.", request.getEmail());
                LOG.warn(msg);
                return Response
                        .status(404) // Not Found
                        .entity(message(msg))
                        .build();
            }

            if (Password.check(request.getPassword(), stored.getPassword())) {
                addUserToSession(stored, httpRequest, sessionDao);
                return Response
                        .ok()
                        .entity(jsonUser(stored).build().toString())
                        .build();

            } else {
                removeUsertFormSessino(session, sessionDao);
                return Response
                        .status(403)
                        .entity(message("Invalid password"))
                        .build();
            }
        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/logout")
    public Response logout(@Context HttpServletRequest httpRequest) {
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserSessionDao sessionDao = jasSession.getUserSessionDao();
            HttpSession session = httpRequest.getSession();
            removeUsertFormSessino(session, sessionDao);
        } catch (Exception e) {
            LOG.warn("Logout process failed.", e);
        }
        return Response
                .ok()
                .build();
    }

    @GET
    @Path("/auth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@Context HttpServletRequest httpRequest) {
        try (JasSession jasSession = JettyContext.getJasSession(null)) {
            JasUserSessionDao sessionDao = jasSession.getUserSessionDao();
            HttpSession session = httpRequest.getSession();

            if (session.getAttribute(SESSION_ATTR_USER) != null) {
                User user = (User) session.getAttribute(SESSION_ATTR_USER);
                return Response
                        .ok()
                        .entity(jsonUser(user).build().toString())
                        .build();
            }

            String sessionId = getSessionId(httpRequest);
            if (sessionId == null) return Response
                    .status(400)
                    .entity(message("Missing JSESSIONID cookie."))
                    .build();

            User user = sessionDao.getUserBySessionId(sessionId);
            if (user == null) return Response
                    .status(404) // Not Found
                    .entity(message("Session is not bound with user."))
                    .build();
            sessionDao.delete(sessionId);
            addUserToSession(user, httpRequest, sessionDao);
            return Response
                    .ok()
                    .entity(jsonUser(user).build().toString())
                    .build();

        } catch (Exception e) {
            return Response
                    .status(500)
                    .entity(message(e.getMessage()))
                    .build();
        }
    }

    public static class ResetPasswordQuery {

        private String email;

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }

    }

    public static class ChangePasswordQuery {

        private String newPassword;
        private String oldPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
    }

}
