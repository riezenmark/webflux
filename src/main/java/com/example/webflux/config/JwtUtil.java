package com.example.webflux.config;

import com.example.webflux.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expirationTimeInSeconds;

    public String extractUsername(String authToken) {
        return getClaimsFromToken(authToken).getSubject();
    }

    public boolean isTokenNotExpired(String authToken) {
        return getClaimsFromToken(authToken)
                .getExpiration()
                .after(new Date());
    }

    @SuppressWarnings("unchecked")
    public List<SimpleGrantedAuthority> getAuthorities(String authToken) {
        List<String> roles = getClaimsFromToken(authToken)
                .get("role", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", List.of(user.getRole()));

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + Long.parseLong(expirationTimeInSeconds) * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(hmacShaKeyFor(secret.getBytes(UTF_8)))
                .compact();
    }

    private Claims getClaimsFromToken(String authToken) {
        //String base64Key = Base64.getEncoder().encodeToString(secret.getBytes(UTF_8));
        //SecretKey secretKey = hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
        SecretKey secretKey = hmacShaKeyFor(secret.getBytes(UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }
}
