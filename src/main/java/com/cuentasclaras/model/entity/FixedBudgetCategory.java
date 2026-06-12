package com.cuentasclaras.model.entity;

import com.cuentasclaras.security.converter.BigDecimalCryptoConverter;
import com.cuentasclaras.security.converter.StringCryptoConverter;
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

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "category_name", nullable = false, length = 1024)
    private String categoryName;

    @Convert(converter = BigDecimalCryptoConverter.class)
    @Column(name = "amount", nullable = false, length = 255)
    private BigDecimal amount;
}
