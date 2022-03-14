package com.pollapp.pollapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// spring security has a filter chain
// a request is intercepted and each filter runs on that request to check for a particular use case
// this filter is registered in the filter chain, its job is to extract the JWT out of the request's header and to validate
// that token and then fetch the details of the user with id stored in the JWT's claims, then it sets that user as the authenticated user.

public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private UserServiceImpl userService;

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    // extract and return JWT from Authorization header of the received request
    private String fetchJWTFromRequest(HttpServletRequest request) {
        String tokenBearer = request.getHeader("Authorization");

        if(tokenBearer != null && !tokenBearer.isEmpty() && tokenBearer.startsWith("Bearer ")) {
            return tokenBearer.substring(7, tokenBearer.length());
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = fetchJWTFromRequest(request);
            if(token != null && !token.isEmpty() && jwtProvider.validateJWT(token)) {
                // extract user id from JWT claims
                Long uid = jwtProvider.getUserIdFromJWT(token);

                // set that user id on the spring security context
                UserDetails userDetails = userService.loadUserById(uid);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch(Exception e) {
            logger.error("Failed to set user authentication in security context");
        }

        filterChain.doFilter(request, response);
    }
}
