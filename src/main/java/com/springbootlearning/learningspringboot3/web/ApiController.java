package com.springbootlearning.learningspringboot3.web;

import com.springbootlearning.learningspringboot3.db.EmployeeRepository;
import com.springbootlearning.learningspringboot3.domain.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ApiController {

    private EmployeeRepository employeeRepository;

    public ApiController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/api/employees")
    Flux<Employee> employees() {
        return employeeRepository.findAll();
    }

    @PostMapping("/api/employees")
    Mono<Employee> add(@RequestBody Mono<Employee> newEmployee) {
        return newEmployee.flatMap(employee -> {
            Employee employeeToLoad = new Employee(employee.getName(), employee.getRole());
            return employeeRepository.save(employeeToLoad);
        });
    }
}
