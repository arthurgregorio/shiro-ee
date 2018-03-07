package br.eti.arthurgregorio.shiroee.config;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@WebListener
public class SecurityWebListener extends EnvironmentLoaderListener {

    @Inject
    private DefaultSecurityConfiguration configuration;
    
    /**
     * 
     * @param event 
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        
        event.getServletContext().setInitParameter(
                ENVIRONMENT_CLASS_PARAM, DefaultWebEnvironment.class.getName());
        
        super.contextInitialized(event);
    }

    /**
     * 
     * @param servletContext
     * @return 
     */
    @Override
    protected WebEnvironment createEnvironment(ServletContext servletContext) {
        
        final DefaultWebEnvironment environment = (DefaultWebEnvironment) 
                super.createEnvironment(servletContext);
        
        environment.setSecurityManager(
                this.configuration.configureSecurityManager());
        environment.setFilterChainResolver(
                this.configuration.configurteFilterChainResolver());
        
        return environment;
    }
}
