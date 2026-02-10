package com.HTT.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "sdkpoLjgsJKdOF23SDs0400sdl=GGstwwbxB,/sggw";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities, long expiryMinutes) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles) 
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiryMinutes * 60 * 1000)))
                .signWith(key)
                .compact();
    }

    public String validateAndExtractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            System.err.println("JWT Validation Error: Token has expired - " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("JWT Validation Error: Signature is invalid - " + e.getMessage());
        } catch (Exception e) {
            // Catch any other JWT-related exception
            System.err.println("JWT Validation Error: " + e.getMessage());
        }
        return null;
    }
}
