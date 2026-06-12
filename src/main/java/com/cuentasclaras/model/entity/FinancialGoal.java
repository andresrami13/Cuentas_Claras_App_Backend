package com.cuentasclaras.model.entity;

import com.cuentasclaras.model.enums.FinancialGoalStatus;
import com.cuentasclaras.security.converter.BigDecimalCryptoConverter;
import com.cuentasclaras.security.converter.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "financial_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "financial_goal_id")
    private Long financialGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_document_number", nullable = false)
    private User user;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "name", nullable = false, length = 1024)
    private String name;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "description", length = 1024)
    private String description;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "target_amount", nullable = false, length = 255)
    private BigDecimal targetAmount;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "current_amount", nullable = false, length = 255)
    private BigDecimal currentAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FinancialGoalStatus status;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
