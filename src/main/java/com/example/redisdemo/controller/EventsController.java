package com.example.redisdemo.controller;

import com.example.redisdemo.model.CacheEvent;
import com.example.redisdemo.model.CacheKeySummary;
import com.example.redisdemo.model.Department;
import com.example.redisdemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundStreamOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventsController {

    @Autowired
    @Qualifier("eventsRedisTemplate")
    private RedisTemplate<String, CacheEvent> redisTemplate;

    private final int maxStreamSize = 5;
    private final String cachePrefix = "EmployeEvent-";

    @GetMapping
    public ResponseEntity<List<ObjectRecord<String, CacheEvent>>> getAllEvents(){
        List<ObjectRecord<String, CacheEvent>> records = redisTemplate
                .opsForStream()
                .read(CacheEvent.class, StreamOffset.fromStart("my-stream"));
        return ResponseEntity.ok(records);
    }

    @PostMapping(value = "/add")
    public String addEvent(@RequestBody CacheEvent event){
        /*
        HashMapper<CacheEvent, Object, Object> hashMapper = redisTemplate.opsForStream().getHashMapper(CacheEvent.class);
        BoundStreamOperations<String, Object, Object> boundedStreamOps = redisTemplate.boundStreamOps(cachePrefix + event.getEmpId());
        RecordId recordId = boundedStreamOps.add(hashMapper.toHash(event));
        boundedStreamOps.trim(maxStreamSize); //keep size of events controlled
   */
        ObjectRecord<String, CacheEvent> record = StreamRecords.newRecord()
                .in("my-stream")
                .ofObject(event);

        RecordId recordId = redisTemplate
                .opsForStream()
                .add(record);
        return "Event added with recordId = " + recordId;
    }

}
