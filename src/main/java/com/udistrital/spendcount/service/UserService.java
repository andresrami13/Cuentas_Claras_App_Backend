package com.udistrital.spendcount.service;


import com.udistrital.spendcount.model.dto.LoginDto;
import com.udistrital.spendcount.model.dto.LoginResponse;
import com.udistrital.spendcount.model.entity.User;

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
