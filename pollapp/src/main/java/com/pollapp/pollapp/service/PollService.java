package com.pollapp.pollapp.service;

import com.pollapp.pollapp.exception.BadRequestException;
import com.pollapp.pollapp.exception.GenericResourceNotFoundException;
import com.pollapp.pollapp.model.*;
import com.pollapp.pollapp.payload.request.PollRequest;
import com.pollapp.pollapp.payload.request.VoteRequest;
import com.pollapp.pollapp.payload.response.ListPollResponse;
import com.pollapp.pollapp.payload.response.PollResponse;
import com.pollapp.pollapp.repository.PollRepository;
import com.pollapp.pollapp.repository.UserRepository;
import com.pollapp.pollapp.repository.VoteRepository;
import com.pollapp.pollapp.security.AuthenticatedUser;
import com.pollapp.pollapp.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    public ListPollResponse getAllPolls(AuthenticatedUser user) {
        // get all polls from newest to oldest
        List<Poll> polls = pollRepository.findAllByOrderByCreatedAtDesc();

        if(polls.size() == 0) {
            return new ListPollResponse();
        }

        // generate a list of all poll ids
        List<Long> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());
        // generate a map of choiceId to voteCount for every poll in pollIds
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        // generate a map of pollId to choiceId voted on by user for every poll in pollIds
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(user, pollIds);
        // generate a map of userId to User for every user that created at least 1 poll in list polls
        Map<Long, User> creatorMap = getPollCreatorMap(polls);

        // generate a list of PollResponse for every poll in polls list
        List<PollResponse> pollResponses = polls.stream().map(poll -> {
            return ModelMapper.mapPollToResponse(poll,
                    choiceVoteCountMap,
                    creatorMap.get(poll.getCreatedBy()),
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null));
        }).collect(Collectors.toList());

        return new ListPollResponse(pollResponses, pollResponses.size());
    }

    public PollResponse getPollById(Long pollId, AuthenticatedUser user) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(
                () -> new GenericResourceNotFoundException("Poll", "id", pollId));

        // retrieve vote count of every choice belonging to the current poll
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        // generate a map of choiceId to voteCount for every choice in votes
        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        // get poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new GenericResourceNotFoundException("User", "id", poll.getCreatedBy()));

        // get vote casted by user on given pollId
        Vote userVote = null;
        if(user != null) {
            userVote = voteRepository.findByUserIdAndPollId(user.getId(), pollId);
        }

        return ModelMapper.mapPollToResponse(poll, choiceVotesMap,
                creator, userVote != null ? userVote.getChoice().getId(): null);
    }

    public ListPollResponse getPollsCreatedBy(String username, AuthenticatedUser currentUser) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GenericResourceNotFoundException("User", "username", username));

        // retrieve all polls created by the given username
        List<Poll> polls = pollRepository.findByCreatedByOrderByCreatedAtDesc(user.getId());

        if (polls.size() == 0) {
            return new ListPollResponse();
        }

        // generate a list of all poll ids created by user
        List<Long> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());
        // generate a map of choiceId to voteCount for every poll in pollIds
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        // generate a map of pollId to choiceId voted on by currentUser for every poll in pollIds
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);

        // generate a list of PollResponse for every poll in polls list
        List<PollResponse> pollResponses = polls.stream().map(poll -> {
            return ModelMapper.mapPollToResponse(poll,
                    choiceVoteCountMap,
                    user,
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null));
        }).collect(Collectors.toList());

        return new ListPollResponse(pollResponses, pollResponses.size());
    }

    public Poll createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoices().forEach(choiceRequest -> {
            poll.addChoice(new Choice(choiceRequest.getText()));
        });

        Instant now = Instant.now();
        Instant endDate = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
                .plus(Duration.ofHours(pollRequest.getPollLength().getHours()));

        poll.setEndDate(endDate);

        return pollRepository.save(poll);
    }

    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, AuthenticatedUser currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new GenericResourceNotFoundException("Poll", "id", pollId));

        // trying to vote on an expired poll
        if(poll.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This Poll has already ended.");
        }

        User user = userRepository.getOne(currentUser.getId());

        // extract choiceId from the received VoteRequest
        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new GenericResourceNotFoundException("Choice", "id", voteRequest.getChoiceId()));

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(user);
        vote.setChoice(selectedChoice);

        try {
            vote = voteRepository.save(vote);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Sorry! You have already cast your vote in this poll");
        }

        // generate updated poll and send it back as PollResponse

        // retrieve vote count of every choice this poll
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        // retrieve poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new GenericResourceNotFoundException("User", "id", poll.getCreatedBy()));

        return ModelMapper.mapPollToResponse(poll, choiceVotesMap, creator, vote.getChoice().getId());
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        // Retrieve vote count of every choice belonging to every poll in the given pollIds
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds);

        // map choiceId to voteCount
        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        return choiceVotesMap;
    }

    private Map<Long, Long> getPollUserVoteMap(AuthenticatedUser user, List<Long> pollIds) {
        // Retrieve Votes done by the logged in user to the given pollIds
        Map<Long, Long> pollUserVoteMap = null;

        if(user != null) {
            // generate a list of Votes casted by user on polls in pollIds list
            List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(user.getId(), pollIds);

            // map pollId to choiceId
            // this will generate a mapping between pollId and the choiceId voted on by user
            pollUserVoteMap = userVotes.stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
        }
        return pollUserVoteMap;
    }

    Map<Long, User> getPollCreatorMap(List<Poll> polls) {
        // get Poll creator details for every poll in list polls
        // generate list of userIds who have created at least one poll in list polls
        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        // generate a list of entity User which contains details of every user in creatorIds
        List<User> creators = userRepository.findByIdIn(creatorIds);

        // map userId to User
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }
}
