package back.app.data.model.qpc;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "decision_qpc_cc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionQpcCcModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. reference_decision_conseil
    @Column(name = "reference_decision_conseil", length = 255)
    private String referenceDecisionConseil;

    // 2. numero
    @Column(name = "numero", length = 100)
    private String numero;

    // 3. date_decision
    @Column(name = "date_decision")
    private LocalDate dateDecision;

    // 4. origine_qpc : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origine_qpc_id")
    private ListeDeroulanteModel origineQpc;

    // 5. reference_decision_transmission
    @Column(name = "reference_decision_transmission", length = 255)
    private String referenceDecisionTransmission;

    // 6. qualite_demandeur : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualite_demandeur_id")
    private ListeDeroulanteModel qualiteDemandeur;

    // 7. identite_demandeur
    @Lob
    @Column(name = "identite_demandeur")
    private String identiteDemandeur;

    // 8. dispositions_legislatives_contestees
    @Lob
    @Column(name = "dispositions_legislatives_contestees")
    private String dispositionsLegislativesContestees;

    // 9. type_disposition_legislative : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_disposition_legislative_id")
    private ListeDeroulanteModel typeDispositionLegislative;

    // 10. matiere : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id")
    private ListeDeroulanteModel matiere;

    // 11. dispositif_decision_cc : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositif_decision_cc_id")
    private ListeDeroulanteModel dispositifDecisionCc;

    // 12. date_abrogation_differee
    @Column(name = "date_abrogation_differee")
    private LocalDate dateAbrogationDifferee;

    // 13. delai_avant_abrogation_mois
    @Column(name = "delai_avant_abrogation_mois")
    private Integer delaiAvantAbrogationMois;

    // 14. traitement_effets_passes : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traitement_effets_passes_id")
    private ListeDeroulanteModel traitementEffetsPasses;

    // 15. nombre_membres_sieges
    @Column(name = "nombre_membres_sieges")
    private Integer nombreMembresSieges;

    // 16. demande_recusation
    @Column(name = "demande_recusation")
    private Integer demandeRecusation;

    // 17. deport
    @Column(name = "deport")
    private Integer deport;

    // 18. nom_membre_deporte_ou_recuse
    @Lob
    @Column(name = "nom_membre_deporte_ou_recuse")
    private String nomMembreDeporteOuRecuse;

    // 19. oralite : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oralite_id")
    private ListeDeroulanteModel oralite;

    // 20. application_theorie_changement_circonstances : booléen
    @Column(name = "application_theorie_changement_circonstances")
    private Boolean applicationTheorieChangementCirconstances;

    // 21. nombre_droits_libertes_invoques
    @Column(name = "nombre_droits_libertes_invoques")
    private Integer nombreDroitsLibertesInvoques;

    // 22. nombre_interventions_admises
    @Column(name = "nombre_interventions_admises")
    private Integer nombreInterventionsAdmises;

    // 23. qualite_tiers_intervention : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualite_tiers_intervention_id")
    private ListeDeroulanteModel qualiteTiersIntervention;

    // 24. nombre_personnes_physiques
    @Column(name = "nombre_personnes_physiques")
    private Integer nombrePersonnesPhysiques;

    // 25. identite_personnes_physiques
    @Lob
    @Column(name = "identite_personnes_physiques")
    private String identitePersonnesPhysiques;

    // 26. nombre_associations
    @Column(name = "nombre_associations")
    private Integer nombreAssociations;

    // 27. identite_associations
    @Lob
    @Column(name = "identite_associations")
    private String identiteAssociations;

    // 28. nombre_entreprises
    @Column(name = "nombre_entreprises")
    private Integer nombreEntreprises;

    // 29. identite_entreprises
    @Lob
    @Column(name = "identite_entreprises")
    private String identiteEntreprises;

    // 30. nombre_syndicats_ap_op
    @Column(name = "nombre_syndicats_ap_op")
    private Integer nombreSyndicatsApOp;

    // 31. identite_syndicats_ap_op
    @Lob
    @Column(name = "identite_syndicats_ap_op")
    private String identiteSyndicatsApOp;

    // 32. nombre_collectivites_territoriales
    @Column(name = "nombre_collectivites_territoriales")
    private Integer nombreCollectivitesTerritoriales;

    // 33. identite_collectivites
    @Lob
    @Column(name = "identite_collectivites")
    private String identiteCollectivites;

    // 34. reserve_opportunite
    @Column(name = "reserve_opportunite")
    private Boolean reserveOpportunite;

    // 35. reserve_incompetence_conseil : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_incompetence_conseil_id")
    private ListeDeroulanteModel reserveIncompetenceConseil;

    // 36. prise_en_compte_interpretation_jurisprudentielle
    @Column(name = "prise_en_compte_interpretation_jurisprudentielle")
    private Boolean priseEnCompteInterpretationJurisprudentielle;

    // 37. techniques_controle
    @Lob
    @Column(name = "techniques_controle")
    private String techniquesControle;

    // 38. motif_inconstitutionnalite
    @Lob
    @Column(name = "motif_inconstitutionnalite")
    private String motifInconstitutionnalite;

    // 39. autres_remarques
    @Lob
    @Column(name = "autres_remarques")
    private String autresRemarques;

    // 40. caractere_notable_decision
    @Column(name = "caractere_notable_decision")
    private Boolean caractereNotableDecision;

    // Métadonnées
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-many avec droits_libertes via decision_qpc_cc__droits_libertes
    @ManyToMany
    @JoinTable(
            name = "decision_qpc_cc__droits_libertes",
            joinColumns = @JoinColumn(name = "id_decision_qpc_cc"),
            inverseJoinColumns = @JoinColumn(name = "id_droit_liberte")
    )
    @Builder.Default
    private Set<DroitLiberteModel> droitsLibertes = new HashSet<>();
}
