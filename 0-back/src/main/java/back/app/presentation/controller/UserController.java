package back.app.presentation.controller;

import back.app.data.model.user.ERole;
import back.app.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import back.app.domain.entity.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User", description = "User operations")
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===========================
    //            GET
    // ===========================

    @Operation(summary = "Get my user details")
    @PreAuthorize("hasAuthority('USER_LIRE')")
    @GetMapping("/details")
    public UserDTO getMyUserDetails() {
        return userService.getUserById(getUserId());
    }

    @Operation(summary = "Get all users")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }


    // ===========================
    //        POST / PUT
    // ===========================

    @Operation(summary = "Update User")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @Operation(summary = "Update user Role")
    @PreAuthorize("hasAuthority('USER_MODIFIER_ROLES')")
    @PutMapping("/{id}/role")
    public UserDTO updateUserRole(@PathVariable Long id, @RequestParam ERole role) {
        return userService.updateUserRole(id, role);
    }


}
