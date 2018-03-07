package br.eti.arthurgregorio.shiroee.realm;

import br.eti.arthurgregorio.shiroee.auth.AuthenticationMechanism;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.AUTHENTICATION_ERROR;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public class JdbcSecurityRealm extends AuthorizingRealm {

    private final AuthenticationMechanism authenticationMechanism;

    /**
     * 
     * @param authenticationMechanism 
     */
    public JdbcSecurityRealm(AuthenticationMechanism authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }

    /**
     * 
     * @param authenticationToken
     * @return
     * @throws AuthenticationException 
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) 
            throws AuthenticationException {
        
        final UsernamePasswordToken token = 
                (UsernamePasswordToken) authenticationToken;
        
        if (this.authenticationMechanism.isAuthorized(token.getUsername())) {
            return new SimpleAuthenticationInfo(token.getUsername(), 
                    token.getCredentials(), this.getName());
        }

        throw new AuthenticationException(
                AUTHENTICATION_ERROR.format(token.getUsername()));
    }

    /**
     * 
     * @param principalCollection
     * @return 
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        
        final String username = (String) 
                this.getAvailablePrincipal(principalCollection);
        
        final SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
        
        authzInfo.setStringPermissions(
                this.authenticationMechanism.getPermissionsFor(username));
        
        return authzInfo;
    }
}
