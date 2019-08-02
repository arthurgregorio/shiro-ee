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
package br.eti.arthurgregorio.shiroee.config.http;

import br.eti.arthurgregorio.shiroee.config.ConfigurationFactory;
import org.apache.commons.configuration2.PropertiesConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.eti.arthurgregorio.shiroee.config.Constants.AUTHENTICATED_OP;

/**
 * The http path security builder, with this class you can build rules to secure the access to the application URL's
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public abstract class HttpSecurityBuilder {

    private final String authcOperator;
    private final Map<String, String> rules;

    protected final PropertiesConfiguration configuration;

    /**
     * Constructor...
     */
    public HttpSecurityBuilder() {
        this.configuration = ConfigurationFactory.get();
        this.rules = new HashMap<>();
        this.authcOperator = this.configuration.getString("operator.authenticated", AUTHENTICATED_OP);
    }

    /**
     * Get the builder operator
     *
     * @return the builder operator
     */
    public abstract String getBuilderOperator();

    /**
     * Method used to add rules to this builder
     *
     * @param path the path to be secured
     * @param rule the rule to be used to secure this path
     * @return this builder
     */
    public HttpSecurityBuilder add(String path, String rule) {
        return this.add(path, rule, false);
    }

    /**
     * Method used to add rules to this builder when need to be authenticated to be accessed
     *
     * @param path the path to be secured
     * @param rule the rule to be used to secure this path
     * @param authenticated if the 
     * @return this builder
     */
    public HttpSecurityBuilder add(String path, String rule, boolean authenticated) {

        if (authenticated) {
            this.rules.put(path, this.authcOperator + "," + this.format(rule));
        } else {
            this.rules.put(path, this.format(rule));
        }

        return this;
    }

    /**
     * With this method you can retrieve the rules created in the execution of this builder
     *
     * @return the map of rules to be used
     */
    public Map<String, String> build() {
        return Collections.unmodifiableMap(this.rules);
    }

    /**
     * Used to format the rule with the builder operator for the given rule
     *
     * @param rule the rule to be formatted
     * @return the rule formatted
     */
    private String format(String rule) {
        return String.format(this.getBuilderOperator(), rule);
    }
}
