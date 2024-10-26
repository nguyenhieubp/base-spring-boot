package com.example.manager.repositories;

import com.example.manager.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles,String> {
    Optional<Roles> findByRoleName(String roleName);
}
