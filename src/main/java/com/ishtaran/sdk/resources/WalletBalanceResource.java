package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.WalletAssetBalanceResponse;
import com.ishtaran.sdk.model.dataplane.WalletBalanceResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Ishtaran Wallet Balance / On-Chain Balance capability. The wallet's own on-chain balance --
 * NEVER the Ledger ({@link LedgerResource#getBalance}, a fundamentally different question: "what
 * does Ishtaran's own accounting say" vs "how many tokens actually sit at this address").
 * {@code accountId} is the walletId throughout -- {@code ExecutionDestination} already ties one
 * Account to one registered self-custody address per AssetNetwork, so no separate
 * wallet-registration concept exists (a distinct {@code WalletsResource} already exists for
 * ExecutionCustody's own execution/signing wallets -- an unrelated concept, deliberately not
 * reused here to avoid confusing the two).
 *
 * <p>{@link #getBalance} is always cheap -- it never calls a blockchain/RPC provider, only
 * returns the last known snapshot. Call {@link #refreshBalance} to request a real, authoritative
 * check; the platform enforces its own freshness window (currently 30s) and single-flight guard
 * server-side, so calling it more often than needed is always safe (never causes extra provider
 * cost, never an error) -- check {@code refreshSuppressed}/{@code stale}/
 * {@code nextRefreshAllowedAt} on the result rather than polling blindly.
 */
public final class WalletBalanceResource extends ApiResourceSupport {

    public WalletBalanceResource(HttpTransport transport) {
        super(transport);
    }

    public WalletBalanceResponse getBalance(UUID accountId, UUID environmentId, UUID assetNetworkId) {
        return execute(HttpRequest.get(
                "/v1/accounts/" + accountId + "/wallet-balances?environmentId=" + environmentId
                        + "&assetNetworkId=" + assetNetworkId),
                WalletBalanceResponse.class);
    }

    /** Never informs what the new balance should be -- only asks the platform to check, authoritatively, itself. */
    public WalletBalanceResponse refreshBalance(UUID accountId, UUID environmentId, UUID assetNetworkId) {
        return execute(HttpRequest.post(
                "/v1/accounts/" + accountId + "/wallet-balances/refresh?environmentId=" + environmentId
                        + "&assetNetworkId=" + assetNetworkId,
                null, false),
                WalletBalanceResponse.class);
    }

    /**
     * Aggregates balance across the given AssetNetworks, grouped by Asset (e.g. USDT total across
     * TRON + Ethereum) -- {@code assetNetworkIds} are candidates the caller already knows about
     * (from {@link AssetNetworkCatalogResource#listAssetNetworks}, say); any candidate this
     * Account has no registered address for is simply omitted from the result, never an error.
     */
    public List<WalletAssetBalanceResponse> getAssetBalances(UUID accountId, UUID environmentId, List<UUID> assetNetworkIds) {
        var joined = assetNetworkIds.stream().map(UUID::toString).collect(Collectors.joining(","));
        return execute(HttpRequest.get(
                "/v1/accounts/" + accountId + "/wallet-balances/aggregate?environmentId=" + environmentId
                        + "&assetNetworkIds=" + joined),
                new TypeReference<List<WalletAssetBalanceResponse>>() {
                });
    }
}
