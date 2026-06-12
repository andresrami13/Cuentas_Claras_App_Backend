package com.cuentasclaras.model.entity;

import com.cuentasclaras.model.enums.DebtStatus;
import com.cuentasclaras.security.converter.BigDecimalCryptoConverter;
import com.cuentasclaras.security.converter.StringCryptoConverter;
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

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "creditor", nullable = false, length = 1024)
    private String creditor;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "description", length = 1024)
    private String description;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "initial_amount", nullable = false, length = 255)
    private BigDecimal initialAmount;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "pending_amount", nullable = false, length = 255)
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
