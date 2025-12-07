package back.app.presentation.request;


import back.app.data.model.qpc.EJuridiction;
import back.app.data.model.qpc.ENiveauFiltrage;
import back.app.data.model.qpc.EOrdreJuridictionnel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de recherche pour les décisions de filtrage QPC.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête de recherche pour les décisions de filtrage QPC",
        requiredProperties = { "numeroDecision", "reference", "juridictions", "niveauxFiltrage",
                "dateFiltrageFrom", "dateFiltrageTo", "formationsJugement", "chambresSousSectionIds",
                "numerosChambresReuniesIds", "applicationTheorieChangementCirconstancesIds",
                "originesJuridictionnellesQpc", "matieresIds", "droitsLibertesIds", "codes" })
public class DecisionFiltrageQpcSearchRequest {

    private String numeroDecision;

    private String reference;

    private List<EJuridiction> juridictions;

    private List<ENiveauFiltrage> niveauxFiltrage;

    private LocalDate dateFiltrageFrom;

    private LocalDate dateFiltrageTo;

    private List<String> formationsJugement;

    private List<Long> chambresSousSectionIds;

    private List<Long> numerosChambresReuniesIds;

    private List<Long> applicationTheorieChangementCirconstancesIds;

    private List<String> originesJuridictionnellesQpc;

    private List<Long> matieresIds;

    private List<Long> droitsLibertesIds;

    private List<String> codes;
}