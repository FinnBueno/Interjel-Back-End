package nl.interjel.management.util.converter;

import javax.ws.rs.ext.ParamConverter;

/**
 * @author Finn Bon
 */
public interface ParameterConverter<T> extends ParamConverter<T> {

    default int stringToInt(String s) {
        if (s == null)
            throw new IllegalArgumentException();
        int i;
        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        if (i < 0)
            throw new IllegalArgumentException();
        return i;
    }

}
