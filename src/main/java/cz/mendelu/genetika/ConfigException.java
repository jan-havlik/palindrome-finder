package cz.mendelu.genetika;

import java.util.Objects;

/**
 * Created by Honza on 20.01.2016.
 */
public class ConfigException extends RuntimeException {

    public static ConfigException unsupportedConfigration(String name, Object value) {
        return new ConfigException(String.format("Configuration %s has unsupported value %s.", name, value));
    }

    public ConfigException() {
        super();
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
