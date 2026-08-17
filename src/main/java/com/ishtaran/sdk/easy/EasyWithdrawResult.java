package com.ishtaran.sdk.easy;

import com.ishtaran.sdk.model.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Resultado do Easy Mode {@code withdraw()} — nunca esconde a Network Fee (regra explícita do
 * brief). Expõe {@code withdrawalId} real do Core para debugging, mesmo em modo fácil.
 */
public record EasyWithdrawResult(
        UUID withdrawalId,
        BigDecimal requestedAmount,
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        WithdrawalStatus status) {
}
