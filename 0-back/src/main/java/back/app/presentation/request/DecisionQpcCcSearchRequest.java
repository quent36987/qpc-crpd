package back.app.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête de recherche pour les décisions QPC du Conseil constitutionnel",
        requiredProperties = {"numero", "referenceDecisionConseil", "identiteDemandeur", "dispositionsLegislativesContestees",
        "nomMembreDeporteOuRecuse", "remarque", "originesQpcIds", "qualitesDemandeurIds",
        "typesDispositionLegislativeIds", "matieresIds", "dispositifsDecisionCcIds",
        "traitementsEffetsPassesIds", "qualitesTiersInterventionIds", "reservesIncompetenceConseilIds",
        "droitsLibertesIds", "techniquesControle", "motifsInconstitutionnalite"})
public class DecisionQpcCcSearchRequest {

    // ---------------------------------------------------------------------
    // Champs texte simples (recherche 'contient')
    // ---------------------------------------------------------------------

    /** Numéro de la décision (numero) */
    private String numero;

    /** Référence de la décision du Conseil (reference_decision_conseil) */
    private String referenceDecisionConseil;

    /** Référence de la décision de transmission (Cass / CE) */
    private String referenceDecisionTransmission;

    /** Identité du ou des demandeurs (identite_demandeur) */
    private String identiteDemandeur;

    /** Dispositions législatives contestées (dispositions_legislatives_contestees) */
    private String dispositionsLegislativesContestees;

    /** Nom du membre déporté ou récusé (nom_membre_deporte_ou_recuse) */
    private String nomMembreDeporteOuRecuse;

    /** Recherche texte dans les remarques (autres_remarques) */
    private String remarque;


    // ---------------------------------------------------------------------
    // Dates
    // ---------------------------------------------------------------------

    /** Date de décision - Du (date_decision) */
    private LocalDate dateDecisionFrom;

    /** Date de décision - Au (date_decision) */
    private LocalDate dateDecisionTo;

    /** Date d’abrogation différée - Du (date_abrogation_differee) */
    private LocalDate dateAbrogationDiffereeFrom;

    /** Date d’abrogation différée - Au (date_abrogation_differee) */
    private LocalDate dateAbrogationDiffereeTo;


    // ---------------------------------------------------------------------
    // Listes déroulantes (IDs de ListeDeroulanteModel)
    // ---------------------------------------------------------------------

    /** Origine QPC (decision_qpc_cc.origine_qpc) */
    private List<Long> originesQpcIds;

    /** Qualité du demandeur (decision_qpc_cc.qualite_demandeur) */
    private List<Long> qualitesDemandeurIds;

    /** Type de disposition législative contestée (decision_qpc_cc.type_disposition_legislative) */
    private List<Long> typesDispositionLegislativeIds;

    /** Matière (decision_qpc_cc.matiere) */
    private List<Long> matieresIds;

    /** Dispositif de la décision du CC (decision_qpc_cc.dispositif_decision_cc) */
    private List<Long> dispositifsDecisionCcIds;

    /** Traitement des effets passés (decision_qpc_cc.traitement_effets_passes) */
    private List<Long> traitementsEffetsPassesIds;

    /** Qualité du/des tiers intervenants (decision_qpc_cc.qualite_tiers_intervention) */
    private List<Long> qualitesTiersInterventionIds;

    /** Réserve d’incompétence du Conseil (decision_qpc_cc.reserve_incompetence_conseil) */
    private List<Long> reservesIncompetenceConseilIds;


    // ---------------------------------------------------------------------
    // Droits & libertés (many-to-many)
    // ---------------------------------------------------------------------

    /** Droit(s) et liberté(s) invoqué(s) (IDs de DroitLiberteModel) */
    private List<Long> droitsLibertesIds;


    // ---------------------------------------------------------------------
    // Délai avant abrogation (en mois) : bornes
    // ---------------------------------------------------------------------

    /** Délai avant abrogation - min (delai_avant_abrogation_mois) */
    private Integer delaiAvantAbrogationMin;

