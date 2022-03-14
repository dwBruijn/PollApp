package com.pollapp.pollapp.util;

import com.pollapp.pollapp.model.Poll;
import com.pollapp.pollapp.model.User;
import com.pollapp.pollapp.payload.response.ChoiceResponse;
import com.pollapp.pollapp.payload.response.PollResponse;
import com.pollapp.pollapp.payload.response.UserBrief;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// used for mapping the Poll entity to a PollResponse payload which contains a bunch of information like Poll’s creator name, Vote counts of each choice in the Poll, the choice that the currently logged in user has voted for, is the Poll expired

public class ModelMapper {
    public static PollResponse mapPollToResponse(Poll poll, Map<Long, Long> choiceVotes, User creator, Long userVote) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreatedAt(poll.getCreatedAt());
        pollResponse.setEndsAt(poll.getEndDate());
        Instant now = Instant.now();
        pollResponse.setEnded(poll.getEndDate().isBefore(now));

        List<ChoiceResponse> choiceResponseList = poll.getChoices().stream().map(choice -> {
            ChoiceResponse response = new ChoiceResponse();
            response.setId(choice.getId());
            response.setText(choice.getText());

            if(choiceVotes.containsKey(choice.getId())) {
                response.setVoteCount(choiceVotes.get(choice.getId()));
            } else {
                response.setVoteCount(0);
            }

            return response;
        }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponseList);
        UserBrief userBrief = new UserBrief(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(userBrief);

        if(userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }

        Long totalVotes = pollResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);

        return pollResponse;
    }
}
