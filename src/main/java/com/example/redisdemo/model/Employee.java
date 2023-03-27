package com.example.redisdemo.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class Employee {
    private Long id;
    private String name;
    private Department dept;
}
