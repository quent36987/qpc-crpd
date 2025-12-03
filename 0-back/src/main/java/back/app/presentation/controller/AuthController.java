package back.app.presentation.controller;

import back.app.data.model.user.ERole;
import back.app.data.model.user.UserModel;
import back.app.data.repository.interfaces.UserRepository;
import back.app.domain.entity.UserDTO;
import back.app.domain.mapper.UserMapper;
import back.app.domain.service.UserService;
import back.app.presentation.request.LoginRequest;
import back.app.presentation.request.ResetPasswordConfirmRequest;
import back.app.presentation.request.ResetPasswordRequest;
import back.app.presentation.request.SignupRequest;
import back.app.presentation.response.MessageResponse;
import back.app.utils.errors.RestError;
import back.app.utils.security.jwt.JwtUtils;
import back.app.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth", description = "Authentication operations")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, UserMapper userMapper,
                          UserService userService,
                          PasswordEncoder encoder, JwtUtils jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    // ===========================
    //         AUTH / TOKEN
    // ===========================

    @PostMapping(path = "/signin")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<UserDTO> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        UserDTO user = userService.getUserByEmail(userDetails.getEmail());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(user);
    }

    @PostMapping(path ="/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new user")
    public UserDTO registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (signUpRequest.getEmail().isEmpty() || signUpRequest.getPassword().isEmpty()) {
            throw RestError.BAD_REQUEST.get();
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw RestError.EMAIL_ALREADY_EXISTS.get(signUpRequest.getEmail());
        }

        // Create new user's account
        UserModel user = new UserModel(
                signUpRequest.getNom(),
                signUpRequest.getPrenom(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getTel()
        );

        user.setRole(ERole.ROLE_USER);

        return userMapper.toDTO(userRepository.save(user));
    }

    @PostMapping(path = "/signout", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign out a user")
    public ResponseEntity<MessageResponse> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("User signed out successfully!"));
    }

    @PostMapping(path = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> refreshToken(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie newAccessToken = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newAccessToken.toString())
                .body(true);
    }

    // ===========================
    //          CURRENT USER
    // ===========================

    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the current user")
    @PreAuthorize("hasAuthority('USER_LIRE')")
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userService.getUserByEmail(userDetails.getEmail());
    }

    // ===========================
    //      RESET PASSWORD FLOW
    // ===========================

    @Operation(summary = "Demander la réinitialisation du mot de passe")
    @PostMapping("/reset-password")
    public void requestPasswordReset(@Valid @RequestBody ResetPasswordRequest dto) {
        userService.requestPasswordReset(dto.getEmail());
    }

    @Operation(summary = "Confirmer la réinitialisation du mot de passe")
    @PostMapping("/reset-password/confirm")
    public void confirmPasswordReset(@Valid @RequestBody ResetPasswordConfirmRequest dto) {
        userService.confirmPasswordReset(dto.getHash(), dto.getNewPassword());
    }
}
