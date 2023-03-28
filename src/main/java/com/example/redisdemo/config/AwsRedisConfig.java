package com.example.redisdemo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;
import software.amazon.awssdk.services.elasticache.ElastiCacheClientBuilder;
import software.amazon.awssdk.services.elasticache.model.DescribeReplicationGroupsRequest;
import software.amazon.awssdk.services.elasticache.model.DescribeReplicationGroupsResponse;
import software.amazon.awssdk.services.elasticache.model.Endpoint;
import software.amazon.awssdk.services.elasticache.model.ReplicationGroup;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(name = "cache.redis.use.local", havingValue = "false")
public class AwsRedisConfig {

    @Value("${cache.redis.aws.host}")
    private String redisHost;

    @Value("${cache.redis.aws.secret.id}")
    private String redisSecretId;

    @Value("${cache.redis.aws.secret.name}")
    private String redisSecretName;

    @Bean
    Region region(@Value("${aws.region}") String region) {
        return Region.of(region);
    }

    @Bean
    ElastiCacheClientBuilder elastiCacheClientBuilder(Region region) {
        return ElastiCacheClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region);
    }

    @Bean
    SecretsManagerClientBuilder secretsManagerClientBuilder(Region region) {
        return SecretsManagerClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region);
    }

    @Bean
    RedissonClient redissonClient(ElastiCacheClientBuilder elastiCacheClientBuilder,
                                  SecretsManagerClientBuilder secretsManagerClientBuilder,
                                  ObjectMapper objectMapper) throws JsonProcessingException {
        String redisPasswordJson;
        try(SecretsManagerClient secretsManagerClient = secretsManagerClientBuilder.build()) {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(redisSecretId).build();
            redisPasswordJson = secretsManagerClient.getSecretValue(getSecretValueRequest).secretString();
        }
        String redisPassword = objectMapper.reader()
                .readTree(redisPasswordJson)
                .get(redisSecretName)
                .textValue();

        Endpoint configEndpoint;
        try (ElastiCacheClient elastiCacheClient = elastiCacheClientBuilder.build()) {
            DescribeReplicationGroupsRequest describeReplicationGroupsRequest = DescribeReplicationGroupsRequest.builder()
                    .replicationGroupId(redisHost)
                    .build();
            DescribeReplicationGroupsResponse describeReplicationGroupsResponse =
                    elastiCacheClient.describeReplicationGroups(describeReplicationGroupsRequest);
            List<Endpoint> endpoints = describeReplicationGroupsResponse.replicationGroups().stream()
                    .map(ReplicationGroup::configurationEndpoint)
                    .collect(Collectors.toList());
            configEndpoint = endpoints.get(0);
        }
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000)
                .setPassword(redisPassword)
                .addNodeAddress("rediss://" + configEndpoint.address() + ":" + configEndpoint.port());
        return Redisson.create(config);
    }
}
