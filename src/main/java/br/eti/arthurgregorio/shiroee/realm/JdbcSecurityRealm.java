package br.eti.arthurgregorio.shiroee.realm;

import br.eti.arthurgregorio.shiroee.auth.AuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.PasswordEncoder;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
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
import org.apache.shiro.authc.credential.PasswordMatcher;

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
        
        // instantiate the custom password matcher based on bcrypt
        final PasswordMatcher passwordMatcher = new PasswordMatcher();

        passwordMatcher.setPasswordService(new PasswordEncoder());
        
        super.setCredentialsMatcher(passwordMatcher);
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
        
        final UserDetails userDetails = this.authenticationMechanism
                 .getUserDetails(token.getUsername());
        
        if (!userDetails.isLdapBindAccount() && !userDetails.isBlocked()) {
            return new SimpleAuthenticationInfo(userDetails.getUsername(), 
                    userDetails.getPassword(), this.getName());
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
