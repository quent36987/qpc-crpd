package back.app.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordConfirmRequest {
    @NotBlank
    private String hash;
    @NotBlank @Size(min = 8)
    private String newPassword;
}
