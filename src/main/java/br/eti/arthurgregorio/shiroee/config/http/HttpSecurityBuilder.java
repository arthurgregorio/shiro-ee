package br.eti.arthurgregorio.shiroee.config.http;

import br.eti.arthurgregorio.shiroee.config.ConfigurationFactory;
import static br.eti.arthurgregorio.shiroee.config.Constants.AUTHENTICATED_OP;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.PropertiesConfiguration;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public abstract class HttpSecurityBuilder {
    
    private final String authcOperator;
    
    private final Map<String, String> rules;
    
    private final PropertiesConfiguration configuration;

    /**
     * 
     */
    public HttpSecurityBuilder() {
        
        this.configuration = ConfigurationFactory.get();
        
        this.rules = new HashMap<>();
        
        this.authcOperator = this.configuration.getString(
                "operator.authenticated", AUTHENTICATED_OP);
    }
    
    /**
     * 
     * @param path
     * @param rule
     * @return 
     */
    public HttpSecurityBuilder addRule(String path, String rule) {
        this.rules.put(path, this.format(rule));
        return this;
    }
    
    /**
     * 
     * @param path
     * @param rule
     * @param requireAuthc
     * @return 
     */
    public HttpSecurityBuilder addRule(String path, String rule, boolean requireAuthc) {
        this.rules.put(path, this.authcOperator + "," + this.format(rule));
        return this;
    }

    /**
     * 
     * @param path
     * @param rule
     * @return 
     */
    private String format(String rule) {
        return String.format(this.getDefaultPrefix(), rule);
    }
    
    /**
     * 
     * @return 
     */
    public Map<String, String> build() {
        return Collections.unmodifiableMap(this.rules);
    }
    
    /**
     * 
     * @return 
     */
    public abstract String getDefaultPrefix();  
}
