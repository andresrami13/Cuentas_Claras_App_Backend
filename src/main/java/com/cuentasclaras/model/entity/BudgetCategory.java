package com.cuentasclaras.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "budget_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private BudgetCycle cycle;

    @Column(name = "category_name", nullable = false, length = 150)
    private String categoryName;

    @Column(name = "assigned_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal assignedAmount;

    @Formula("(SELECT COALESCE(SUM(fr.amount), 0) FROM financial_records fr WHERE fr.budget_category_id = id AND fr.record_type = 'EXPENSE')")
    private BigDecimal spentAmount;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
