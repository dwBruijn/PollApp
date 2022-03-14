package com.pollapp.pollapp.controller;

import com.pollapp.pollapp.exception.GenericResourceNotFoundException;
import com.pollapp.pollapp.model.User;
import com.pollapp.pollapp.payload.response.*;
import com.pollapp.pollapp.repository.PollRepository;
import com.pollapp.pollapp.repository.UserRepository;
import com.pollapp.pollapp.security.AuthenticatedUser;
import com.pollapp.pollapp.security.CurrentUser;
import com.pollapp.pollapp.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollService pollService;

    @GetMapping("/user/me")
    public UserBrief getAuthenticatedUser(@CurrentUser AuthenticatedUser user) {
        return new UserBrief(user.getId(), user.getUsername(), user.getName());
    }

    @GetMapping("/user/checkUsernameAvailability/{username}")
    public UserAvailability checkUsernameAvailability(@PathVariable(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);

        return new UserAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new GenericResourceNotFoundException("User", "username", username));

        long pollCount = pollRepository.countByCreatedBy(user.getId());

        return new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount);
    }

    @GetMapping("/users/{username}/polls")
    public ListPollResponse getPollsCreatedBy(@PathVariable(value = "username") String username,
                                              @CurrentUser AuthenticatedUser user) {
        return pollService.getPollsCreatedBy(username, user);
    }
}
