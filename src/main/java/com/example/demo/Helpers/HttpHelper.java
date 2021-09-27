package com.example.demo.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpHelper {
    private static HttpHelper instance;

    public static HttpHelper getInstance(){
        if(instance == null) instance = new HttpHelper();
        return instance;
    }

    private HttpEntity buildBodyFromMap(HashMap<Object, Object> data) throws JsonProcessingException {
        String jsonString = Helper.getInstance().mapToJsonString(data);

        log.info(jsonString);

        return new StringEntity(jsonString, ContentType.APPLICATION_JSON);
    }

    public void makePost(String url, HashMap<Object, Object> requestBody, HashMap<Object, Object> headers) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        HttpEntity entity = buildBodyFromMap(requestBody);
        request.setEntity(entity);

        for(var entry : headers.entrySet()){
            request.addHeader(entry.getKey().toString(), entry.getValue().toString());
        }

        for(var item : request.getAllHeaders()){
            log.info(item.toString());
        }

        CloseableHttpResponse response = httpclient.execute(request);
        log.info(response.toString());
    }
}
