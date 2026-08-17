package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.AssetNetworkResponse;
import com.ishtaran.sdk.model.controlplane.AssetResponse;
import com.ishtaran.sdk.model.controlplane.NetworkResponse;
import com.ishtaran.sdk.model.enums.AssetNetworkStatus;

import java.util.List;
import java.util.UUID;

/**
 * Catálogo — {@code AssetNetworkCatalog} (6 rotas reais, só leitura no escopo do SDK — as 5 rotas
 * de mutação são exclusivas de Platform Owner, fora de escopo). Sempre Member JWT — não aceita API
 * Key hoje (Known Gap real, ver SDK_CAPABILITY_SPEC.md §12.3).
 */
public final class AssetNetworkCatalogResource extends ApiResourceSupport {

    public AssetNetworkCatalogResource(HttpTransport transport) {
        super(transport);
    }

    public List<AssetResponse> listAssets() {
        return execute(HttpRequest.get("/v1/assets"), new TypeReference<List<AssetResponse>>() {
        });
    }

    public AssetResponse getAsset(UUID assetId) {
        return execute(HttpRequest.get("/v1/assets/" + assetId), AssetResponse.class);
    }

    public List<NetworkResponse> listNetworks() {
        return execute(HttpRequest.get("/v1/networks"), new TypeReference<List<NetworkResponse>>() {
        });
    }

    public NetworkResponse getNetwork(UUID networkId) {
        return execute(HttpRequest.get("/v1/networks/" + networkId), NetworkResponse.class);
    }

    /**
     * {@code status} é enviado como inteiro bruto na query string, per o contrato documentado do
     * OpenAPI real ({@code AssetNetworkCatalog.Contracts.Enums.AssetNetworkStatus}) — mesmo a
     * resposta devolvendo o status como string (Grupo A). Filtro opcional.
     */
    public List<AssetNetworkResponse> listAssetNetworks(AssetNetworkStatus status) {
        var path = "/v1/asset-networks" + (status != null ? "?status=" + toRequestRawValue(status) : "");
        return execute(HttpRequest.get(path), new TypeReference<List<AssetNetworkResponse>>() {
        });
    }

    public AssetNetworkResponse getAssetNetwork(UUID assetNetworkId) {
        return execute(HttpRequest.get("/v1/asset-networks/" + assetNetworkId), AssetNetworkResponse.class);
    }

    private int toRequestRawValue(AssetNetworkStatus status) {
        return switch (status.name()) {
            case "ENABLED" -> 1;
            case "PAUSED" -> 2;
            case "DISABLED" -> 3;
            default -> throw new IllegalArgumentException("Valor de AssetNetworkStatus desconhecido para filtro: " + status);
        };
    }
}
