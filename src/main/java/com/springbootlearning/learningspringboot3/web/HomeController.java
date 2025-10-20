package com.springbootlearning.learningspringboot3.web;

import com.springbootlearning.learningspringboot3.db.EmployeeRepository;
import com.springbootlearning.learningspringboot3.domain.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    private EmployeeRepository employeeRepository;

    public HomeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public Mono<Rendering> index() {
        return employeeRepository.findAll()
                .collectList()
                .map(employees -> Rendering
                        .view("index")
                        .modelAttribute("employees", employees)
                        .modelAttribute("newEmployee", new Employee("", ""))
                        .build());
    }

    @PostMapping("/new-employee")
    public Mono<String> newEmployee(@ModelAttribute Mono<Employee> newEmployee) {
        return newEmployee.flatMap(employee -> {
            Employee employeeToLoad = new Employee(employee.getName(), employee.getRole());
            return employeeRepository.save(employeeToLoad);
        }).map(employeeMono -> "redirect:/");
    }
}
