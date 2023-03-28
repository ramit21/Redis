package com.example.redisdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "cache.redis.use.local", havingValue = "false")
public class AwsRedisConfig {

    @Value("${cache.redis.aws.host}")
    private String redisHost;

    @Value("${cache.redis.aws.secret.id}")
    private String redisSecretId;

    @Value("${cache.redis.aws.secret.name}")
    private String redisSecretName;


}
