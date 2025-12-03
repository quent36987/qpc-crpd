package back.app.data.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 128)
    private String nom;

    @NotBlank
    @Size(max = 128)
    private String prenom;

    @NotBlank
    @Size(max = 128)
    @Email
    private String email;

    @NotBlank
    @Size(max = 128)
    private String password;

    // @NotBlank ne marche pas avec LocalDateTime ou LocalDate, @Past garantie une valeur non nulle et dans le passé
    @Past
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Size(max = 20)
    private String tel;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ERole role = ERole.ROLE_USER;


    private String hash;

    public UserModel(String nom, String prenom, String email, String password, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.tel = tel;
        this.createdAt = LocalDateTime.now();
    }

    public UserModel(Long id) {
        this.id = id;
    }
}