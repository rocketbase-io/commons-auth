package org.company.sample.config;

import io.rocketbase.commons.api.LoginApi;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.filter.JwtAuthenticationTokenFilter;
import io.rocketbase.commons.filter.LoginCookieFilter;
import io.rocketbase.commons.handler.LoginSuccessCookieHandler;
import io.rocketbase.commons.handler.LogoutCookieHandler;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.security.LoginAuthenticationProvider;
import io.rocketbase.commons.security.TokenAuthenticationProvider;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
import io.rocketbase.commons.service.token.AuthorizationCodeService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import io.rocketbase.commons.util.JwtTokenStoreService;
import io.rocketbase.commons.vaadin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({AuthProperties.class, FormsProperties.class})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final AuthProperties authProperties;

    private final FormsProperties formsProperties;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private LoginApi loginApi;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private AuthorizationCodeService authorizationCodeService;

    @Autowired(required = false)
    private CustomAuthoritiesProvider customAuthoritiesProvider;

    @Bean
    public JwtTokenStoreProvider jwtTokenStoreProvider() {
        return jwtTokenBundle -> new JwtTokenStoreService(jwtTokenBundle, jwtTokenService, appUserTokenService);
    }

    @Bean
    public LoginCookieFilter loginCookieFilter() {
        return new LoginCookieFilter(loginApi, jwtTokenService, customAuthoritiesProvider, jwtTokenStoreProvider());
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(new LoginAuthenticationProvider(loginApi, jwtTokenService, jwtTokenStoreProvider()))
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
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        SecurityUtils.configure(httpSecurity);

        // @formatter:off
        httpSecurity
                // activate CorsConfigurationSource
                .cors().and()
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                // login
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/app")
                .successHandler(new LoginSuccessCookieHandler(authorizationCodeService))
                .permitAll()
                .and()
                // logout
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(new LogoutCookieHandler())
                .permitAll()
                .and()
                // normal requests
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                // allow anonymous resource requests
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/assets/**",
                        "/images/**",
                        "/static/favicon.ico",
                        "/favicon.ico"
                ).permitAll()
                // configure auth endpoint
                .antMatchers(authProperties.getAllPublicRestEndpointPaths()).permitAll()
                // allow logged in users get profile details etc.
                .antMatchers(authProperties.getAllAuthenticatedRestEndpointPaths()).authenticated()
                // login/logout, forgot, reset-password forms etc
                .antMatchers(formsProperties.getFormEndpointPaths()).permitAll()
                // registration form
                .antMatchers(formsProperties.getRegistrationEndpointPaths()).permitAll()
                // invite form
                .antMatchers(formsProperties.getInviteEndpointPaths()).permitAll()
                // user-management is only allowed by ADMINS
                .antMatchers(authProperties.getApiRestEndpointPaths()).hasAuthority("api.crud")
                .antMatchers(authProperties.getApiInviteRestEndpointPaths()).hasAuthority("invite")
                .antMatchers(authProperties.getUserSearchRestEndpointPaths()).authenticated()
                // secure all other api-endpoints
                .antMatchers(authProperties.getPrefix() + "/api/**").authenticated()
                .anyRequest().authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(loginCookieFilter(), UsernamePasswordAuthenticationFilter.class);
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
        SecurityUtils.configure(web);

        // needed when basic auth is also set and oauth (with header auth is used)
        web.ignoring()
                .antMatchers(HttpMethod.GET, "/actuator/health");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .maxAge(600L);
    }
}