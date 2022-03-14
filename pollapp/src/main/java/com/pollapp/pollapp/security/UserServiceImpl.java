package com.pollapp.pollapp.security;

import com.pollapp.pollapp.exception.GenericResourceNotFoundException;
import com.pollapp.pollapp.model.User;

import com.pollapp.pollapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    // used by Spring Security
    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        // allow the user to login using either their username or email
        User user = userRepository.findByUsernameOrEmail(uname, uname).orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + uname));

        return AuthenticatedUser.create(user);
    }

    // used by JWTAuthFilter
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new GenericResourceNotFoundException("user", "id", id)
        );

        return AuthenticatedUser.create(user);
    }
}
