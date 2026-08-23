package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/** {@code plainTextInviteToken} only appears in this response (equivalent to an email invitation in real production). */
public record InviteMemberResult(UUID memberId, String plainTextInviteToken) {
}
