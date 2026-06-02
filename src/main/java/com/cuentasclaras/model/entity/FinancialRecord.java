package com.cuentasclaras.model.entity;

import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.model.enums.Periodicity;
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

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
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
