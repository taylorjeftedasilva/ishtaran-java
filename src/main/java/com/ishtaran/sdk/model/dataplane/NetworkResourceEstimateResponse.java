package com.ishtaran.sdk.model.dataplane;

import java.util.List;

public record NetworkResourceEstimateResponse(List<NetworkResourceLineResponse> lines) {
}
