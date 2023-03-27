package com.example.redisdemo.controller;

import com.example.redisdemo.model.Department;
import com.example.redisdemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    @Qualifier("employeeRedisTemplate")
    private RedisTemplate<String, Employee> redisTemplate;

    private final String cachePrefix = "Employe-"; //Prefix helps in avoiding cache key collisions

    @GetMapping(value="/keys")
    @ResponseBody
    public Set<String> getRedisKeys(){
        Employee emp = Employee.builder().id(1l).name("ramit").dept(Department.builder().id(20l).name("IT").build()).build();
        Set<String> redisKeys = redisTemplate.keys(cachePrefix + "*");
        return redisKeys;
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
        String key = emp.getId().toString();
        redisTemplate.boundHashOps(cachePrefix+key).put(key, emp);
        System.out.println(emp);
        return "Employee added";
    }
}
