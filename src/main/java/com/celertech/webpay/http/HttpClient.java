package com.celertech.webpay.http;

import lombok.SneakyThrows;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public interface HttpClient {
    Response post(Map<String, String> headerList, String jsonPayload, String url) throws IOException;

    @SneakyThrows
    <T> T post(Map<String, String> headerList, String jsonPayload, String url, Class<T> tClass);

    Response get(Map<String, String> headerList, Map<String, Object> params, String url) throws IOException;


    Response getNoParam(Map<String, String> headerList, String url) throws IOException;

    @SneakyThrows
    <T> T get(Map<String, String> headerList, Map<String, Object> params, String url, Class<T> t);

    <T> T toPojo(String json, Class<T> type);

    String toJson(Object src);
}
