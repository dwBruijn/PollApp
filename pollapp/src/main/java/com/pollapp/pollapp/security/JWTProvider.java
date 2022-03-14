package com.pollapp.pollapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTProvider {
    private static final Logger logger = LoggerFactory.getLogger(JWTProvider.class);

    // secret string to generate JWTs
    private final String secret = "lake0aGp3N9U79nPDMXGr8JC/Ah4Y5qc785wuNL61hjhuqgSwGDCIR6fHIGccmo9W79blj4wn/Ms5QVue8eZ5Kg1vvbjdfHIH0v4ViqQSwPcvh7qZKLLsyd0wGaceJ3CajAczhfuJTjRfUNo08bHM4yZbTxA6CnPXN6CdVy9zGWQ3rPYpycRB66VDEL8KAQ+231ZemADSDzUa5sHZkl1+c1o1E0B966sk3mYQw==";

    // 24 hours
    private final int expirationInMs = 86400000;

    public String generateJWT(Authentication authentication) {
        // get currently authenticated user's info
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Date d = new Date();
        Date expiry = new Date(d.getTime() + expirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(authenticatedUser.getId()))
                .setIssuedAt(d)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Long getUserIdFromJWT(String JWT) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(JWT)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateJWT(String JWT) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(JWT);

            return true;
        } catch(SignatureException e) {
            logger.error("invalid signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid format");
        } catch(ExpiredJwtException e) {
            logger.error("Expired token");
        } catch(Exception e) {
            logger.error("Exception while parsing JWT");
        }

        return false;
    }
}
