package com.cuentasclaras.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_budget_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBudgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_document_number", nullable = false, unique = true)
    private User user;

    @Column(name = "payment_day", nullable = false)
    private Integer paymentDay;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(
            mappedBy = "userBudgetConfig",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<FixedBudgetCategory> fixedCategories = new ArrayList<>();
}
