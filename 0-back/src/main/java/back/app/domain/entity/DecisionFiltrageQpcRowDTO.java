package back.app.domain.entity;

import back.app.data.model.qpc.EJuridiction;
import back.app.data.model.qpc.ENiveauFiltrage;
import back.app.data.model.qpc.EOrdreJuridictionnel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(requiredProperties = {
        "id",
        "ordreJuridictionnel",
        "juridiction",
        "niveauFiltrage",
        "niveauCompetence",
        "dateFiltrage",
        "formationJugement",
        "numeroDecision",
        "decisionRenvoi",
        "decisionNonRenvoi"
})
public class DecisionFiltrageQpcRowDTO {

    private Long id;

    // 1. ordre_juridictionnel : enum
    private EOrdreJuridictionnel ordreJuridictionnel;

    // 2. juridiction : enum
    private EJuridiction juridiction;

    // 5. niveau_filtrage : enum
    private ENiveauFiltrage niveauFiltrage;

    // 7. niveau_competence : anciennement liste déroulante
    private String niveauCompetence;

    // 8. date_filtrage : date
    private LocalDate dateFiltrage;

    // 9. formation_jugement : texte (ou enum plus tard si tu veux)
    private String formationJugement;

    // 11. numero_decision : texte
    private String numeroDecision;


    // 18. decision_renvoi : anciennement liste déroulante
    private String decisionRenvoi;

    // 19. decision_non_renvoi : anciennement liste déroulante
    private String decisionNonRenvoi;
}