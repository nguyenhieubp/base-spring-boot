package com.example.manager.repositories;

import com.example.manager.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,String> {

    Optional<Users> findByUserId(String userId);

    Optional<Users> findByUserName(String userName);

    Boolean existsByUserName(String userName);

    @Query("SELECT u.id FROM Users u")
    List<String> findAllUserIds();
}
