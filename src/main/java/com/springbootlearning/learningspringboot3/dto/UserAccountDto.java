package com.springbootlearning.learningspringboot3.dto;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public record UserAccountDto(String username, String password, List<String> authorities) {

    public UserAccount toEntity(PasswordEncoder passwordEncoder) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(username);
        userAccount.setPassword(passwordEncoder.encode(password)); // Хэшируем пароль!

        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        userAccount.setAuthorities(grantedAuthorities);

        return userAccount;
    }
}
