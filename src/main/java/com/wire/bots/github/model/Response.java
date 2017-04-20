package com.wire.bots.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonProperty("action")
    public String action;

    @JsonProperty("pull_request")
    public PullRequest pr;

    @JsonProperty("repository")
    public Repository repository;
}
