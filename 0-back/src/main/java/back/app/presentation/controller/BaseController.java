package back.app.presentation.controller;

import back.app.utils.errors.RestError;
import back.app.utils.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }
}
