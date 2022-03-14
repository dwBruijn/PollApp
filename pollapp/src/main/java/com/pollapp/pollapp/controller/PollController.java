package com.pollapp.pollapp.controller;

import com.pollapp.pollapp.model.Poll;
import com.pollapp.pollapp.payload.request.PollRequest;
import com.pollapp.pollapp.payload.request.VoteRequest;
import com.pollapp.pollapp.payload.response.GenericAPIResponse;
import com.pollapp.pollapp.payload.response.ListPollResponse;
import com.pollapp.pollapp.payload.response.PollResponse;
import com.pollapp.pollapp.security.AuthenticatedUser;
import com.pollapp.pollapp.security.CurrentUser;
import com.pollapp.pollapp.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping
    public ListPollResponse getPolls(@CurrentUser AuthenticatedUser user) {
        return pollService.getAllPolls(user);
    }

    @PostMapping
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest request) {
        Poll poll = pollService.createPoll(request);

        return new ResponseEntity<GenericAPIResponse>(new GenericAPIResponse(HttpStatus.CREATED, "Poll created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@CurrentUser AuthenticatedUser user, @PathVariable Long pollId) {
        return pollService.getPollById(pollId, user);
    }

    @PostMapping("/{pollId}/votes")
    public PollResponse castVote(@CurrentUser AuthenticatedUser user,
                                 @PathVariable Long pollId,
                                 @Valid @RequestBody VoteRequest request) {
        return pollService.castVoteAndGetUpdatedPoll(pollId, request, user);
    }
}
