package cz.mendelu.genetika.rest;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Random;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Created by Jiří Lýsek on 31.3.2016.
 */
@Path("captcha")
public class CaptchaPath extends RestService {

    private JsonObjectBuilder captchaResponse(File file) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("captcha", file.getName());
        return builder;
    }

    @GET
    public Response getCaptcaCode() {
        String webAppPath = (new File("web").exists())
                ? "web" // Cesta v distribuci
                : "src/main/webapp"; // Cesta při vývoji.
        File dir = new File(webAppPath + "/images/captcha");

        File[] filesList = dir.listFiles();
        if(filesList.length == 0) {
            Response.status(NOT_FOUND)
                    .entity(message("Captcha image not found."))
                    .build();
        }


        Random rn = new Random();
        int p = rn.nextInt(filesList.length);
        File f = filesList[p];

        return Response
                .ok()
                .entity(captchaResponse(f).build().toString())
                .build();
    }

}
