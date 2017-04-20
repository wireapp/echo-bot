package com.wire.bots.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPullRequest {
    @JsonProperty("action")
    public String action;

    @JsonProperty("pull_request")
    public PullRequest pr;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        @JsonProperty("html_url")
        public String url;

        @JsonProperty("title")
        public String title;

        @JsonProperty("user")
        public User user;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class User {
            @JsonProperty("avatar_url")
            public String avatarUrl;
        }
    }

}
