package com.udistrital.spendcount.model.entity;

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

    @Column(name = "question", nullable = false, length = 1000)
    private String question;

    @Lob
    @Column(name = "financial_context", nullable = false, columnDefinition = "TEXT")
    private String financialContext;

    @Lob
    @Column(name = "ai_response", nullable = false, columnDefinition = "TEXT")
    private String aiResponse;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}