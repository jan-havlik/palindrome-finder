package cz.mendelu.genetika.genoms.resources;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Honza on 23.01.2016.
 */
public abstract class AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);

    private static final String METHOD = "GET";

    private boolean sslEnabled = false;

    protected InputStream getResource(String urlAddress) {
        try {
            LOG.info("Start get resource from {}.", urlAddress);
            if (!sslEnabled) setSslEnabled();
            URL url = new URL(urlAddress);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(METHOD);
            urlConnection.connect();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //redirekt
                if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_MOVED_PERM || urlConnection.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
                    String newUrl = urlConnection.getHeaderField("Location");
                    LOG.warn("Resource moved to {}.", newUrl);
                    return this.getResource(newUrl);
                } else {
                    LOG.error("Get resource failed: {} ({}).", urlConnection.getResponseCode(), urlConnection.getResponseMessage());
                    throw new ResourceException(String.format("Get resource failed. Server response code %d, content:\n%s", IOUtils.toString(urlConnection.getInputStream())));
                }
            }

            LOG.info("Connection open, start download");
            return urlConnection.getInputStream();
        } catch (Exception e) {
            LOG.error("Get resource failed: {}.", e.getMessage());
            throw new ResourceException(String.format("Get resource from url %s failed.", urlAddress), e);
        }
    }

    private void setSslEnabled() throws NoSuchAlgorithmException, KeyManagementException {
        LOG.info("Create a trust manager that does not validate certificate chains.");
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        LOG.info("Install the all-trusting trust manager.");
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, null);

        LOG.info("Create an ssl socket factory with our all-trusting manager.");
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        });

        sslEnabled = true;
    }
}
