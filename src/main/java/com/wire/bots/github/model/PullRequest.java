package com.wire.bots.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
        @JsonProperty("html_url")
        public String url;

        @JsonProperty("title")
        public String title;

        @JsonProperty("user")
        public User user;

        @JsonProperty("merged")
        public Boolean merged;
}
