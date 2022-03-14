package com.pollapp.pollapp.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public class PollResponse {
    private Long id;
    private String question;
    private List<ChoiceResponse> choices;
    private UserBrief createdBy;
    private Instant createdAt;
    private Instant endsAt;
    private Boolean ended;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long eelectedChoice;
    private Long totalVotes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChoiceResponse> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceResponse> choices) {
        this.choices = choices;
    }

    public UserBrief getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserBrief createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public Boolean getEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public Long getSelectedChoice() {
        return eelectedChoice;
    }

    public void setSelectedChoice(Long eelectedChoice) {
        this.eelectedChoice = eelectedChoice;
    }

    public Long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Long totalVotes) {
        this.totalVotes = totalVotes;
    }
}
