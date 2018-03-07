package br.eti.arthurgregorio.shiroee.config.http;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public class PermissionHttpSecurityBuilder extends HttpSecurityBuilder {

    private final String permOperator = "perms[%s]";

    /**
     * 
     */
    public PermissionHttpSecurityBuilder() {
        super();
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getDefaultPrefix() {
        return this.permOperator;
    }
}
