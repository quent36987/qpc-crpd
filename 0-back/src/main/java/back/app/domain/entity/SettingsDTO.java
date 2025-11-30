package back.app.domain.entity;
import back.app.data.model.ESettingCategorie;
import back.app.data.model.ESettingNom;
import back.app.data.model.ESettingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(requiredProperties = {"id", "categorie", "nom", "valeur", "nomEnum", "type", "updatedAt", "updatedById", "updatedByNom", "updatedByPrenom"})
public class SettingsDTO {
    private Long id;

    private ESettingCategorie categorie;
    private String nom;
    private String description;
    private String valeur;
    private ESettingType type;
    private ESettingNom nomEnum;

    private LocalDateTime updatedAt;
    private Long updatedById;       // pour remonter un user côté écriture
    private String updatedByNom;    // pour affichage
    private String updatedByPrenom; // pour affichage
}
