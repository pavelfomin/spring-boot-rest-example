package com.droidablebee.springboot.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${app.security.ignore:/swagger/**, /swagger-resources/**, /swagger-ui/**, /swagger-ui.html, /webjars/**, /v3/api-docs/**, /actuator/info}")
    private String[] ignorePatterns;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(customizer -> customizer
                //make sure principal is created for the health endpoint to verify the role
                .requestMatchers(new AntPathRequestMatcher("/actuator/health"))
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .oauth2ResourceServer((configurer) -> configurer.jwt(Customizer.withDefaults()))
            .sessionManagement((s) -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers(ignorePatterns);
    }
}
