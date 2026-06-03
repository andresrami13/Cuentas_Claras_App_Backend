package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.UserBudgetConfig;

public interface UserBudgetConfigService {
    UserBudgetConfig saveConfig(UserBudgetConfig config, String userDocumentNumber);
    UserBudgetConfig getConfig(String userDocumentNumber);
}
