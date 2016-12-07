package com.wire.bots.propeller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class ImageRecognitionClient {
    
    private ObjectMapper objectMapper;
    private CloseableHttpClient client;
    private final static int SO_TIMEOUT = 60 * 1000;
    private final String apiUrl = "http://104.196.212.163:8000/api/classify/image";

    public ImageRecognitionClient() {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SO_TIMEOUT).build();
        HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
        ((BasicHttpClientConnectionManager) connectionManager).setSocketConfig(socketConfig);
        client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        objectMapper = new ObjectMapper();
    }
    
    public ImageClassificationResponse doPost (ImageClassificationRequest request) throws URISyntaxException {
        HttpPost httpRequest = new HttpPost();
        httpRequest.setURI(new URI(this.apiUrl));
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
            httpRequest.setEntity(new StringEntity(requestBody));
            HttpResponse response = client.execute(httpRequest);

            return objectMapper.readValue(response.getEntity().getContent(), ImageClassificationResponse.class);
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
