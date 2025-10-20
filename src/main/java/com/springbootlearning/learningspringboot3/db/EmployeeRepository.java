package com.springbootlearning.learningspringboot3.db;

import com.springbootlearning.learningspringboot3.domain.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
}
