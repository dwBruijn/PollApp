package com.pollapp.pollapp.controller;

import com.pollapp.pollapp.model.User;
import com.pollapp.pollapp.payload.request.LoginRequest;
import com.pollapp.pollapp.payload.request.SignupRequest;
import com.pollapp.pollapp.payload.response.GenericAPIResponse;
import com.pollapp.pollapp.payload.response.JWTAuthResponse;
import com.pollapp.pollapp.repository.UserRepository;
import com.pollapp.pollapp.security.JWTProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = jwtProvider.generateJWT(auth);

        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<GenericAPIResponse>(new GenericAPIResponse(HttpStatus.BAD_REQUEST, "Email is unavailable"), HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            return new ResponseEntity<GenericAPIResponse>(new GenericAPIResponse(HttpStatus.BAD_REQUEST, "Username is unavailable"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.getName(), request.getUsername(), request.getEmail(), request.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);

        return new ResponseEntity<GenericAPIResponse>(new GenericAPIResponse(HttpStatus.CREATED, "User created successfully"), HttpStatus.CREATED);
    }
}
