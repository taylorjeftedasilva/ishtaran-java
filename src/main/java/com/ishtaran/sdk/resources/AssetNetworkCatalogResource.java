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
 * Catalog — {@code AssetNetworkCatalog} (6 real routes, read-only in the SDK's scope — the 5 mutation
 * routes are exclusive to Platform Owner, out of scope). Always Member JWT — does not accept API
 * Key today (real Known Gap, see SDK_CAPABILITY_SPEC.md §12.3).
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
     * {@code status} is sent as a raw integer in the query string, per the documented contract of the
     * real OpenAPI ({@code AssetNetworkCatalog.Contracts.Enums.AssetNetworkStatus}) — even though the
     * response returns the status as a string (Group A). Optional filter.
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
            default -> throw new IllegalArgumentException("Unknown AssetNetworkStatus value for filter: " + status);
        };
    }
}
