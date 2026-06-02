package com.cuentasclaras.utils;

import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.dto.*;
import com.cuentasclaras.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapper {

    //Permissions Adapters
    public PermissionDto toPermissionDto(Permission permission) {
        return new PermissionDto(permission.getPermissionCode(), permission.getPermissionName(), permission.getDescription());
    }

    public List<PermissionDto> toPermissionsDto(List<Permission> permissions) {
        return permissions.stream().map(this::toPermissionDto).toList();
    }

    public Permission toPermission(PermissionDto permissionDto) {
        return Permission.builder()
                .permissionCode(permissionDto.getPermissionCode())
                .permissionName(permissionDto.getPermissionName())
                .description(permissionDto.getDescription()).build();
    }

    //Roles Adapters
    public RoleDto toRoleDto(Role role) {
        RoleDto roleDto;

        try {
            roleDto = RoleDto.builder().
                    roleCode(role.getRoleCode())
                    .roleName(role.getRoleName())
                    .description(role.getDescription())
                    .rolePermissionDtoList(this.toRolesPermissionsDto(role.getRolePermissions()))
                    .build();
        } catch (Exception e) {
            throw new SystemException("Error al intentar hacer mapper to role dto");
        }

        return roleDto;
    }

    public List<RoleDto> toRolesDto(List<Role> roles) {
        return roles.stream().map(this::toRoleDto).toList();
    }

    private RolePermissionDto toRolePermissionDto(RolePermission rolePermission) {
        return new RolePermissionDto(rolePermission.getId().getRoleCode(), rolePermission.getId().getPermissionCode());
    }

    private List<RolePermissionDto> toRolesPermissionsDto(List<RolePermission> rolesPermissions) {
        return rolesPermissions.stream().map(this::toRolePermissionDto).toList();
    }

    public Role toRole(RoleDto roleDto) {
        Role role;

        try {
            role = Role.builder()
                    .roleCode(roleDto.getRoleCode())
                    .roleName(roleDto.getRoleName())
                    .description(roleDto.getDescription())
                    .rolePermissions(roleDto.getRolePermissionDtoList() != null ? this.toRolesPermissions(roleDto.getRolePermissionDtoList()) : null)
                    .build();
        } catch (Exception e) {
            throw new SystemException("Error al intentar hacer mapper to role entity");
        }

        return role;
    }

    private RolePermission toRolePermission(RolePermissionDto rolePermissionDto) {
        return RolePermission.builder().id(new RolePermissionId(rolePermissionDto.getRoleCode(), rolePermissionDto.getPermissionCode())).build();
    }

    private List<RolePermission> toRolesPermissions(List<RolePermissionDto> rolesPermissionsDto) {
        return rolesPermissionsDto.stream().map(this::toRolePermission).toList();
    }


    //User Adapters
    public UserDto toUserDto(User user) {
        RoleDto roleDto = RoleDto.builder().roleCode(user.getRole().getRoleCode()).build();
        return UserDto.builder()
                .documentType(user.getDocumentType())
                .documentNumber(user.getDocumentNumber())
                .role(roleDto)
                .name(user.getName()).lastName(user.getLastName())
                .email(user.getEmail())
                .celNumber(user.getCelNumber())
                .birthDate(user.getBirthDate())
                .password(user.getPassword())
                .locked(user.getLocked())
                .build();
    }

    public List<UserDto> toUsersDto(List<User> users) {
        return users.stream().map(this::toUserDto).toList();
    }

    public User toUser(UserDto userDto) {
        Role role = Role.builder().roleCode(userDto.getRole().getRoleCode()).build();

        return User.builder()
                .documentType(userDto.getDocumentType())
                .documentNumber(userDto.getDocumentNumber())
                .role(role)
                .name(userDto.getName()).lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .celNumber(userDto.getCelNumber())
                .birthDate(userDto.getBirthDate())
                .password(userDto.getPassword())
                .locked(userDto.getLocked())
                .build();
    }

    //Financial Records Adapters
    public FinancialRecordDto toFinancialRecordDto(FinancialRecord financialRecord) {
        return FinancialRecordDto.builder()
                .financialRecordId(financialRecord.getFinancialRecordId())
                .userDocumentNumber(financialRecord.getUser().getDocumentNumber())
                .recordType(financialRecord.getRecordType())
                .category(financialRecord.getCategory())
                .description(financialRecord.getDescription())
                .amount(financialRecord.getAmount())
                .recordDate(financialRecord.getRecordDate())
                .recurring(financialRecord.getRecurring())
                .periodicity(financialRecord.getPeriodicity())
                .build();
    }

    public List<FinancialRecordDto> toFinancialRecordsDto(List<FinancialRecord> financialRecords) {
        return financialRecords.stream().map(this::toFinancialRecordDto).toList();
    }

    public FinancialRecord toFinancialRecord(FinancialRecordDto financialRecordDto) {
        User user = User.builder()
                .documentNumber(financialRecordDto.getUserDocumentNumber())
                .build();

        return FinancialRecord.builder()
                .financialRecordId(financialRecordDto.getFinancialRecordId())
                .user(user)
                .recordType(financialRecordDto.getRecordType())
                .category(financialRecordDto.getCategory())
                .description(financialRecordDto.getDescription())
                .amount(financialRecordDto.getAmount())
                .recordDate(financialRecordDto.getRecordDate())
                .recurring(financialRecordDto.getRecurring())
                .periodicity(financialRecordDto.getPeriodicity())
                .build();
    }

    //Debts Adapters
    public DebtDto toDebtDto(Debt debt) {
        return DebtDto.builder()
                .debtId(debt.getDebtId())
                .userDocumentNumber(debt.getUser().getDocumentNumber())
                .creditor(debt.getCreditor())
                .description(debt.getDescription())
                .initialAmount(debt.getInitialAmount())
                .pendingAmount(debt.getPendingAmount())
                .startDate(debt.getStartDate())
                .dueDate(debt.getDueDate())
                .status(debt.getStatus())
                .build();
    }

    public List<DebtDto> toDebtsDto(List<Debt> debts) {
        return debts.stream().map(this::toDebtDto).toList();
    }

    public Debt toDebt(DebtDto debtDto) {
        User user = User.builder()
                .documentNumber(debtDto.getUserDocumentNumber())
                .build();

        return Debt.builder()
                .debtId(debtDto.getDebtId())
                .user(user)
                .creditor(debtDto.getCreditor())
                .description(debtDto.getDescription())
                .initialAmount(debtDto.getInitialAmount())
                .pendingAmount(debtDto.getPendingAmount())
                .startDate(debtDto.getStartDate())
                .dueDate(debtDto.getDueDate())
                .status(debtDto.getStatus())
                .build();
    }

    //Financial Goals Adapters
    public FinancialGoalDto toFinancialGoalDto(FinancialGoal financialGoal) {
        return FinancialGoalDto.builder()
                .financialGoalId(financialGoal.getFinancialGoalId())
                .userDocumentNumber(financialGoal.getUser().getDocumentNumber())
                .name(financialGoal.getName())
                .description(financialGoal.getDescription())
                .targetAmount(financialGoal.getTargetAmount())
                .currentAmount(financialGoal.getCurrentAmount())
                .startDate(financialGoal.getStartDate())
                .targetDate(financialGoal.getTargetDate())
                .status(financialGoal.getStatus())
                .build();
    }

    public List<FinancialGoalDto> toFinancialGoalsDto(List<FinancialGoal> financialGoals) {
        return financialGoals.stream().map(this::toFinancialGoalDto).toList();
    }

    public FinancialGoal toFinancialGoal(FinancialGoalDto financialGoalDto) {
        User user = User.builder()
                .documentNumber(financialGoalDto.getUserDocumentNumber())
                .build();

        return FinancialGoal.builder()
                .financialGoalId(financialGoalDto.getFinancialGoalId())
                .user(user)
                .name(financialGoalDto.getName())
                .description(financialGoalDto.getDescription())
                .targetAmount(financialGoalDto.getTargetAmount())
                .currentAmount(financialGoalDto.getCurrentAmount())
                .startDate(financialGoalDto.getStartDate())
                .targetDate(financialGoalDto.getTargetDate())
                .status(financialGoalDto.getStatus())
                .build();
    }

    //AI Coach Requests Adapters
    public AiCoachRequestDto toAiCoachRequestDto(AiCoachRequest aiCoachRequest) {
        Long financialGoalId = aiCoachRequest.getFinancialGoal() != null
                ? aiCoachRequest.getFinancialGoal().getFinancialGoalId()
                : null;

        return AiCoachRequestDto.builder()
                .aiCoachRequestId(aiCoachRequest.getAiCoachRequestId())
                .userDocumentNumber(aiCoachRequest.getUser().getDocumentNumber())
                .financialGoalId(financialGoalId)
                .question(aiCoachRequest.getQuestion())
                .financialContext(aiCoachRequest.getFinancialContext())
                .aiResponse(aiCoachRequest.getAiResponse())
                .model(aiCoachRequest.getModel())
                .build();
    }

    public List<AiCoachRequestDto> toAiCoachRequestsDto(List<AiCoachRequest> aiCoachRequests) {
        return aiCoachRequests.stream().map(this::toAiCoachRequestDto).toList();
    }

    public AiCoachRequest toAiCoachRequest(AiCoachRequestDto aiCoachRequestDto) {
        User user = User.builder()
                .documentNumber(aiCoachRequestDto.getUserDocumentNumber())
                .build();

        FinancialGoal financialGoal = aiCoachRequestDto.getFinancialGoalId() != null
                ? FinancialGoal.builder()
                  .financialGoalId(aiCoachRequestDto.getFinancialGoalId())
                  .build()
                : null;

        return AiCoachRequest.builder()
                .aiCoachRequestId(aiCoachRequestDto.getAiCoachRequestId())
                .user(user)
                .financialGoal(financialGoal)
                .question(aiCoachRequestDto.getQuestion())
                .financialContext(aiCoachRequestDto.getFinancialContext())
                .aiResponse(aiCoachRequestDto.getAiResponse())
                .model(aiCoachRequestDto.getModel())
                .build();
    }
}
