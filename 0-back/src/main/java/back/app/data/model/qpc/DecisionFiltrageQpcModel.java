package back.app.data.model.qpc;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "decision_filtrage_qpc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionFiltrageQpcModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. ordre_juridictionnel : enum (stocké en VARCHAR)
    @Enumerated(EnumType.STRING)
    @Column(name = "ordre_juridictionnel", length = 100)
    private EOrdreJuridictionnel ordreJuridictionnel;

    // 2. juridiction : enum
    @Enumerated(EnumType.STRING)
    @Column(name = "juridiction", length = 100)
    private EJuridiction juridiction;

    // 3. chambre_sous_section : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chambre_sous_section_id")
    private ListeDeroulanteModel chambreSousSection;

    // 4. numero_chambres_reunies : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_chambres_reunies_id")
    private ListeDeroulanteModel numeroChambresReunies;

    // 5. niveau_filtrage : enum
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_filtrage", length = 100)
    private ENiveauFiltrage niveauFiltrage;

    // 6. origine_juridictionnelle_qpc : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origine_juridictionnelle_qpc_id")
    private ListeDeroulanteModel origineJuridictionnelleQpc;

    // 7. niveau_competence : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_competence_id")
    private ListeDeroulanteModel niveauCompetence;

    // 8. date_filtrage : date
    @Column(name = "date_filtrage")
    private LocalDate dateFiltrage;

    // 9. formation_jugement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_jugement_id")
    private ListeDeroulanteModel formationJugement;

    // 10. reference : texte
    @Column(name = "reference", length = 255)
    private String reference;

    // 11. numero_decision : texte
    @Column(name = "numero_decision", length = 100)
    private String numeroDecision;

    // 12. reference_dispositions_contestees : texte
    @Column(name = "reference_dispositions_contestees")
    private String referenceDispositionsContestees;

    // 13. loi_origine_disposition
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loi_origine_disposition_id")
    private ListeDeroulanteModel loiOrigineDisposition;

    // 14. matiere : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id")
    private ListeDeroulanteModel matiere;

    // 15. qualite_demandeur : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualite_demandeur_id")
    private ListeDeroulanteModel qualiteDemandeur;

    // 16. qualite_precise_demandeur : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualite_precise_demandeur_id")
    private ListeDeroulanteModel qualitePreciseDemandeur;

    // 17. identite_demandeur : texte
    @Column(name = "identite_demandeur")
    private String identiteDemandeur;

    // 18. decision_renvoi : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_renvoi_id")
    private ListeDeroulanteModel decisionRenvoi;

    // 19. decision_non_renvoi : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_non_renvoi_id")
    private ListeDeroulanteModel decisionNonRenvoi;

    // 20. application_theorie_changement_circonstances : liste déroulante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_theorie_changement_circonstances_id")
    private ListeDeroulanteModel applicationTheorieChangementCirconstances;

    // 22. nombre_droits_non_mentionnes : TEXTE dans le changelog
    @Column(name = "nombre_droits_non_mentionnes")
    private String nombreDroitsNonMentionnes;

    // 23. autres_remarques
    @Column(name = "autres_remarques")
    private String autresRemarques;

    // 24. mots_cles
    @Column(name = "mots_cles")
    private String motsCles;

    // Métadonnées
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Many-to-many avec droits_libertes via decision_filtrage_qpc__droits_libertes
    @ManyToMany
    @JoinTable(
            name = "decision_filtrage_qpc__droits_libertes",
            joinColumns = @JoinColumn(name = "id_decision_filtrage"),
            inverseJoinColumns = @JoinColumn(name = "id_droit_liberte")
    )
    @Builder.Default
    private Set<DroitLiberteModel> droitsLibertes = new HashSet<>();
}
