package br.eti.arthurgregorio.shiroee.config;

import br.eti.arthurgregorio.shiroee.config.http.HttpSecurityBuilder;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 02/03/2018
 */
public interface HttpSecurityConfiguration {

    /**
     * 
     * @return 
     */
    HttpSecurityBuilder configureHttpSecurity();
}
