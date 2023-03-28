package com.example.redisdemo.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.redis.connection.stream.RecordId;

import java.io.Serializable;

@Value
@Jacksonized
@Builder
public class CacheEvent implements Serializable {
    private String empId;
    private CacheEventEnum eventType;
}
