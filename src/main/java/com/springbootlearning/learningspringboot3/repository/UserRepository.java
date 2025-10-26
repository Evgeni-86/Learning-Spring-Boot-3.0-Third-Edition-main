package com.springbootlearning.learningspringboot3.repository;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}
