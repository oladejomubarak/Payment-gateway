package com.celertech.webpay.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpClientImplementation implements HttpClient {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Override
    public Response post(Map<String, String> headerList, String jsonPayload, String url) throws IOException {
        log.info("Making POST request with header {}, jsonPayload {} and url {}", headerList, jsonPayload, url);

        Request request = new Request.Builder().post(
                RequestBody.create(jsonPayload, MediaType.parse("application/json"))
        ).headers(Headers.of(headerList)).url(url).build();

        return okHttpClient.newCall(request).execute();
    }

    @Override
    @SneakyThrows
    public <T> T post(Map<String, String> headerList, String jsonPayload, String url, Class<T> t) {
        return
                responseToObject(post(headerList, jsonPayload, url), t);
    }

    private <T> T responseToObject(Response r, Class<T> t) {

        try {

            return toPojo(r.body().string(), t);
        } catch (Exception e) {
            log.info("--> Error converting response to object :: {}", e);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Response get(Map<String, String> headerList, Map<String, Object> params, String url) throws IOException {
        log.info("Making GET request with header {}, params {} and url {}", headerList, params, url);
        String queryString = params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String fullUrl = url + "?" + queryString;
        URL httpUrl = new URL(fullUrl);

        Request request = new Request.Builder().get().headers(Headers.of(headerList)).url(httpUrl).build();
        return okHttpClient.newCall(request).execute();
    }

    @Override
    public Response getNoParam(Map<String, String> headerList, String url) throws IOException {
        log.info("Making GET request with header {} and url {}", headerList, url);

        Request request = new Request.Builder().get().headers(Headers.of(headerList)).url(url).build();
        return okHttpClient.newCall(request).execute();
    }

    @Override
    @SneakyThrows
    public <T> T get(Map<String, String> headerList, Map<String, Object> params, String url, Class<T> t) {
        return responseToObject(get(headerList, params, url), t);
    }

    @Override
    public <T> T toPojo(final String o, Class<T> type) {
        try {
            return objectMapper.readValue(o, type);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("--> conversion of json  to object error {} ", e.getMessage());
            throw new RuntimeException("Error while parsing response");
        }
    }

    @Override
    public String toJson(Object src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            log.error("conversion to json string error :: {}", e);
            return "{}";
        }
    }
}
