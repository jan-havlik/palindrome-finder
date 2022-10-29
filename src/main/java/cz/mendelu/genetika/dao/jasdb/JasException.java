package cz.mendelu.genetika.dao.jasdb;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
public class JasException extends RuntimeException {

    public JasException(String message) {
        super(message);
    }

    public JasException(String message, Throwable cause) {
        super(message, cause);
    }
}
