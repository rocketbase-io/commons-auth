package io.rocketbase.commons;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.filter.JwtAuthenticationTokenFilter;
import io.rocketbase.commons.security.TokenAuthenticationProvider;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.test.AppUserPersistenceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({AuthProperties.class})
@RequiredArgsConstructor
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProperties authProperties;

    @Resource
    private UserDetailsService userDetailsService;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(new TokenAuthenticationProvider())
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * a bit confusing but needed
     * https://github.com/spring-projects/spring-boot/issues/11136
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager authenticationManager = super.authenticationManagerBean();
        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public AppUserPersistenceService appUserPersistenceService() {
        return new AppUserPersistenceTestService();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity
            // we don't need CSRF because our token is invulnerable
            .csrf().disable()

            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

            // allow anonymous resource requests
            .antMatchers(HttpMethod.GET,
                    "/",
                    "/assets/**",
                    "/favicon.ico"
            ).permitAll()
            // configure auth endpoint
            .antMatchers(AuthProperties.getAllPublicRestEndpointPaths(authProperties.getPrefix())).permitAll()
            .antMatchers(FormsProperties.getAllPublicFormEndpointPaths(authProperties.getPrefix())).permitAll()
            .antMatchers(authProperties.getPrefix()+"/auth/me/**").authenticated()
            // user-management is only allowed by ADMINS
            .antMatchers(authProperties.getPrefix()+"/api/user/**").hasRole(new AuthProperties().getRoleAdmin())
            .antMatchers(authProperties.getPrefix()+"/api/user-search/**").authenticated()
            // secure all other api-endpoints
            .antMatchers(authProperties.getPrefix()+"/api/**").authenticated()
            .anyRequest().authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page cachingtemplates
        httpSecurity.headers().cacheControl().disable();
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS);
    }

}