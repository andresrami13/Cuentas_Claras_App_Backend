package com.cuentasclaras.service;

import com.cuentasclaras.model.dto.LoginDto;
import com.cuentasclaras.model.dto.LoginResponse;
import com.cuentasclaras.model.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User createUser(User user, String license, String specialityCode, String bloodType);

    User updateUser(String documentNumber, User user);

    void deleteUser(String documentNumber);

    List<User> getAllUsers();

    User findByDocumentNumber(String documentNumber);

    LoginResponse login(LoginDto loginDto);

}
