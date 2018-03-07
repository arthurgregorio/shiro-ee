package br.eti.arthurgregorio.shiroee.config.messages;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 01/03/2018
 */
public interface MessageFormatter {

    /**
     * 
     * @param values
     * @return 
     */
    String format(Object... values);
}
