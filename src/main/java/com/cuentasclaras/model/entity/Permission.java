package com.cuentasclaras.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @Column(name = "permission_code", length = 5, nullable = false)
    private String permissionCode;

    @Column(name = "permission_name", length = 250, nullable = false)
    private String permissionName;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
