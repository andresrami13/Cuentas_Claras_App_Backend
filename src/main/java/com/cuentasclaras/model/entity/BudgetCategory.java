package com.cuentasclaras.model.entity;

import com.cuentasclaras.security.converter.BigDecimalCryptoConverter;
import com.cuentasclaras.security.converter.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.*;

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

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "category_name", nullable = false, length = 1024)
    private String categoryName;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "assigned_amount", nullable = false, length = 255)
    private BigDecimal assignedAmount;

    // Antes era un @Formula que sumaba financial_records.amount en SQL. Como ese
    // monto ahora está cifrado y no se puede sumar en la base, este valor se calcula
    // en la capa de servicio sumando los gastos descifrados de la categoría.
    @Transient
    private BigDecimal spentAmount;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
