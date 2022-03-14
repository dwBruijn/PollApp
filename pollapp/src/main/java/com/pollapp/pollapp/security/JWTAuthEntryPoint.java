package com.pollapp.pollapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pollapp.pollapp.payload.response.GenericAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthEntryPoint.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized: " + authException.getMessage());

        GenericAPIResponse apiResponse = new GenericAPIResponse();
        apiResponse.setStatus(HttpStatus.UNAUTHORIZED);
        apiResponse.setMessage("Login failed");

        ArrayList<String> errors = new ArrayList<String>();
        errors.add("Wrong username or password");
        apiResponse.setErrors(errors);

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().print(responseBody);
        response.flushBuffer();
    }
}
