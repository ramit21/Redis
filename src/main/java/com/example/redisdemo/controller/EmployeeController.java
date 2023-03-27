package com.example.redisdemo.controller;

import com.example.redisdemo.model.Department;
import com.example.redisdemo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    @Qualifier("employeeRedisTemplate")
    private RedisTemplate<String, Employee> redisTemplate;

    @GetMapping
    @ResponseBody
    public List<Employee> getEmployees(){
        Employee emp = Employee.builder().id(1l).name("ramit").dept(Department.builder().id(20l).name("IT").build()).build();
        return List.of(emp);
    }

    @PostMapping(value = "/add")
    public String saveEmployee(@RequestBody Employee emp){
        System.out.println(emp);
        return "Employee added";
    }
}
