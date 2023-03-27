package com.example.redisdemo.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
public class CacheKeySummary {
    private String key;
    private Long ttl;
}
