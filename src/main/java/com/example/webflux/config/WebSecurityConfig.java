package com.example.webflux.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(CsrfSpec::disable)
                .formLogin(FormLoginSpec::disable)
                .httpBasic(HttpBasicSpec::disable)
                .exceptionHandling(this::configureExceptionHandling)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/login", "/favicon.ico").permitAll()
                        .pathMatchers("/controller").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .build();
    }

    private void configureExceptionHandling(ExceptionHandlingSpec exceptionHandlingSpec) {
        exceptionHandlingSpec
                .authenticationEntryPoint((serverWebExchange, authenticationException) ->
                        Mono.fromRunnable(() ->
                                serverWebExchange.getResponse()
                                        .setStatusCode(HttpStatus.UNAUTHORIZED)
                        )
                )
                .accessDeniedHandler((serverWebExchange, authenticationException) ->
                        Mono.fromRunnable(() ->
                                serverWebExchange.getResponse()
                                        .setStatusCode(HttpStatus.FORBIDDEN)
                        )
                );
    }
}
