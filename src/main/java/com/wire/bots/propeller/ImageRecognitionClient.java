package com.wire.bots.propeller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import java.net.URI;
import java.util.concurrent.TimeUnit;

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
        HttpParams params = new BasicHttpParams();
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8000));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, registry);
        cm.setDefaultMaxPerRoute(10);
        cm.setMaxTotal(100);
        cm.closeIdleConnections(SO_TIMEOUT, TimeUnit.SECONDS);
        CloseableHttpClient client = new DefaultHttpClient(cm, params);
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