    /** Délai avant abrogation - max (delai_avant_abrogation_mois) */
    private Integer delaiAvantAbrogationMax;


    // ---------------------------------------------------------------------
    // Nombre d’interventions admises & membres / DL invoqués : bornes
    // ---------------------------------------------------------------------

    /** Nombre d’interventions admises - min (nombre_interventions_admises) */
    private Integer nombreInterventionsMin;

    /** Nombre d’interventions admises - max (nombre_interventions_admises) */
    private Integer nombreInterventionsMax;

    /** Nombre de membres ayant siégé - min (nombre_membres_sieges) */
    private Integer nombreMembresSiegesMin;

    /** Nombre de membres ayant siégé - max (nombre_membres_sieges) */
    private Integer nombreMembresSiegesMax;

    /** Nombre de droits et libertés invoqués - min (nombre_droits_libertes_invoques) */
    private Integer nombreDroitsLibertesMin;

    /** Nombre de droits et libertés invoqués - max (nombre_droits_libertes_invoques) */
    private Integer nombreDroitsLibertesMax;


    // ---------------------------------------------------------------------
    // Répartition par type de parties (bornes)
    // ---------------------------------------------------------------------

    /** Personnes physiques - min (nombre_personnes_physiques) */
    private Integer nombrePersonnesPhysiquesMin;

    /** Personnes physiques - max (nombre_personnes_physiques) */
    private Integer nombrePersonnesPhysiquesMax;

    /** Associations - min (nombre_associations) */
    private Integer nombreAssociationsMin;

    /** Associations - max (nombre_associations) */
    private Integer nombreAssociationsMax;

    /** Entreprises - min (nombre_entreprises) */
    private Integer nombreEntreprisesMin;

    /** Entreprises - max (nombre_entreprises) */
    private Integer nombreEntreprisesMax;

    /** Syndicats / AP / OP - min (nombre_syndicats_ap_op) */
    private Integer nombreSyndicatsApOpMin;

    /** Syndicats / AP / OP - max (nombre_syndicats_ap_op) */
    private Integer nombreSyndicatsApOpMax;

    /** Collectivités territoriales - min (nombre_collectivites_territoriales) */
    private Integer nombreCollectivitesTerritorialesMin;

    /** Collectivités territoriales - max (nombre_collectivites_territoriales) */
    private Integer nombreCollectivitesTerritorialesMax;


    // ---------------------------------------------------------------------
    // Booléens
    // ---------------------------------------------------------------------

    /**
     * Demande de récusation :
     * - true  => au moins une demande (demande_recusation > 0)
     * - false => aucune (demande_recusation = 0 ou null)
     * - null  => pas de filtre
     */
    private Boolean demandeRecusation;

    /**
     * Déport :
     * - true  => au moins un déport (deport > 0)
     * - false => aucun (deport = 0 ou null)
     * - null  => pas de filtre
     */
    private Boolean deport;

    /** Application de la théorie du changement de circonstances */
    private Boolean applicationTheorieChangementCirconstances;

    /** Réserve d’opportunité */
    private Boolean reserveOpportunite;

    /** Prise en compte d’une interprétation jurisprudentielle */
    private Boolean interpretationJurisprudence;

    /** Caractère notable de la décision */
    private Boolean caractereNotableDecision;

    /** Oralité (champ booléen dans ton modèle modifié) */
    private Boolean oralite;


    // ---------------------------------------------------------------------
    // Techniques / motifs (multi-select côté front, stockés en texte)
    // ---------------------------------------------------------------------

    /**
     * Techniques de contrôle :
     * par exemple des valeurs codées 'proportionnalite', 'controle_concret', etc.
     * À toi de décider si tu fais un "contient un des éléments" dans la Spec.
     */
    private String techniquesControle;

    /**
     * Motifs d’inconstitutionnalité :
     * idem, valeurs textuelles que tu peux parser / matcher dans le champ motif_inconstitutionnalite.
     */
    private String motifsInconstitutionnalite;
}
