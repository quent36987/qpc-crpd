package back.app.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
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
@Schema(description = "Requête de recherche pour les décisions QPC du Conseil Constitutionnel",
        requiredProperties = { "numero", "referenceDecisionConseil", "dateDecisionFrom", "dateDecisionTo",
                "originesQpcIds", "qualitesDemandeurIds", "matieresIds", "dispositifsDecisionCcIds",
                "traitementsEffetsPassesIds", "qualitesTiersInterventionIds", "reservesIncompetenceConseilIds",
                "droitsLibertesIds", "delaiAvantAbrogationExpression", "nombreInterventionsExact",
                "nombreInterventionsMin", "nombreInterventionsMax", "nombreMembresSieges", "demandeRecusation",
                "deport", "applicationTheorieChangementCirconstances", "reserveOpportunite",
                "interpretationJurisprudence", "caractereNotableDecision", "oralite", "techniquesControle",
                "motifsInconstitutionnalite" })
public class DecisionQpcCcSearchRequest {

    // --- Champs texte simples ---

    /** Numéro (champ "numero") */
    private String numero;

    /** Réf. décision du CC (referenceDecisionConseil) */
    private String referenceDecisionConseil;

    /** Réf. décision de transmission */
    private String referenceDecisionTransmission;

    /** Identité(s) des auteurs de la QPC */
    private String identiteDemandeur;

    /** Nom du membre déporté / récusé */
    private String nomMembreDeporteOuRecuse;

    /** Recherche texte dans les remarques */
    private String remarque;


    // --- Dates ---

    /** Date de décision - De */
    private LocalDate dateDecisionFrom;

    /** Date de décision - À */
    private LocalDate dateDecisionTo;


    // --- Listes déroulantes (IDs de ListeDeroulanteModel) ---

    /** Origine QPC (origine_qpc) */
    private List<Long> originesQpcIds;

    /** Catégorie des auteurs de QPC (qualite_demandeur) */
    private List<Long> qualitesDemandeurIds;

    /** Matière (matiere) */
    private List<Long> matieresIds;

    /** Dispositif de la décision du CC (dispositif_decision_cc) */
    private List<Long> dispositifsDecisionCcIds;

    /** Traitement des effets passés (traitement_effets_passes) */
    private List<Long> traitementsEffetsPassesIds;

    /** Qualité du/des tiers intervenants (qualite_tiers_intervention) */
    private List<Long> qualitesTiersInterventionIds;

    /** Réserve d’incompétence du Conseil (reserve_incompetence_conseil) */
    private List<Long> reservesIncompetenceConseilIds;


    // --- Droits & libertés (many-to-many) ---

    /** Droit(s) et liberté(s) invoqué(s) */
    private List<Long> droitsLibertesIds;


    // --- Délai avant abrogation (en mois) ---

    /**
     * Expression telle que saisie dans le champ texte :
     * exemple : ">6", "<=3", "=12", "<>0"
     * On pourra parser cette string côté back pour construire la Spec.
     */
    private String delaiAvantAbrogationExpression;


    // --- Nombre d’interventions admises ---

    /** Nombre exact d'interventions admises */
    private Integer nombreInterventionsExact;

    /** Nombre d'interventions admises minimum (intervalle) */
    private Integer nombreInterventionsMin;

    /** Nombre d'interventions admises maximum (intervalle) */
    private Integer nombreInterventionsMax;


    // --- Autres champs numériques / booléens ---

    /** Nombre de membres siégeant (6,7,8,9,10) */
    private Integer nombreMembresSieges;

    /**
     * Demande de récusation :
     * - true  => au moins une demande (demande_recusation > 0)
     * - false => aucune demande (demande_recusation = 0 ou null)
     */
    private Boolean demandeRecusation;

    /**
     * Déport :
     * - true  => au moins un déport (deport > 0)
     * - false => aucun (deport = 0 ou null)
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

    /**
     * Oralité (ton modèle a un ListeDeroulanteModel "oralite"
     */
    private Boolean oralite;


    // --- Techniques / motifs (si stockés en texte) ---

    /** Techniques de contrôle (multi-select) */
    private List<String> techniquesControle;

    /** Motifs d'inconstitutionnalité (multi-select) */
    private List<String> motifsInconstitutionnalite;
}
