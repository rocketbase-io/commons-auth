# commons-auth

![logo](assets/commons-logo.svg)

[![Build Status](https://travis-ci.org/rocketbase-io/commons-auth.svg?branch=master)](https://travis-ci.org/rocketbase-io/commons-auth)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-auth/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-auth)

Add the missing auth service to your spring-boot applications. We [@rocketbase.io](https://www.rocketbase.io) develop many microservices and tried many tools and projects. All of them didn't matched our needs or were too enterprise for smaller projects .

The implementation bases on spring-boot: mainly on **spring-mvc**, **spring-data**, **javax.mail** and **jjwt**

**Features:**
* authentication with jwt-tokens
* refresh token flow
* registration + verification flow
* optional gravatar integration
* optional key values pairs to hold custom properties to user
* password forgot/reset flow
* admin endpoints to crud users

## commons-auth-api

This module provides the DTOs and a client to communicate with the authentication endpoints.

## commons-auth-core

Containing an implementation for Token-Generators, UserManagement, Filters and many more...

### configuration properties

You can configure the behaviour of the service by following properties

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.role-admin                | ADMIN           |                                                              |
| auth.role-user                 | USER            |                                                              |
| auth.token-secret              | *fixed 32chars* | **should get changed for production**<br>used to generate reset + verification tokens |
| auth.use-cache-time            | 30              | time in minutes - 0 means disabled                           |
| auth.verification-url          | null            | full qualified url to a custom UI that proceed the verification<br />?verification=VALUE will get append |
| auth.password-reset-url        | Null            | full qualified url to a custom UI that proceed the password reset<br />?verification=VALUE will get append |
| auth.password-reset-expiration | 60              | time in minutes - after this period the token is invalid     |

For handling the JWT-Tokens and it's expirations you can use these properties:

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.jwt.header                | Authorization   | standard approach                                            |
| auth.jwt.token-prefix          | Bearer          | standard approach with " " at the end                        |
| auth.jwt.uri-param          | token          | token could also get provided via url-param in case of downloads etc.                        |
| auth.jwt.secet          | **required**          | a base64 encoded jwt secret |
| auth.jwt.access-token-expiration          | 60          | time in minutes |
| auth.jwt.refresh-token-expiration          | 43200          | time in minutes - default means 30 days |

The service contains also an registration flow that is by default enabled


| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.registration.enabled          | true          | allow users to register |
| auth.registration.verification          | true          | registered used needs to verify their email |
| auth.registration.verification-expiration          | 1440          | time in minutes - default means 1 day |
| auth.registration.role          | USER          | role of user after registration |

Gravatar is been used by default to fetch an avatar if nothing is provided.

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.gravatar.enabled          | true          | should the avatar been initially loaded via gravatar service |
| auth.gravatar.size          | 160          | size of image |
| auth.gravatar.image          | RETRO          | type of placeholder style<br />https://gravatar.com/site/implement/images/ |
| auth.gravatar.rating          | null          | filter for spefic rating<br />https://gravatar.com/site/implement/images/ |

The Content of the emails (forgot-password + registration-verification) is been highly configurable. Furthermore a simple EmailTempalte engine is implemented that could also be used for other purposes.


| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.email.subject-prefix          | [Auth]          | prefix of email subject |
| auth.email.service-name          | commons-auth          | will get displayed in email-text |
| auth.email.support-email          | support@localhost          | will get displayed in email-text |
| auth.email.from-email          | no-reply@localhost          | sender of emails |
| auth.email.copyright-name          | commons-auth          | name of sender |
| auth.email.copyright-url          | link to github repro          | will get displayed in email-text |

To send emails the is used. This needs also some configurations:

| property                                         | default    |
| ------------------------------------------------ | ---------- |
| spring.mail.host                                 | *required* |
| spring.mail.port                                 | *required* |
| spring.mail.username                             | *required* |
| spring.mail.password                             | *required* |
| spring.mail.properties.mail.smtp.auth            | *required* |
| spring.mail.properties.mail.smtp.starttls.enable | *required* |



### configure spring-security

Apart from the configuration properties to get it running you need to configure and activate the security filter etc. Here you can find an example:

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
        // @formatter:off
        httpSecurity
            // activate CorsConfigurationSource
            .cors().and()
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
            .antMatchers("/auth/login", "/auth/refresh").permitAll()
            .antMatchers("/auth/me/**").authenticated()
            // user-management is only allowed by ADMINS
            .antMatchers("/api/user/**").hasRole("ADMIN")
            // secure all other api-endpoints
            .antMatchers("/api/**").authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page cachingtemplates
        httpSecurity.headers().cacheControl().disable();
        // @formatter:on
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

Furthermore you need to add the package "io.rocketbase.commons" to your componentscan - otherwise all  provided beans will not get found

```java
@ComponentScan(basePackages = {"io.rocketbase.commons", "YOUR_PACKAGE"})
```

## commons-auth-mongo

Containing the persistence layer for user via spring-data-mongo

Will create on collection with name of **user**

## commons-auth-jpa

Containing the persistence layer for user via jpa

Will create 3 tables: **USER**, **USER_ROLES**, **USER_KEYVALUE_PAIRS**

In order to get it running you need to add the following annotations to your project, so that jpa detects also the provided entites and repositories...

```java
@EnableJpaRepositories(basePackages = {"io.rocketbase.commons", "YOUR_PACKAGE"})
@EntityScan({"io.rocketbase.commons", "YOUR_PACKAGE"})
```



### The MIT License (MIT)
Copyright (c) 2018 rocketbase.io

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.