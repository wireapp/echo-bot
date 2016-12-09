package com.wire.bots.propeller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 *
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class ImageRecognitionClient {

    private ObjectMapper objectMapper;
    private CloseableHttpClient client;
    private final static int SO_TIMEOUT = 60;
    private final String apiUrl = "http://104.196.212.163:8000/api/classify/image";

    public ImageRecognitionClient() {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SO_TIMEOUT).build();
	HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
	((BasicHttpClientConnectionManager) connectionManager).setSocketConfig(socketConfig);
	PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setSocketConfig(new HttpHost("104.196.212.163", 8000), socketConfig);
        client = HttpClients.custom().setConnectionManager(cm).build();
        objectMapper = new ObjectMapper();
    }

    public ImageClassificationResponse doPost (ImageClassificationRequest request) throws Exception {
        HttpPost httpRequest = new HttpPost();
        httpRequest.setURI(new URI(this.apiUrl));
        String requestBody = objectMapper.writeValueAsString(request);
        httpRequest.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse response = client.execute(httpRequest);
        ImageClassificationResponse classification =
                objectMapper.readValue(response.getEntity().getContent(), ImageClassificationResponse.class);
        response.getEntity().getContent().close();
        response.close();

        return classification;
    }
}
