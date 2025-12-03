package back.app.presentation.request;

import lombok.Data;

/**
 * DTO de recherche pour les décisions QPC CC.
 * Tous les champs sont optionnels :
 * - null => pas de filtre sur ce critère
 */
@Data
public class DecisionQpcCcSearchRequest {

    private Long origineQpcId;
    private Long qualiteDemandeurId;
    private Long typeDispositionLegislativeId;
    private Long matiereId;
    private Long dispositifDecisionCcId;
    private Long traitementEffetsPassesId;
    private Long oraliteId;
    private Long qualiteTiersInterventionId;
    private Long reserveIncompetenceConseilId;

}