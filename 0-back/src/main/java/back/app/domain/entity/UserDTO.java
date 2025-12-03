package back.app.domain.entity;

import back.app.data.model.user.ERole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String nom;
    private LocalDateTime createdAt;
    private String prenom;
    private String email;
    private String tel;
    private ERole role;
}

