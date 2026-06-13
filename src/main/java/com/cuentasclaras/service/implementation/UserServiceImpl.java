package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.dto.GoogleLoginDto;
import com.cuentasclaras.model.dto.GoogleRegisterDto;
import com.cuentasclaras.model.dto.LoginDto;
import com.cuentasclaras.model.dto.LoginResponse;
import com.cuentasclaras.model.entity.*;
import com.cuentasclaras.model.enums.AuthProvider;
import com.cuentasclaras.repository.*;
import com.cuentasclaras.security.GoogleTokenVerifier;
import com.cuentasclaras.security.GoogleUserInfo;
import com.cuentasclaras.security.JwtService;
import com.cuentasclaras.security.SecurityUtils;
import com.cuentasclaras.service.UserService;
import com.cuentasclaras.utils.Constant;
import com.cuentasclaras.utils.Encryption;
import com.cuentasclaras.utils.LogMask;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final JwtService jwtService;
    private final SecurityUtils securityUtils;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Value("${app.security.default-role-code}")
    private String defaultRoleCode;

    @Value("${app.security.login.max-failed-attempts:10}")
    private int maxFailedAttempts;

    @Value("${app.security.login.lockout-minutes:15}")
    private long lockoutMinutes;

    @Override
    public User createUser(@Valid User user) {

        log.info("Inicio de validaciones de negocio para crear usuario");
        if (user.getDocumentNumber() == null || user.getDocumentNumber().isBlank())
            throw new BusinessException("El número de documento es obligatorio");

        // El registro público siempre asigna el rol por defecto y la cuenta desbloqueada:
        // el rol nunca se acepta del cliente para impedir auto-asignarse privilegios.
        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        user.setAuthProvider(AuthProvider.LOCAL);

        this.applyProfileValidation(user);

        if (user.getPassword() == null)
            throw new BusinessException("La contraseña del usuario es obligatoria");

        try {
            Role role = roleRepository.findById(defaultRoleCode)
                .orElseThrow(() -> new SystemException("No está configurado el rol por defecto: " + defaultRoleCode));
            user.setRole(role);

            // Mensaje genérico para no revelar qué documentos están registrados (anti-enumeración)
            if (userRepository.existsByDocumentNumber(user.getDocumentNumber()))
                throw new BusinessException("No fue posible completar el registro con los datos proporcionados");

            user.setCreatedAt(new Date());
            user.setPassword(encryption.encrypt(user.getPassword()));
            log.info("Registrando usuario");
            user = userRepository.saveAndFlush(user);

        } catch (BusinessException | SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar usuario: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar registrar usuario");
        }

        return user;
    }

    @Override
    public User updateUser(String documentNumber, User user, String currentPassword) {

        log.info("Inicio de validaciones de negocio para actualizar usuario");

        // Solo el dueño de la cuenta puede modificar su perfil
        securityUtils.validateOwnership(documentNumber,
                Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + documentNumber);

        this.applyProfileValidation(user);

        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + documentNumber));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Las cuentas que entran con Google no tienen contraseña local. Definir cómo se
            // vincula una contraseña a una cuenta Google queda fuera del alcance de la fase 1.
            if (existingUser.getAuthProvider() == AuthProvider.GOOGLE)
                throw new BusinessException("Esta cuenta ingresa con Google; no es posible establecer una contraseña");

            if (currentPassword == null || currentPassword.isBlank())
                throw new BusinessException("Para cambiar la contraseña debe enviar la contraseña actual");

            if (!encryption.matches(currentPassword, existingUser.getPassword()))
                throw new BusinessException("La contraseña actual no es correcta");
        }

        try {
            // No se permite cambiar documentNumber, rol ni estado de bloqueo por esta vía
            existingUser.setDocumentType(user.getDocumentType());
            existingUser.setName(user.getName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setCelNumber(user.getCelNumber());
            existingUser.setBirthDate(user.getBirthDate());
            if (user.getPassword() != null && !user.getPassword().isEmpty())
                existingUser.setPassword(encryption.encrypt(user.getPassword()));

            existingUser.setUpdatedAt(new Date());
            log.info("Actualizando usuario");
            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar actualizar usuario: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar usuario");
        }

    }

    @Override
    public User setUserLocked(String documentNumber, boolean locked) {
        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + documentNumber));

        try {
            existingUser.setLocked(locked);
            existingUser.setUpdatedAt(new Date());
            log.info("Actualizando estado de bloqueo del usuario");
            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar actualizar estado de bloqueo: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar estado de bloqueo del usuario");
        }
    }

    @Override
    public User setUserRole(String documentNumber, String roleCode) {
        if (roleCode == null || roleCode.isBlank())
            throw new BusinessException("El código del rol es obligatorio");

        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + documentNumber));

        Role role = roleRepository.findById(roleCode)
                .orElseThrow(() -> new BusinessException("No existe el rol con código: " + roleCode));

        try {
            existingUser.setRole(role);
            existingUser.setUpdatedAt(new Date());
            log.info("Actualizando rol del usuario");
            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar actualizar rol del usuario: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar rol del usuario");
        }
    }

    @Override
    public void deleteUser(String documentNumber) {
        log.info("Inicio de validaciones de negocio para eliminar usuario");
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new BusinessException("El número de documento del usuario es obligatorio");
        }

        securityUtils.validateOwnershipOrAdmin(documentNumber,
                Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + documentNumber);

        User existingUser = userRepository.findById(documentNumber)
                .orElseThrow(() -> new BusinessException("No existe usuario registrado con el número de documento: " + documentNumber));

        try {
            log.info("Eliminando usuario");
            userRepository.delete(existingUser);
        } catch (Exception e) {
            log.error("Error al intentar eliminar usuario: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar eliminar usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try{
            log.info("Obteniendo lista de usuarios");
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("Error al intentar consultar lista de usuarios: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar consultar lista de usuarios");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByDocumentNumber(String documentNumber) {
        securityUtils.validateOwnershipOrAdmin(documentNumber,
                Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + documentNumber);

        try{
            log.info("Obteniendo usuario autenticado");
            return userRepository.findById(documentNumber).orElseThrow(() -> new BusinessException("No se ha encontrado un usuario con número de documento ".concat(documentNumber)));
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            log.error("Error al intentar consultar usuario", e);
            throw new SystemException("Error al intentar consultar usuario");
        }
    }

    @Override
    public LoginResponse login(LoginDto loginDto) {
        try {
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

            // Las cuentas que entran con Google no tienen contraseña local: se evita validar
            // credenciales contra un hash inexistente y se orienta al usuario al botón de Google.
            if (user.getAuthProvider() == AuthProvider.GOOGLE) {
                log.info("Intento de login con contraseña sobre cuenta Google: {}", LogMask.doc(user.getDocumentNumber()));
                return LoginResponse.builder()
                        .match(false)
                        .detail("Esta cuenta ingresa con Google")
                        .build();
            }

            // Bloqueo automático temporal por intentos fallidos
            if (user.getLockedUntil() != null && user.getLockedUntil().after(new Date())) {
                log.info("Intento de login de cuenta con bloqueo temporal: {}", LogMask.doc(user.getDocumentNumber()));
                return LoginResponse.builder()
                        .match(false)
                        .detail("Cuenta bloqueada temporalmente por intentos fallidos. Intente más tarde")
                        .build();
            }

            boolean match = encryption.matches(loginDto.getPassword(), user.getPassword());
            log.info("Resultado de validación de credenciales para {}: {}",
                    LogMask.doc(user.getDocumentNumber()), match ? "exitoso" : "fallido");

            if (!match) {
                registerFailedAttempt(user);
                return LoginResponse.builder()
                        .match(false)
                        .detail("Credenciales incorrectas")
                        .build();
            }

            // Bloqueo permanente por parte de un administrador
            if (Boolean.TRUE.equals(user.getLocked())) {
                log.info("Intento de login de usuario bloqueado: {}", LogMask.doc(user.getDocumentNumber()));
                return LoginResponse.builder()
                        .match(false)
                        .detail("El usuario se encuentra bloqueado. Contacte al administrador")
                        .build();
            }

            // Login correcto: se reinicia el contador de intentos fallidos
            if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() != 0 || user.getLockedUntil() != null) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }

            String roleCode = user.getRole().getRoleCode();
            String token = jwtService.generateToken(user.getDocumentNumber(), roleCode);

            return LoginResponse.builder()
                    .match(true)
                    .detail("Login correcto")
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationSeconds())
                    .documentNumber(user.getDocumentNumber())
                    .name(user.getName())
                    .roleCode(roleCode)
                    .build();
        } catch (Exception e) {
            log.error("Error al intentar validar login de usuario: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar validar login de usuario");
        }
    }

    @Override
    public LoginResponse loginWithGoogle(GoogleLoginDto googleLoginDto) {
        try {
            GoogleUserInfo info = verifyGoogle(googleLoginDto != null ? googleLoginDto.getGoogleToken() : null);

            User user = userRepository.findByEmail(info.email()).orElse(null);

            // Caso B: el correo no existe todavía. No se crea la cuenta aquí porque faltan datos
            // que Google no entrega (documento, celular, fecha de nacimiento). El frontend
            // muestra "completa tu perfil" y luego llama al registro con Google.
            if (user == null) {
                log.info("Login con Google de correo no registrado: registro incompleto");
                return LoginResponse.builder()
                        .match(false)
                        .isNewUser(true)
                        .detail("REGISTRO_INCOMPLETO")
                        .email(info.email())
                        .name(info.givenName())
                        .lastName(info.familyName())
                        .build();
            }

            // Caso A: el correo ya existe. Se respetan los mismos bloqueos que el login normal,
            // aunque entre por Google. Las cuentas LOCAL se dejan entrar sin alterar su provider
            // (conservan su login con contraseña): el correo viene verificado por Google.
            LoginResponse blocked = lockResponseOrNull(user);
            if (blocked != null) return blocked;

            log.info("Login con Google exitoso para {}", LogMask.doc(user.getDocumentNumber()));
            return buildSuccessLogin(user);
        } catch (BusinessException | SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar validar login con Google: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar validar login con Google");
        }
    }

    @Override
    public LoginResponse registerWithGoogle(GoogleRegisterDto dto) {
        if (dto == null)
            throw new BusinessException("No se recibieron datos para el registro con Google");

        GoogleUserInfo info = verifyGoogle(dto.getGoogleToken());

        if (dto.getDocumentNumber() == null || dto.getDocumentNumber().isBlank())
            throw new BusinessException("El número de documento es obligatorio");
        if (dto.getDocumentType() == null)
            throw new BusinessException("El tipo de documento es obligatorio");
        if (dto.getCelNumber() == null || dto.getCelNumber().isBlank())
            throw new BusinessException("El número de celular del usuario es obligatorio");
        if (dto.getBirthDate() == null)
            throw new BusinessException("La fecha de nacimiento del usuario es obligatoria");

        try {
            Role role = roleRepository.findById(defaultRoleCode)
                    .orElseThrow(() -> new SystemException("No está configurado el rol por defecto: " + defaultRoleCode));

            // Si el correo ya existe, no es un registro: el usuario debe iniciar sesión con Google.
            if (userRepository.existsByEmail(info.email()))
                throw new BusinessException("Ya existe una cuenta con este correo. Inicia sesión con Google");

            // Mensaje genérico para no revelar qué documentos están registrados (anti-enumeración)
            if (userRepository.existsByDocumentNumber(dto.getDocumentNumber()))
                throw new BusinessException("No fue posible completar el registro con los datos proporcionados");

            // Nombre, apellido y correo se toman del token verificado, no del cliente.
            User user = User.builder()
                    .documentNumber(dto.getDocumentNumber())
                    .documentType(dto.getDocumentType())
                    .role(role)
                    .name(info.givenName())
                    .lastName(info.familyName())
                    .email(info.email())
                    .celNumber(dto.getCelNumber())
                    .birthDate(dto.getBirthDate())
                    .password(null)
                    .authProvider(AuthProvider.GOOGLE)
                    .googleSub(info.sub())
                    .locked(false)
                    .failedLoginAttempts(0)
                    .createdAt(new Date())
                    .build();

            log.info("Registrando usuario con Google");
            user = userRepository.saveAndFlush(user);

            return buildSuccessLogin(user);
        } catch (BusinessException | SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar usuario con Google: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar registrar usuario con Google");
        }
    }

    /**
     * Verifica el ID token de Google y exige que el correo esté verificado.
     * Devuelve los datos del usuario tomados del token firmado.
     */
    private GoogleUserInfo verifyGoogle(String idToken) {
        GoogleUserInfo info = googleTokenVerifier.verify(idToken);
        if (info.email() == null || info.email().isBlank())
            throw new BusinessException("El token de Google no contiene un correo");
        if (!info.emailVerified())
            throw new BusinessException("El correo de la cuenta de Google no está verificado");
        return info;
    }

    /**
     * Devuelve una respuesta de bloqueo si la cuenta está bloqueada (temporal por intentos
     * fallidos o permanente por un administrador), o null si puede continuar. Centraliza las
     * verificaciones de bloqueo para que apliquen igual al login normal y al login con Google.
     */
    private LoginResponse lockResponseOrNull(User user) {
        if (user.getLockedUntil() != null && user.getLockedUntil().after(new Date())) {
            log.info("Acceso bloqueado temporalmente por intentos fallidos: {}", LogMask.doc(user.getDocumentNumber()));
            return LoginResponse.builder()
                    .match(false)
                    .detail("Cuenta bloqueada temporalmente por intentos fallidos. Intente más tarde")
                    .build();
        }
        if (Boolean.TRUE.equals(user.getLocked())) {
            log.info("Acceso de usuario bloqueado por administrador: {}", LogMask.doc(user.getDocumentNumber()));
            return LoginResponse.builder()
                    .match(false)
                    .detail("El usuario se encuentra bloqueado. Contacte al administrador")
                    .build();
        }
        return null;
    }

    /**
     * Construye la respuesta de login exitoso con un JWT recién emitido.
     */
    private LoginResponse buildSuccessLogin(User user) {
        String roleCode = user.getRole().getRoleCode();
        String token = jwtService.generateToken(user.getDocumentNumber(), roleCode);

        return LoginResponse.builder()
                .match(true)
                .isNewUser(false)
                .detail("Login correcto")
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .documentNumber(user.getDocumentNumber())
                .name(user.getName())
                .roleCode(roleCode)
                .build();
    }

    /**
     * Incrementa el contador de intentos fallidos y, al superar el umbral, bloquea
     * la cuenta temporalmente. Complementa al límite por IP (rate limiting), que un
     * atacante con varias IP podría esquivar.
     */
    private void registerFailedAttempt(User user) {
        int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;

        if (attempts >= maxFailedAttempts) {
            user.setLockedUntil(new Date(System.currentTimeMillis() + lockoutMinutes * 60_000));
            user.setFailedLoginAttempts(0);
            log.info("Cuenta bloqueada temporalmente por intentos fallidos: {}", LogMask.doc(user.getDocumentNumber()));
        } else {
            user.setFailedLoginAttempts(attempts);
        }
        userRepository.save(user);
    }

    private void applyProfileValidation(User user) {
        if (user.getDocumentType() == null)
            throw new BusinessException("El tipo de documento es obligatorio");

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
    }

}
