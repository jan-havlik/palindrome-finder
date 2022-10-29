package cz.mendelu.genetika.rest.jetty;

import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.Start.Service;
import cz.mendelu.genetika.rest.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.File;
import java.util.EnumSet;

public class EmbeddedJetty extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJetty.class);

    private static EmbeddedJetty embeddedJetty;
    private Server jettyServer;

    private EmbeddedJetty() {
        super("Jetty thread");
        LOG.info("Create jetty.");
       JettyContext.initBeforeCreateJetty();

        jettyServer = new Server(Config.server.port());

        WebAppContext context = initWebAppContext();
        jettyServer.setHandler(context);

        // Register all rest class here!!!
        initRestServlet(context,
                GenomsPath.class,
                AnalysePalindromePath.class,
                UserPath.class,
                NcbiPath.class,
                ExportPath.class,
                AppPath.class,
                CaptchaPath.class);

        initSecureFilter(context, ((Config.app.mode() == Config.app.Mode.SERVER) ? SecureServerFilter.class : SecureDesktopFilter.class));
    }

    public static EmbeddedJetty getEmbeddedJetty() {
        if (embeddedJetty == null) {
            synchronized (EmbeddedJetty.class) {
                if (embeddedJetty == null) {
                    embeddedJetty = new EmbeddedJetty();
                }
            }
        }
        return embeddedJetty;
    }

    private static WebAppContext initWebAppContext() {
        String webAppPath = (new File("web").exists())
                ? "web" // Cesta v distribuci
                : "src/main/webapp"; // Cesta při vývoji.

        String contextPath = Config.server.contextPath();

        LOG.info("Create web-app from {} on url {}", webAppPath, contextPath);
        return new WebAppContext(webAppPath, contextPath);
    }

    private static void initRestServlet(WebAppContext context, Class... restClasses) {
        String restContextPath = Config.server.restPath();

        LOG.info("Create rest context on url {}", restContextPath);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, restContextPath);
        jerseyServlet.setInitOrder(0);

        StringBuilder classNames = new StringBuilder();
        for (Class restClass : restClasses) {
            classNames.append(",");
            classNames.append(restClass.getCanonicalName());
        }
        LOG.info("Rest classes: {}", classNames.substring(1));

        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES,
                "org.glassfish.jersey.media.multipart.MultiPartFeature" + classNames.toString());
        jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
    }

    private static final void initSecureFilter(WebAppContext context, Class filter) {
        LOG.info("Setup security filter: {}", filter.getSimpleName());
        context.addFilter(filter, Config.server.restPath(), EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
    }


    public void run() {
        try {
            LOG.info("Starting jetty.");
            synchronized (startUpMonitor) {
                jettyServer.start();

                // See up state indicator
                ready = true;
                startUpMonitor.notifyAll();
            }

            jettyServer.join();
        } catch (Exception e) {
            throw new RuntimeException("Jetty start failed.", e);
        } finally {
            jettyServer.destroy();
        }
    }

    @Override
    public void terminate() {
        LOG.info("Stop Jetty not available.");
    }

}
