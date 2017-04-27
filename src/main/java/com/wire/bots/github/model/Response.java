package com.wire.bots.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonProperty("action")
    public String action;

    @JsonProperty("pull_request")
    public PullRequest pr;

    @JsonProperty("comment")
    public Comment comment;

    @JsonProperty("issue")
    public Issue issue;

    @JsonProperty("commits")
    public List<Commit> commits;

    @JsonProperty("sender")
    public User sender;

    @JsonProperty("compare")
    public String compare;

    @JsonProperty("repository")
    public Repository repository;
}
