package com.pollapp.pollapp.payload.response;

import java.util.List;

public class ListPollResponse {
    private List<PollResponse> content;
    int size;

    public ListPollResponse() {
    }

    public ListPollResponse(List<PollResponse> content) {
        this.content = content;
    }

    public ListPollResponse(List<PollResponse> content, int size) {
        this.content = content;
        this.size = size;
    }

    public List<PollResponse> getContent() {
        return content;
    }

    public void setContent(List<PollResponse> content) {
        this.content = content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
