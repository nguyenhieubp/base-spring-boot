package com.example.manager.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "UserRoles")
public class UserRoles {
    @Id
    @UuidGenerator
    @Column(name = "user_role_id")
    private String userRoleId;


    @ManyToOne(optional = true,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users user;


    @ManyToOne(optional = true,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Roles role;
}
