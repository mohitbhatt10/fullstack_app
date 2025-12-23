package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleName(String roleName);

}
