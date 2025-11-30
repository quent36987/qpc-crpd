package back.app.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    @NotBlank
    @Size(max = 20, message = "Tel must be at most 20 characters")
    private String tel;
}
