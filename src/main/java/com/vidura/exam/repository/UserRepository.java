package com.vidura.exam.repository;

import com.vidura.exam.entities.Role;
import com.vidura.exam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    int countUsersByRole(Role role);
}