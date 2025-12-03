package back.app.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(requiredProperties = { "id", "champ", "valeur", "actif" })
public class ListeDeroulanteDTO {

    private Long id;

    private String champ;

    private String valeur;

    private Boolean actif;
}
