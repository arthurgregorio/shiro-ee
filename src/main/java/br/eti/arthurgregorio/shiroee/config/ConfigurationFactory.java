/*
 * Copyright 2018 Arthur Gregorio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.eti.arthurgregorio.shiroee.config;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.CONFIGURATION_ERROR;
import java.io.File;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

/**
 * The configuration factory.
 * 
 * This class has a simple responsability, load and hold the values of ths 
 * library configuration to use when is needed
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public final class ConfigurationFactory {

    private static PropertiesConfiguration configuration;

    /**
     * Static constructor to instantiate the properties file with the 
     * configurations
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
     * Hide the constructor
     */
    private ConfigurationFactory() { }
    
    /**
     * @return the properties with the configuration
     */
    public static PropertiesConfiguration get() {
        return configuration;
    }
}
