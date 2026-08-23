package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code CreateAccount} returns {@code { accountId }} (real anonymous object, see AccountsEndpoints.cs). */
public record CreateAccountResult(UUID accountId) {
}
