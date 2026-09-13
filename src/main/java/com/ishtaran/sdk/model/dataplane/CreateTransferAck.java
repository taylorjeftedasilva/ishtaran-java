package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** The creation endpoint's own real shape -- {@code POST /v1/organizations/{id}/transfers} only ever acknowledges the new id, never the full record (see {@code TransfersResource#request}). */
public record CreateTransferAck(UUID transferId) {
}
