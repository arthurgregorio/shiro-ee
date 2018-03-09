package br.eti.arthurgregorio.shiroee.config;

import static br.eti.arthurgregorio.shiroee.config.Constants.AUTHENTICATED_OP;
import br.eti.arthurgregorio.shiroee.config.http.HttpSecurityBuilder;
import br.eti.arthurgregorio.shiroee.config.http.PermissionHttpSecurityBuilder;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
@TestInstance(Lifecycle.PER_CLASS)
public class HttpSecurityBuilderTest {

    private Map<String, String> rules;
    
    /**
     * 
     */
    @BeforeAll
    public void prepare() {
        
        final HttpSecurityBuilder builder = new PermissionHttpSecurityBuilder();

        builder.add("/secured/tools/user/**", "user:access", true)
                .add("/secured/tools/group/**", "group:access", true);
        
        this.rules = builder.build();
    }
    
    /**
     * 
     */
    @Test
    public void ruleHasBeenAdded() {
        assertFalse(this.rules.isEmpty());
    }
    
    /**
     * 
     */
    @Test
    public void rulesIsCorrect() {
        assertTrue(this.rules.get("/secured/tools/user/**")
                .contains("user:access"));
    }

    /**
     * 
     */
    @Test
    public void rulesRequireAuthentication() {
        assertTrue(this.rules.get("/secured/tools/user/**")
                .contains(AUTHENTICATED_OP));
    }
}
