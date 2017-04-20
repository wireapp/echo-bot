package com.wire.bots.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    @JsonProperty("body")
    public String body;

    @JsonProperty("user")
    public User user;
}
