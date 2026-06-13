package com.cuentasclaras.service;

import com.cuentasclaras.model.dto.GoogleLoginDto;
import com.cuentasclaras.model.dto.GoogleRegisterDto;
import com.cuentasclaras.model.dto.LoginDto;
import com.cuentasclaras.model.dto.LoginResponse;
import com.cuentasclaras.model.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(String documentNumber, User user, String currentPassword);

    User setUserLocked(String documentNumber, boolean locked);

    User setUserRole(String documentNumber, String roleCode);

    void deleteUser(String documentNumber);

    List<User> getAllUsers();

    User findByDocumentNumber(String documentNumber);

    LoginResponse login(LoginDto loginDto);

    LoginResponse loginWithGoogle(GoogleLoginDto googleLoginDto);

    LoginResponse registerWithGoogle(GoogleRegisterDto googleRegisterDto);

}
