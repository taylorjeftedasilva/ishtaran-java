package com.ishtaran.sdk.http;

import java.util.List;
import java.util.Map;

public record HttpResponse(int status, Map<String, List<String>> headers, String body) {

    public String header(String name) {
        if (headers == null) {
            return null;
        }
        for (var entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)
                    && entry.getValue() != null && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
}
