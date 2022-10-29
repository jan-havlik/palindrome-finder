package cz.mendelu.genetika.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Jiří Lýsek on 10.3.2016.
 */
@Path("app")
public class AppPath {

    private static final Logger LOG = LoggerFactory.getLogger(ExportPath.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response appInfo() {
        LOG.info("Get app info");

        //TODO jde to nejak dostat z Gradle?
        JsonObjectBuilder entity = Json.createObjectBuilder().add("version", "2.8.1");

        return Response
                .ok()
                .entity(entity.build().toString())
                .build();
    }

}