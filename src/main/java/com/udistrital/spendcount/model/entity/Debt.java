package com.udistrital.spendcount.model.entity;

import com.udistrital.spendcount.model.enums.DebtStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "debts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debt_id")
    private Long debtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_document_number", nullable = false)
    private User user;

    @Column(name = "creditor", nullable = false, length = 150)
    private String creditor;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "initial_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal initialAmount;

    @Column(name = "pending_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal pendingAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DebtStatus status;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}