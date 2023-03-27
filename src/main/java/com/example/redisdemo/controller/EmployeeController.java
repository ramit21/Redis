package com.example.redisdemo.controller;

import com.example.redisdemo.model.CacheKeySummary;
import com.example.redisdemo.model.Department;
import com.example.redisdemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    @Qualifier("employeeRedisTemplate")
    private RedisTemplate<String, Employee> redisTemplate;

    private final String cachePrefix = "Employe-"; //Prefix helps in avoiding cache key collisions

    private final Long ttlExpiry = 120l;

    @GetMapping(value="/keys")
    @ResponseBody
    public List<CacheKeySummary> getRedisKeys(){
        Employee emp = Employee.builder().id(1l).name("ramit").dept(Department.builder().id(20l).name("IT").build()).build();
        Set<String> redisKeys = redisTemplate.keys(cachePrefix + "*");
        return redisKeys.stream().map(this::mapKeyToSummary).collect(Collectors.toList());
    }

    @GetMapping(value="/{id}")
    @ResponseBody
    public Employee getEmployeeById(@PathVariable String id){
        BoundHashOperations<String, String, Employee> hashOps = redisTemplate.boundHashOps( cachePrefix + id);
        Map<String, Employee> employees = hashOps.entries();
        return employees.get(id);
    }

    @PostMapping(value = "/add")
    public String saveEmployee(@RequestBody Employee emp){
        String idStr = emp.getId().toString();
        String cacheKey = cachePrefix + idStr;
        redisTemplate.boundHashOps(cacheKey).put(idStr, emp);
        redisTemplate.expire(cacheKey, ttlExpiry, TimeUnit.SECONDS);
        return "Employee added succesfully";
    }

    private CacheKeySummary mapKeyToSummary(String key) {
        return CacheKeySummary.builder()
                .key(key)
                .ttl(redisTemplate.getExpire(key))
                .build();
    }
}
