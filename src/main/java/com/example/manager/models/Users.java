package com.example.manager.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import java.util.List;

@Data
@Entity
@Table(name = "Users")
public class Users {
    @Id
    @UuidGenerator
    @Column(name = "user_id")
    private String userId;

    @Column(unique = true)
    private String userName;

    @Column
    private String password;

    @Column(name = "refresh_token",nullable = true)
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserRoles> userRoles;
}
