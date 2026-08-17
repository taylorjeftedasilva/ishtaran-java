package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.BalanceResponse;
import com.ishtaran.sdk.model.dataplane.LedgerEntryResponse;
import com.ishtaran.sdk.model.enums.EntryNature;
import com.ishtaran.sdk.pagination.PageIterator;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Data Plane — {@code Ledger} (2 rotas reais, ambas leitura). */
public final class LedgerResource extends ApiResourceSupport {

    public LedgerResource(HttpTransport transport) {
        super(transport);
    }

    public BalanceResponse getBalance(UUID accountId, UUID assetNetworkId) {
        return execute(HttpRequest.get(
                "/v1/accounts/" + accountId + "/balance?assetNetworkId=" + assetNetworkId),
                BalanceResponse.class);
    }

    /** {@code skip}/{@code take} são paginação real (ver SDK_CAPABILITY_SPEC.md §12.7). */
    public List<LedgerEntryResponse> listEntries(UUID accountId, UUID assetNetworkId, EntryNature nature,
                                                  OffsetDateTime from, OffsetDateTime to, int skip, int take) {
        var query = new StringBuilder("/v1/accounts/" + accountId + "/ledger-entries?assetNetworkId=" + assetNetworkId);
        if (nature != null) {
            query.append("&nature=").append(nature.rawValue());
        }
        if (from != null) {
            query.append("&from=").append(from);
        }
        if (to != null) {
            query.append("&to=").append(to);
        }
        query.append("&skip=").append(skip).append("&take=").append(take);
        return execute(HttpRequest.get(query.toString()), new TypeReference<List<LedgerEntryResponse>>() {
        });
    }

    /** Iterador lazy — ver SDK_CAPABILITY_SPEC.md §12.7 e {@link WithdrawalsResource#listAll}. */
    public PageIterator<LedgerEntryResponse> listAllEntries(UUID accountId, UUID assetNetworkId, EntryNature nature,
                                                              OffsetDateTime from, OffsetDateTime to, int pageSize) {
        return new PageIterator<>(pageSize, (skip, take) -> listEntries(accountId, assetNetworkId, nature, from, to, skip, take));
    }
}
