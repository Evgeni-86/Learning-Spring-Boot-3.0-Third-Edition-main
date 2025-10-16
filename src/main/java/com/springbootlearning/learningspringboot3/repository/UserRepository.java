package com.springbootlearning.learningspringboot3.repository;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<UserAccount, Long> {
    UserAccount findByUsername(String username);
}
