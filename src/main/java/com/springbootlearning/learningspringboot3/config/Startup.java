package com.springbootlearning.learningspringboot3.config;

import com.springbootlearning.learningspringboot3.domain.Employee;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.springbootlearning.learningspringboot3.web.ApiController.DATABASE;

@Component
public class Startup {
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            DATABASE.put("Frodo Baggins", new Employee("Frodo Baggins", "ring bearer"));
            DATABASE.put("Samwise Gamgee", new Employee("Samwise Gamgee", "gardener"));
            DATABASE.put("Bilbo Baggis", new Employee("Bilbo Baggins", "burglar"));
        };
    }
}