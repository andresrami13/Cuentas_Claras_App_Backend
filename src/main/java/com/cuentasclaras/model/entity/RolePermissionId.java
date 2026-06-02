package com.cuentasclaras.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionId implements Serializable {

    @Column(name = "role_code", length = 3, nullable = false)
    private String roleCode;

    @Column(name = "permission_code", length = 5, nullable = false)
    private String permissionCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePermissionId that)) return false;
        return Objects.equals(roleCode, that.roleCode)
                && Objects.equals(permissionCode, that.permissionCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleCode, permissionCode);
    }
}
