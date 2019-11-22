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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({AuthProperties.class, FormsProperties.class})
@RequiredArgsConstructor
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProperties authProperties;

    private final FormsProperties formsProperties;

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
            .antMatchers(authProperties.getAllPublicRestEndpointPaths()).permitAll()
            // allow logged in users get profile details etc.
            .antMatchers(authProperties.getAllAuthenticatedRestEndpointPaths()).authenticated()
            // login/logout forms etc
            .antMatchers(formsProperties.getFormEndpointPaths()).permitAll()
            // registration form
            .antMatchers(formsProperties.getRegistrationEndpointPaths()).permitAll()
            // user-management is only allowed by ADMINS
            .antMatchers(authProperties.getApiRestEndpointPath()).hasRole(new AuthProperties().getRoleAdmin())
            .antMatchers(authProperties.getUserSearchRestEndpointPath()).authenticated()
            // secure all other api-endpoints
            .antMatchers(authProperties.getPrefix()+"/api/**").authenticated()
            .anyRequest().authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // allow also basic auth (optional)
        httpSecurity.httpBasic();

        // disable page caching templates
        httpSecurity.headers().cacheControl().disable();
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) {
        // needed when basic auth is also set and oauth (with header auth is used)
        web.ignoring().antMatchers(authProperties.getOauthRestEndpointPaths());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(1800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}