package cz.mendelu.genetika.genoms.resources;

/**
 * Created by Honza on 23.01.2016.
 */
public class ResourceException extends RuntimeException {

    public ResourceException() {
    }

    public ResourceException(String message) {
        super(message);
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceException(Throwable cause) {
        super(cause);
    }
}
