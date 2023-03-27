package com.example.redisdemo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalRedisConfig {

    @Value("${cache.redis.local.host:localhost}")
    private String redisHost;

    @Value("${cache.redis.local.port:6379}")
    private String redisPort;

    @Bean
    public RedissonClient redissonClient(){
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        return Redisson.create(redissonConfig);
    }

}
