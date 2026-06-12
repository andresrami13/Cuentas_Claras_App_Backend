package com.cuentasclaras.model.entity;

import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.model.enums.Periodicity;
import com.cuentasclaras.security.converter.BigDecimalCryptoConverter;
import com.cuentasclaras.security.converter.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "financial_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "financial_record_id")
    private Long financialRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_document_number", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 20)
    private FinancialRecordType recordType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_category_id")
    private BudgetCategory budgetCategory;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "description", length = 1024)
    private String description;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "amount", nullable = false, length = 255)
    private BigDecimal amount;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "recurring", nullable = false)
    private Boolean recurring;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicity", length = 20)
    private Periodicity periodicity;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
