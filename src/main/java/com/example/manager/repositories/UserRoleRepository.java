package com.example.manager.repositories;

import com.example.manager.models.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoles,String>  {
}
