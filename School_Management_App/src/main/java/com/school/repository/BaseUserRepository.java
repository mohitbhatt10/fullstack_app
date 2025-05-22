package com.school.repository;

import com.school.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseUserRepository extends UserRepository<User> {
}
