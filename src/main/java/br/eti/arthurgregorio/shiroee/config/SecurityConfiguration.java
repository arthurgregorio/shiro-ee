package br.eti.arthurgregorio.shiroee.config;

import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public interface SecurityConfiguration {

    /**
     * 
     * @return 
     */
    FilterChainResolver configurteFilterChainResolver();
    
    /**
     * 
     * @return 
     */
    DefaultWebSecurityManager configureSecurityManager();
}
