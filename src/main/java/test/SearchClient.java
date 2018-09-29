package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wire.bots.sdk.tools.Util;
import com.wire.bots.sdk.user.TrustedTlsClientBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

class SearchClient {
    private final String token;
    private final WebTarget target;

    SearchClient(String token) {
        this.token = token;

        target = TrustedTlsClientBuilder.build().target(Util.getHost());
    }

    Result search(String tags, String start) throws IOException {
        Response response = target.
                path("services").
                queryParam("tags", tags).
                queryParam("start", start).
                request(MediaType.APPLICATION_JSON).
                header("Authorization", "Bearer " + token).
                get();

        if (response.getStatus() != 200) {
            throw new IOException(response.readEntity(String.class));
        }

        return response.readEntity(Result.class);
    }

    Result search(UUID teamId, String prefix) throws IOException {
        Response response = target.
                path("teams").
                path(teamId.toString()).
                path("services").
                path("whitelisted").
                queryParam("prefix", prefix).
                request(MediaType.APPLICATION_JSON).
                header("Authorization", "Bearer " + token).
                get();

        if (response.getStatus() != 200) {
            throw new IOException(response.readEntity(String.class));
        }

        return response.readEntity(Result.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("has_more")
        public boolean hasMore;
        @JsonProperty
        public ArrayList<Service> services;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Service {
        @JsonProperty
        public String name;
        @JsonProperty
        public String description;
        @JsonProperty
        public String summary;
        @JsonProperty
        public String[] tags;
        @JsonProperty("id")
        public String serviceId;
        @JsonProperty("provider")
        public String providerId;
        @JsonProperty
        public boolean enabled;
    }
}
