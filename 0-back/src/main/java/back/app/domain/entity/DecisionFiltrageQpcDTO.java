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
        "chambreSousSection",
        "numeroChambresReunies",
        "niveauFiltrage",
        "origineJuridictionnelleQpc",
        "niveauCompetence",
        "dateFiltrage",
        "formationJugement",
        "reference",
        "numeroDecision",
        "referenceDispositionsContestees",
        "loiOrigineDisposition",
        "matiere",
        "qualiteDemandeur",
        "qualitePreciseDemandeur",
        "identiteDemandeur",
        "decisionRenvoi",
        "decisionNonRenvoi",
        "applicationTheorieChangementCirconstances",
        "nombreDroitsNonMentionnes",
        "autresRemarques",
        "motsCles",
        "createdAt",
        "updatedAt",
        "droitsLibertes"
})
public class DecisionFiltrageQpcDTO {

    private Long id;

    // 1. ordre_juridictionnel : enum
    private EOrdreJuridictionnel ordreJuridictionnel;

    // 2. juridiction : enum
    private EJuridiction juridiction;

    // 3. chambre_sous_section : anciennement liste déroulante
    private String chambreSousSection;

    // 4. numero_chambres_reunies : anciennement liste déroulante
    private String numeroChambresReunies;

    // 5. niveau_filtrage : enum
    private ENiveauFiltrage niveauFiltrage;

    // 6. origine_juridictionnelle_qpc : texte
    private String origineJuridictionnelleQpc;

    // 7. niveau_competence : anciennement liste déroulante
    private String niveauCompetence;

    // 8. date_filtrage : date
    private LocalDate dateFiltrage;

    // 9. formation_jugement : texte (ou enum plus tard si tu veux)
    private String formationJugement;

    // 10. reference : texte
    private String reference;

    // 11. numero_decision : texte
    private String numeroDecision;

    // 12. reference_dispositions_contestees : texte
    private String referenceDispositionsContestees;

    // 13. loi_origine_disposition : texte
    private String loiOrigineDisposition;

    // 14. matiere : anciennement liste déroulante
    private String matiere;

    // 15. qualite_demandeur : anciennement liste déroulante
    private String qualiteDemandeur;

    // 16. qualite_precise_demandeur : anciennement liste déroulante
    private String qualitePreciseDemandeur;

    // 17. identite_demandeur : texte
    private String identiteDemandeur;

    // 18. decision_renvoi : anciennement liste déroulante
    private String decisionRenvoi;

    // 19. decision_non_renvoi : anciennement liste déroulante
    private String decisionNonRenvoi;

    // 20. application_theorie_changement_circonstances : anciennement liste déroulante
    private String applicationTheorieChangementCirconstances;

    // 22. nombre_droits_non_mentionnes : texte
    private String nombreDroitsNonMentionnes;

    // 23. autres_remarques
    private String autresRemarques;

    // 24. mots_cles
    private String motsCles;

    // Métadonnées
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Liste de droits / libertés associés
    private List<DroitLiberteDTO> droitsLibertes;
}