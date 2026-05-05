package com.example.ecommerce.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static <T> T fromJson(HttpServletRequest request, Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(request.getInputStream(), valueType);
    }

    public static void sendJson(HttpServletResponse response, Object value, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        OBJECT_MAPPER.writeValue(response.getOutputStream(), value);
    }

    public static void sendJson(HttpServletResponse response, Object value) throws IOException {
        sendJson(response, value, HttpServletResponse.SC_OK);
    }

    public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        OBJECT_MAPPER.writeValue(response.getOutputStream(), Map.of("error", message));
    }
}
