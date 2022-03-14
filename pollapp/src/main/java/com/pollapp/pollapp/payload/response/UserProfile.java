package com.pollapp.pollapp.payload.response;

import java.time.Instant;

public class UserProfile {
    private Long id;
    private String name;
    private String username;
    private Instant createdAt;
    private Long pollCount;

    public UserProfile(Long id, String name, String username, Instant createdAt, Long pollCount) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.createdAt = createdAt;
        this.pollCount = pollCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPollCount() {
        return pollCount;
    }

    public void setPollCount(Long pollCount) {
        this.pollCount = pollCount;
    }
}
