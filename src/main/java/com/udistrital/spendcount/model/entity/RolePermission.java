package com.udistrital.spendcount.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "roles_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @MapsId("roleCode")
    @JoinColumn(name = "role_code", nullable = false)
    private Role role;

    @ManyToOne
    @MapsId("permissionCode")
    @JoinColumn(name = "permission_code", nullable = false)
    private Permission permission;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}