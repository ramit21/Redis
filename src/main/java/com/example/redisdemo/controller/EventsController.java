package com.example.redisdemo.controller;

import com.example.redisdemo.model.CacheEvent;
import com.example.redisdemo.model.CacheKeySummary;
import com.example.redisdemo.model.Department;
import com.example.redisdemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundStreamOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
    public ResponseEntity<List<CacheEvent>> getAllEvents(){

        return null;
    }

    @PostMapping(value = "/add")
    public String addEvent(@RequestBody CacheEvent event){
        BoundStreamOperations<String, String, CacheEvent> boundedStreamOps = redisTemplate.boundStreamOps(cachePrefix + event.getEmpId());
        Map<String, CacheEvent> streamEntryMap = new HashMap<>();
        streamEntryMap.put(event.getEmpId(), event);
        RecordId recordId = boundedStreamOps.add(streamEntryMap);
        return "Event added with recordId = " + recordId;
    }

}
