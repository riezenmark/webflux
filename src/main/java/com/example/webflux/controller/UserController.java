package com.example.webflux.controller;

import com.example.webflux.config.JwtUtil;
import com.example.webflux.domain.User;
import com.example.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {
    private static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(credentials ->
                        userService.findByUsername(credentials.getFirst("username"))
                                .cast(User.class)
                                .map(userDetails ->
                                        passwordEncoder.matches(
                                                credentials.getFirst("password"),
                                                userDetails.getPassword()
                                        )
                                                ? ResponseEntity.ok(jwtUtil.generateToken(userDetails))
                                                : UNAUTHORIZED
                                )
                                .defaultIfEmpty(UNAUTHORIZED)
                );
    }
}
