package com.example.manager.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@Entity
@Table(name = "Roles")
public class Roles {
    @Id
    @UuidGenerator
    @Column(name = "role_id")
    private String roleId;

    @Column
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserRoles> userRoles;
}
