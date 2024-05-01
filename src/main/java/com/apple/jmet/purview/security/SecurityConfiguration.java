package com.apple.jmet.purview.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.apple.ist.locksmith.oidc.LocksmithOidcAuthenticationFilter;

// @Configuration
// @ComponentScan(basePackages = "com.apple.ist.locksmith.oidc")
// @EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    // @Autowired
    // private CustomLocksmithOidcService customLocksmithOidcService;

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     LocksmithOidcAuthenticationFilter locksmithOidcAuthenticationFilter = new LocksmithOidcAuthenticationFilter();
    //     locksmithOidcAuthenticationFilter.setOidcService(customLocksmithOidcService);
    //     http
    //             .csrf(csrf -> csrf.disable())
    //             .addFilterBefore(locksmithOidcAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    //     return http.build();
    // }

    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     // return web -> web.ignoring().requestMatchers("/*/**");
    //     return web -> web.ignoring().requestMatchers("/actuator/**");
    // }

}
