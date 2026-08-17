package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.error.ErrorMapper;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpResponse;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.serialization.JsonCodec;

/**
 * Base comum de todo {@code *Resource} — execução de request + desserialização + mapeamento de erro
 * centralizados, para nenhum resource duplicar essa lógica (mesmo espírito de
 * {@code ErrorMapper}/{@code JsonCodec} únicos).
 */
public abstract class ApiResourceSupport {

    protected final HttpTransport transport;

    protected ApiResourceSupport(HttpTransport transport) {
        this.transport = transport;
    }

    protected <T> T execute(HttpRequest request, Class<T> responseType) {
        HttpResponse response = transport.send(request);
        return decode(response, responseType);
    }

    protected <T> T execute(HttpRequest request, TypeReference<T> responseType) {
        HttpResponse response = transport.send(request);
        return decode(response, responseType);
    }

    protected void executeNoContent(HttpRequest request) {
        HttpResponse response = transport.send(request);
        if (response.status() >= 400) {
            throw ErrorMapper.map(response);
        }
    }

    private <T> T decode(HttpResponse response, Class<T> type) {
        if (response.status() >= 400) {
            throw ErrorMapper.map(response);
        }
        if (response.body() == null || response.body().isBlank()) {
            return null;
        }
        try {
            return JsonCodec.mapper().readValue(response.body(), type);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao desserializar resposta real da API: " + e.getMessage(), e);
        }
    }

    private <T> T decode(HttpResponse response, TypeReference<T> type) {
        if (response.status() >= 400) {
            throw ErrorMapper.map(response);
        }
        if (response.body() == null || response.body().isBlank()) {
            return null;
        }
        try {
            return JsonCodec.mapper().readValue(response.body(), type);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao desserializar resposta real da API: " + e.getMessage(), e);
        }
    }
}
