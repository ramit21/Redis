package com.example.redisdemo.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Jacksonized
@Builder
public class Department implements Serializable {
    private Long id;
    private String name;
}
