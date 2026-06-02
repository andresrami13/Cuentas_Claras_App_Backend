package com.udistrital.spendcount.service.implementation;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.dto.LoginDto;
import com.udistrital.spendcount.model.dto.LoginResponse;
import com.udistrital.spendcount.model.entity.*;
import com.udistrital.spendcount.repository.*;
import com.udistrital.spendcount.service.UserService;
import com.udistrital.spendcount.utils.Encryption;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Encryption encryption;

    /**
     * Allows to create a new user, encrypting their password before persisting.
     *
     * @param user  user info to save
     * @return User object saved
     */
    @Override
    public User createUser(@Valid User user) {
        return this.createUser(user, null, null, null);
    }

    @Override
    public User createUser(@Valid User user, String license, String specialityCode, String bloodType) {

        //Business validation
        log.info("Inicio de validaciones de negocio para crear usuario");
        if (user.getDocumentNumber() == null || user.getDocumentNumber().isBlank())
            throw new BusinessException("El número de documento es obligatorio");

        this.applyBussinessValidation(user);

        if (user.getPassword() == null)
            throw new BusinessException("La contraseña del usuario es obligatoria");

        String roleCode = user.getRole().getRoleCode();
        Role role = roleRepository.findById(roleCode)
            .orElseThrow(() -> new BusinessException("No existe el rol con código: " + roleCode));
        user.setRole(role);

        if (userRepository.existsByDocumentNumber(user.getDocumentNumber()))
            throw new BusinessException("Ya existe un usuario con el número de documento: " + user.getDocumentNumber());

        try {
            user.setCreatedAt(new Date());
            //Password encryption
            user.setPassword(encryption.encrypt(user.getPassword()));
            log.info("Registrando usuario");
            user = userRepository.saveAndFlush(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar usuario", e);
            throw new SystemException("Error al intentar registrar usuario");
        }

        return user;
    }
    /**
     * Allows to update an existing user identified by their document number,
     * encrypting their new password before persisting.
     *
     * @param documentNumber    document number of the user to update
     * @param user              user info to save
     * @return User             object saved
     */
    @Override
    public User updateUser(String documentNumber, User user) {

        //Business validation
        log.info("Inicio de validaciones de negocio para actualizar usuario");
        this.applyBussinessValidation(user);

        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new  BusinessException("No existe el usuario con número de documento: " + documentNumber));

        try {
            existingUser.setDocumentType(user.getDocumentType());
            existingUser.setDocumentNumber(user.getDocumentNumber());
            existingUser.setRole(user.getRole());
            existingUser.setName(user.getName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setCelNumber(user.getCelNumber());
            existingUser.setBirthDate(user.getBirthDate());
            //Password encryption
            if (user.getPassword() != null && !user.getPassword().isEmpty())
                existingUser.setPassword(encryption.encrypt(user.getPassword()));

            existingUser.setLocked(user.getLocked());
            existingUser.setUpdatedAt(new Date());
            log.info("Actualizando usuario");
            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar actualizar usuario: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar actualizar usuario");
        }

    }

    /**
     * Allows to delete a user identified by their document number.
     *
     * @param documentNumber    document number of the user to delete
     */
    @Override
    public void deleteUser(String documentNumber) {
        //Business validations
        log.info("Inicio de validaciones de negocio para eliminar usuario");
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new  BusinessException("El número de documento del usuario es obligatorio");
        }

        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new  BusinessException("No existe usuario registrado con el número de documento: " + documentNumber));

        try {
            log.info("Eliminando usuario".concat(documentNumber));
            userRepository.delete(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar eliminar usuario: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar eliminar usuario");
        }
    }

    /**
     * Allows to get all the users in the system.
     *
     * @return List&lt;User&gt;  list of found users
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try{
            log.info("Obteniendo lista de usuarios");
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("Error al intentar consultar lista de usuarios: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar consultar lista de usuarios");
        }
    }

    /**
     * Allows to get a user by their document number.
     *
     * @param documentNumber    document number of the user to find
     * @return User             object found
     */
    @Override
    @Transactional(readOnly = true)
    public User findByDocumentNumber(String documentNumber) {
        try{
            log.info("Obteniendo usuario con número de documento: ".concat(documentNumber));
            return userRepository.findById(documentNumber).orElseThrow(() -> new BusinessException("No se ha encontrado un usuario con número de documento ".concat(documentNumber)));
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            log.error("Error al intentar consultar usuario con número de documento: ".concat(documentNumber));
            throw new SystemException("Error al intentar consultar usuario con número de documetno ".concat(documentNumber));
        }
    }

    /**
     * Validates user credentials against the stored encrypted password.
     *
     * @param loginDto  data transfer object containing the user's email, password and document number
     * @return LoginResponse    object indicating whether the credentials matched,
     *                          along with a descriptive detail message
     */
    @Override
    public LoginResponse login(LoginDto loginDto) {
        try {
            //Business validations
            if (loginDto.getPassword() == null || loginDto.getPassword().isBlank())
                throw new BusinessException("La contraseña del usuario es obligatoria");

            if (loginDto.getDocumentNumber() == null || loginDto.getDocumentNumber().isBlank())
                throw new BusinessException("El número de documento del usuario es obligatoria");

            User user = userRepository.findByDocumentNumber(loginDto.getDocumentNumber())
                    .orElse(null);

            if (user == null) {
                log.info("No se encontró usuario con el número de documento proporcionado");
                return LoginResponse.builder()
                        .match(false)
                        .detail("Credenciales incorrectas")
                        .build();
            }

            boolean match = encryption.matches(loginDto.getPassword(), user.getPassword());
            log.info("Resultado de validación de credenciales: {}", match ? "exitoso" : "fallido");
            return LoginResponse.builder().match(match).detail(match ? "Login correcto" : "Credenciales incorrectas" ).build();
        } catch (Exception e) {
            log.error("Error al intentar validar login de usuario: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar validar login de usuario");
        }
    }

    /**
     * Applies business validations for the user entity, checking that all required
     * fields are present and non-blank.
     *
     * @param user  user to validate
     */
    private void applyBussinessValidation(User user) {
        if (user.getDocumentType() == null)
            throw new BusinessException("El tipo de documento es obligatorio");

        if (user.getRole() == null)
            throw new BusinessException("El rol del usuario es obligatorio");

        if (user.getRole().getRoleCode() == null || user.getRole().getRoleCode().isBlank())
            throw new BusinessException("El codigo del rol del usuario es obligatorio");

        if (user.getName() == null || user.getName().isBlank())
            throw new BusinessException("El nombre del usuario es obligatorio");

        if (user.getLastName() == null || user.getLastName().isBlank())
            throw new BusinessException("El apellido del usuario es obligatorio");

        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new BusinessException("El email del usuario es obligatorio");

        if (user.getCelNumber() == null || user.getCelNumber().isBlank())
            throw new BusinessException("El número de celular del usuario es obligatorio");

        if (user.getBirthDate() == null)
            throw new BusinessException("La fecha de naciemiento del usuario es obligatoria");

        if (user.getLocked() == null)
            throw new BusinessException("Es necesario indicar si el usuario está o no bloqueado, parámetro: locked");
    }

}
