package back.app.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "settings")
public class SettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 255)
    private ESettingCategorie categorie;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nom", nullable = false, unique = true, length = 255)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "nom_enum", length = 255)
    private ESettingNom nomEnum;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank
    @Size(max = 500)
    @Column(name = "valeur", nullable = false, length = 500)
    private String valeur;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ESettingType type;

    // Les deux suivants peuvent être NULL :
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private UserModel updatedBy;
}
