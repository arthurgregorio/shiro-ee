package br.eti.arthurgregorio.shiroee.config;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.CONFIGURATION_ERROR;
import java.io.File;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public class ConfigurationFactory {

    private static PropertiesConfiguration configuration;

    /**
     * 
     */
    static {
        try {
             configuration = new Configurations()
                    .properties(new File("shiroee.properties"));
        } catch (org.apache.commons.configuration2.ex.ConfigurationException ex) {
            throw new org.apache.shiro.config.ConfigurationException(
                    CONFIGURATION_ERROR.format("shiroee.properties"));
        }
    }
    
    /**
     * 
     */
    private ConfigurationFactory() { }
    
    /**
     * 
     * @return 
     */
    public static PropertiesConfiguration get() {
        return configuration;
    }
}
