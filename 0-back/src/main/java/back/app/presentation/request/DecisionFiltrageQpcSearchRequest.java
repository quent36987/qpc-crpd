package back.app.presentation.request;


import back.app.data.model.qpc.EJuridiction;
import back.app.data.model.qpc.ENiveauFiltrage;
import back.app.data.model.qpc.EOrdreJuridictionnel;
import lombok.Data;

/**
 * DTO de recherche pour les décisions de filtrage QPC.
 */
@Data
public class DecisionFiltrageQpcSearchRequest {

    // Énums
    private EOrdreJuridictionnel ordreJuridictionnel;
    private EJuridiction juridiction;
    private ENiveauFiltrage niveauFiltrage;

    // Listes déroulantes (IDs)
    private Long chambreSousSectionId;
    private Long numeroChambresReuniesId;
    private Long niveauCompetenceId;
    private Long matiereId;
    private Long qualiteDemandeurId;
    private Long qualitePreciseDemandeurId;
    private Long decisionRenvoiId;
    private Long decisionNonRenvoiId;
    private Long applicationTheorieChangementCirconstancesId;
}