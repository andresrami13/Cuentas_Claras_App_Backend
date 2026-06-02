package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.LoginDto;
import com.cuentasclaras.model.dto.LoginResponse;
import com.cuentasclaras.model.dto.UserDto;
import com.cuentasclaras.service.UserService;
import com.cuentasclaras.utils.Mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
public class UserController {

    private final UserService userService;
    private final Mapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {

        UserDto response = mapper.toUserDto(userService.createUser(
                mapper.toUser(userDto),
                userDto.getLicense(),
                userDto.getSpecialityCode(),
                userDto.getBloodType()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Usuario creado exitosamente",
                        response
                ));
    }

    @PutMapping("/{documentNumber}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable String documentNumber, @RequestBody UserDto userDto) {
        UserDto response = mapper.toUserDto(userService.updateUser(documentNumber, mapper.toUser(userDto)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Usuario actualizado exitosamente",
                        response
                ));
    }

    @DeleteMapping("/{documentNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String documentNumber) {
        userService.deleteUser(documentNumber);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Usuario eliminado exitosamente",
                        null
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = mapper.toUsersDto(userService.getAllUsers());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Lista de usuarios obtenida exitosamente",
                        users
                ));
    }

    @GetMapping("/{documentNumber}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable String documentNumber) {
        UserDto userDto = mapper.toUserDto(userService.findByDocumentNumber(documentNumber));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Usuario obtenido exitosamente",
                        userDto
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDto loginDto) {
        LoginResponse loginResponse = userService.login(loginDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Login procesado correctamente",
                        loginResponse
                ));
    }

}
