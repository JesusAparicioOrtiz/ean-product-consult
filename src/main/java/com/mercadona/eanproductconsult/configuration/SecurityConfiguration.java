package com.mercadona.eanproductconsult.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"),
                    AntPathRequestMatcher.antMatcher("/actuator/**"),
                    AntPathRequestMatcher.antMatcher("/api/v1/**")).permitAll()
                    .anyRequest().denyAll()
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"),
                AntPathRequestMatcher.antMatcher("/api/v1/**"))
            );

        return http.build();
    }
}
