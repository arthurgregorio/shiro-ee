package br.eti.arthurgregorio.shiroee.config;

import javax.servlet.annotation.WebFilter;
import lombok.NoArgsConstructor;
import org.apache.shiro.web.servlet.ShiroFilter;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@WebFilter("/*")
@NoArgsConstructor
public class SecurityFilterActivator extends ShiroFilter { }
