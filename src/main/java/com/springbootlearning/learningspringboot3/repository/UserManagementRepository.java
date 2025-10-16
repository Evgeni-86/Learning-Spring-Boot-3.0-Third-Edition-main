package com.springbootlearning.learningspringboot3.repository;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<UserAccount, Long> {
}
