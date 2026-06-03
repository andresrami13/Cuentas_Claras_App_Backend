package com.cuentasclaras.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fixed_budget_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedBudgetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_budget_config_id", nullable = false)
    private UserBudgetConfig userBudgetConfig;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
