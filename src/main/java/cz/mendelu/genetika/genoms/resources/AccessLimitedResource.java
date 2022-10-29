package cz.mendelu.genetika.genoms.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Honza on 23.01.2016.
 */
public abstract class AccessLimitedResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccessLimitedResource.class);

    private final long restrictionTiming;

    private long lastTime;

    public AccessLimitedResource(long restrictionTiming) {
        this.restrictionTiming = restrictionTiming;
        this.lastTime = System.currentTimeMillis();
    }

    @Override
    protected InputStream getResource(String urlAddress) {
        synchronized (this) {
            long delay = System.currentTimeMillis() - lastTime;
            if (delay < restrictionTiming) {
                try {
                    LOG.info("The time limit is applied, delay time is {} milliseconds.", delay);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new ResourceException("Obtaining source has been interrupted.");
                }
            }
            lastTime = System.currentTimeMillis();
            return super.getResource(urlAddress);
        }
    }

}
