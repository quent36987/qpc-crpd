package back.app.domain.entity;

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
@Schema(requiredProperties = { "id", "referenceDecisionConseil", "numero", "dateDecision", "origineQpc", "matiere", "dispositifDecisionCc", "traitementEffetsPasses", "motifInconstitutionnalite", "qualiteDemandeur", "identiteDemandeur" })
public class DecisionQpcCcRowDTO {

    private Long id;

    // 1. reference_decision_conseil
    private String referenceDecisionConseil;

    // 2. numero
    private String numero;

    // 3. date_decision
    private LocalDate dateDecision;

    // 4. origine_qpc : valeur de la liste déroulante
    private String origineQpc;

    // 10. matiere : valeur de la liste déroulante
    private String matiere;

    // 11. dispositif_decision_cc : valeur de la liste déroulante
    private String dispositifDecisionCc;

    // 14. traitement_effets_passes : valeur de la liste déroulante
    private String traitementEffetsPasses;

    // 38. motif_inconstitutionnalite
    private String motifInconstitutionnalite;

    // 6. qualite_demandeur : valeur de la liste déroulante
    private String qualiteDemandeur;

    // 7. identite_demandeur
    private String identiteDemandeur;
}