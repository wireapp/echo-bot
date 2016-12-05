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

/**
 *
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class ImageRecognitionClient {
    
    private ObjectMapper objectMapper;
    private CloseableHttpClient client;
    private final static int SO_TIMEOUT = 60 * 1000;

    public ImageRecognitionClient() {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SO_TIMEOUT).build();
        HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
        ((BasicHttpClientConnectionManager) connectionManager).setSocketConfig(socketConfig);
        client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        objectMapper = new ObjectMapper();
    }
    
    public ImageClassificationResponse doPost (URI uri, ImageClassificationRequest request){
        HttpPost httpRequest = new HttpPost();
        httpRequest.setURI(uri);
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
