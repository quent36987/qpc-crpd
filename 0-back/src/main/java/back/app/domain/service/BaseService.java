package back.app.domain.service;

import back.app.data.model.ERole;
import back.app.utils.errors.RestError;
import back.app.utils.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseService {
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getRole() == ERole.ROLE_ADMIN || userDetails.getRole() == ERole.ROLE_SUPERVISEUR;
        }

        throw RestError.USER_NOT_FOUND.get();
    }
}
