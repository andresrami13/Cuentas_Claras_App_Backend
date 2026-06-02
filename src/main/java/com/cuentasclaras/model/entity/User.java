package com.cuentasclaras.model.entity;

import com.cuentasclaras.model.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "document_number", nullable = false, length = 12)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 3)
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code", nullable = false)
    private Role role;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "last_name", nullable = false, length = 250)
    private String lastName;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "cel_number", length = 12)
    private String celNumber;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "locked", nullable = false, length = 1)
    private Boolean locked;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
