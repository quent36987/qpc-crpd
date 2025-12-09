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
@Schema(requiredProperties = { "id", "referenceDecisionConseil", "numero", "dateDecision", "origineQpc", "referenceDecisionTransmission",
        "qualiteDemandeur", "identiteDemandeur", "dispositionsLegislativesContestees", "typeDispositionLegislative",
        "matiere", "dispositifDecisionCc", "dateAbrogationDifferee", "delaiAvantAbrogationMois", "traitementEffetsPasses",
        "nombreMembresSieges", "demandeRecusation", "deport", "nomMembreDeporteOuRecuse", "oralite",
        "applicationTheorieChangementCirconstances", "nombreDroitsLibertesInvoques", "nombreInterventionsAdmises",
        "qualiteTiersIntervention", "nombrePersonnesPhysiques", "identitePersonnesPhysiques", "nombreAssociations",
        "identiteAssociations", "nombreEntreprises", "identiteEntreprises", "nombreSyndicatsApOp",
        "identiteSyndicatsApOp", "nombreCollectivitesTerritoriales", "identiteCollectivites", "reserveOpportunite",
        "reserveIncompetenceConseil", "priseEnCompteInterpretationJurisprudentielle", "techniquesControle",
        "motifInconstitutionnalite", "autresRemarques", "caractereNotableDecision", "createdAt", "updatedAt",
        "droitsLibertes" })
public class DecisionQpcCcDTO {

    private Long id;

    // 1. reference_decision_conseil
    private String referenceDecisionConseil;

    // 2. numero
    private String numero;

    // 3. date_decision
    private LocalDate dateDecision;

    // 4. origine_qpc : valeur de la liste déroulante
    private String origineQpc;

    // 5. reference_decision_transmission
    private String referenceDecisionTransmission;

    // 6. qualite_demandeur : valeur de la liste déroulante
    private String qualiteDemandeur;

    // 7. identite_demandeur
    private String identiteDemandeur;

    // 8. dispositions_legislatives_contestees
    private String dispositionsLegislativesContestees;

    // 9. type_disposition_legislative : valeur de la liste déroulante
    private String typeDispositionLegislative;

    // 10. matiere : valeur de la liste déroulante
    private String matiere;

    // 11. dispositif_decision_cc : valeur de la liste déroulante
    private String dispositifDecisionCc;

    // 12. date_abrogation_differee
    private LocalDate dateAbrogationDifferee;

    // 13. delai_avant_abrogation_mois
    private Integer delaiAvantAbrogationMois;

    // 14. traitement_effets_passes : valeur de la liste déroulante
    private String traitementEffetsPasses;

    // 15. nombre_membres_sieges
    private Integer nombreMembresSieges;

    // 16. demande_recusation
    private Integer demandeRecusation;

    // 17. deport
    private Integer deport;

    // 18. nom_membre_deporte_ou_recuse
    private String nomMembreDeporteOuRecuse;

    // 19. oralite : valeur de la liste déroulante
    private Boolean oralite;

    // 20. application_theorie_changement_circonstances : booléen
    private Boolean applicationTheorieChangementCirconstances;

    // 21. nombre_droits_libertes_invoques
    private Integer nombreDroitsLibertesInvoques;

    // 22. nombre_interventions_admises
    private Integer nombreInterventionsAdmises;

    // 23. qualite_tiers_intervention : valeur de la liste déroulante
    private String qualiteTiersIntervention;

    // 24. nombre_personnes_physiques
    private Integer nombrePersonnesPhysiques;

    // 25. identite_personnes_physiques
    private String identitePersonnesPhysiques;

    // 26. nombre_associations
    private Integer nombreAssociations;

    // 27. identite_associations
    private String identiteAssociations;

    // 28. nombre_entreprises
    private Integer nombreEntreprises;

    // 29. identite_entreprises
    private String identiteEntreprises;

    // 30. nombre_syndicats_ap_op
    private Integer nombreSyndicatsApOp;

    // 31. identite_syndicats_ap_op
    private String identiteSyndicatsApOp;

    // 32. nombre_collectivites_territoriales
    private Integer nombreCollectivitesTerritoriales;

    // 33. identite_collectivites
    private String identiteCollectivites;

    // 34. reserve_opportunite
    private Boolean reserveOpportunite;

    // 35. reserve_incompetence_conseil : valeur de la liste déroulante
    private String reserveIncompetenceConseil;

    // 36. prise_en_compte_interpretation_jurisprudentielle
    private Boolean priseEnCompteInterpretationJurisprudentielle;

    // 37. techniques_controle
    private String techniquesControle;

    // 38. motif_inconstitutionnalite
    private String motifInconstitutionnalite;

    // 39. autres_remarques
    private String autresRemarques;

    // 40. caractere_notable_decision
    private Boolean caractereNotableDecision;

    // Métadonnées
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Liste de droits / libertés associés
    private List<DroitLiberteDTO> droitsLibertes;
}