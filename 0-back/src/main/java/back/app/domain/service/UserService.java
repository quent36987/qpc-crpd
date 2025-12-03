package back.app.domain.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import back.app.domain.entity.UserDTO;
import back.app.domain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import back.app.data.model.user.ERole;
import back.app.data.model.user.UserModel;
import back.app.data.repository.UserRepositoryCustom;
import back.app.data.repository.interfaces.UserRepository;
import back.app.utils.errors.RestError;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Value("${back.front.url}")
    private String urlFront;

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userMapper.toDTO(userRepositoryCustom.getUserById(id));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        Optional<UserModel> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return userMapper.toDTO(user.get());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @Transactional
    public String resetPassword(String email) {
        log.info("Reset password for user with email {}", email);
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(encoder.encode(newPassword));

        userRepository.save(user);

        return newPassword;
    }

    @Transactional
    public void changePassword(String email, String password) {
        log.info("Change password for user with email {}", email);
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        user.setPassword(encoder.encode(password));

        userRepository.save(user);
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        log.info("Update user with id {}", userId);
        UserModel user = userRepository.findById(userId)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());

        if (userDTO.getEmail() != null) {
            if (userDTO.getRole().equals(ERole.ROLE_SUPERVISEUR)) {
                throw RestError.FORBIDDEN_MESSAGE.get("Interdit de donner le rôle de superviseur ou de modifier ce rôle.");
            }

            if (userDTO.getRole().equals(ERole.ROLE_ADMIN) && !isAdmin()) {
                throw RestError.FORBIDDEN_MESSAGE.get("Seul un administrateur peut donner le rôle d'administrateur.");
            }

            user.setRole(userDTO.getRole());
        }

        UserModel updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);

    }

    public UserDTO updateUserRole(Long userId, ERole newRole) {
        log.info("Update role for user with id {} to {}", userId, newRole);
        UserModel user = userRepository.findById(userId)
                .orElseThrow(RestError.USER_NOT_FOUND::get);
        user.setRole(newRole);
        UserModel updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }


    /**
     * Génère un hash, le stocke sur l’utilisateur, et lui envoie un mail.
     */
    public void requestPasswordReset(String email) {
        log.info("Password reset requested for email {}", email);
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        String hash = UUID.randomUUID().toString();
        user.setHash(hash);
        userRepository.save(user);

        // envoi du mail (objet et contenu à adapter)
        String subject = "Réinitialisation de votre mot de passe";
        String body = String.format("Bonjour %s,\n\n" +
                        "Pour réinitialiser votre mot de passe, veuillez cliquer sur ce lien :\n" +
                        urlFront + "/auth/reset?hash=%s\n\n" +
                        "Si vous n’êtes pas à l’origine de cette demande, ignorez ce message.",
                user.getPrenom(), hash);


        emailService.sendEmail(user.getEmail(), subject, body);
    }

    /**
     * Valide le hash, change le mot de passe, et clear le hash.
     */
    public void confirmPasswordReset(String hash, String newPassword) {
        log.info("Confirming password reset for hash {}", hash);
        UserModel user = userRepository.findByHash(hash)
                .orElseThrow(RestError.INVALID_HASH::get);

        user.setPassword(encoder.encode(newPassword));
        user.setHash(null);
        userRepository.save(user);
    }


    public UserModel createUser(String nom, String prenom, String email, String password, String tel) {
        log.info("Create user with name {} {} {}", nom, prenom, email);
        UserModel user = new UserModel(nom, prenom, email, encoder.encode(password), tel);
        user.setRole(ERole.ROLE_USER);
        return userRepository.save(user);
    }

    public UserModel updateRole(Long userId, ERole newRole) {
        log.info("Update role for user with id {} to {}", userId, newRole);
        UserModel user = userRepository.findById(userId)
                .orElseThrow(RestError.USER_NOT_FOUND::get);
        user.setRole(newRole);
        return userRepository.save(user);
    }
}
