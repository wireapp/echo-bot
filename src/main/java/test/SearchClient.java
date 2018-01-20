package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

class SearchClient {
    private final Client client;
    private final String httpUrl;
    private final String token;

    SearchClient(String token) {
        this.token = token;
        String env = System.getProperty("env", "prod");
        String domain = env.equals("prod") ? "wire.com" : "zinfra.io"; //fixme: remove zinfra
        httpUrl = String.format("https://%s-nginz-https.%s", env, domain);

        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        client = JerseyClientBuilder.createClient(cfg);
    }

    Result search(String tags, String start) throws IOException {
        Response response = client.target(httpUrl).
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
