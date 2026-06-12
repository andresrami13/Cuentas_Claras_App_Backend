package com.cuentasclaras.model.entity;

import com.cuentasclaras.security.converter.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ai_coach_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCoachRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_coach_request_id")
    private Long aiCoachRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_document_number", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_goal_id")
    private FinancialGoal financialGoal;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "question", nullable = false, length = 2048)
    private String question;

    // Conservado como nullable para leer registros antiguos; los nuevos no lo guardan.
    @Lob
    @Column(name = "financial_context", columnDefinition = "TEXT")
    private String financialContext;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "ai_response", nullable = false, columnDefinition = "TEXT")
    private String aiResponse;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
