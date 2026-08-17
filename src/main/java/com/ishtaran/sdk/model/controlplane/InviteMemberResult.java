package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/** {@code plainTextInviteToken} só aparece nesta resposta (equivalente a um convite por e-mail em produção real). */
public record InviteMemberResult(UUID memberId, String plainTextInviteToken) {
}
