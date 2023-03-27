package com.example.redisdemo.config;

import com.example.redisdemo.model.Employee;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class RedisTemplateConfig {

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient client){
        return new RedissonConnectionFactory(client);
    }

    @Bean
    public RedisTemplate<String, Employee> redisTemplate(RedissonConnectionFactory redissonConnectionFactory){
        RedisTemplate<String, Employee> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(RedisSerializer.string()); //String/Java object/JSON can be keys of redis
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(redissonConnectionFactory);
        return redisTemplate;
    }
}
