package back.app.domain.service.excel.upload;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.data.model.qpc.DroitLiberteModel;
import back.app.data.model.qpc.EJuridiction;
import back.app.data.model.qpc.ENiveauFiltrage;
import back.app.data.model.qpc.EOrdreJuridictionnel;
import back.app.data.model.qpc.ListeDeroulanteModel;
import back.app.data.repository.interfaces.DecisionFiltrageQpcRepository;
import back.app.data.repository.interfaces.DroitLiberteRepository;
import back.app.data.repository.interfaces.ListeDeroulanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DecisionFiltrageQpcImportService {

    
    private final GenericExcelImportService genericExcelImportService;
    private final DecisionFiltrageQpcRepository decisionFiltrageQpcRepository;
    private final ListeDeroulanteRepository listeDeroulanteRepository;
    private final DroitLiberteRepository droitLiberteRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void importFromXls(InputStream in) {
        ExcelImportConfig<DecisionFiltrageQpcModel> config = buildConfig();
        List<DecisionFiltrageQpcModel> entities = genericExcelImportService.importFromXls(in, config);

        // createdAt / updatedAt par défaut si tu veux
        LocalDateTime now = LocalDateTime.now();
        for (DecisionFiltrageQpcModel e : entities) {
            if (e.getCreatedAt() == null) e.setCreatedAt(now);
            if (e.getUpdatedAt() == null) e.setUpdatedAt(now);
        }

        decisionFiltrageQpcRepository.saveAll(entities);
    }

    // ----------------------------------------------------------------------
    // Construction de la config
    // ----------------------------------------------------------------------

    private ExcelImportConfig<DecisionFiltrageQpcModel> buildConfig() {

        List<ExcelImportColumn<DecisionFiltrageQpcModel>> cols = List.of(
                // On peut ignorer l'ID à l'import (ou le garder pour du upsert, mais là on laisse null)
                ExcelImportColumn.of("id",
                        s -> null,
                        (DecisionFiltrageQpcModel t, Object ignored) -> {
                        }),

                // Enums
                ExcelImportColumn.of("ordreJuridictionnel",
                        enumParser(EOrdreJuridictionnel.class),
                        DecisionFiltrageQpcModel::setOrdreJuridictionnel),

                ExcelImportColumn.of("juridiction",
                        enumParser(EJuridiction.class),
                        DecisionFiltrageQpcModel::setJuridiction),

                ExcelImportColumn.of("niveauFiltrage",
                        enumParser(ENiveauFiltrage.class),
                        DecisionFiltrageQpcModel::setNiveauFiltrage),

                // Listes déroulantes
                ExcelImportColumn.of("chambreSousSection",
                        s -> findOrCreateListe("decision_filtrage_qpc.chambre_sous_section", s),
                        DecisionFiltrageQpcModel::setChambreSousSection),

                ExcelImportColumn.of("numeroChambresReunies",
                        s -> findOrCreateListe("decision_filtrage_qpc.numero_chambres_reunies", s),
                        DecisionFiltrageQpcModel::setNumeroChambresReunies),

                ExcelImportColumn.of("niveauCompetence",
                        s -> findOrCreateListe("decision_filtrage_qpc.niveau_competence", s),
                        DecisionFiltrageQpcModel::setNiveauCompetence),

                ExcelImportColumn.of("matiere",
                        s -> findOrCreateListe("decision_filtrage_qpc.matiere", s),
                        DecisionFiltrageQpcModel::setMatiere),

                ExcelImportColumn.of("qualiteDemandeur",
                        s -> findOrCreateListe("decision_filtrage_qpc.qualite_demandeur", s),
                        DecisionFiltrageQpcModel::setQualiteDemandeur),

                ExcelImportColumn.of("qualitePreciseDemandeur",
                        s -> findOrCreateListe("decision_filtrage_qpc.qualite_precise_demandeur", s),
                        DecisionFiltrageQpcModel::setQualitePreciseDemandeur),

                ExcelImportColumn.of("decisionRenvoi",
                        s -> findOrCreateListe("decision_filtrage_qpc.decision_renvoi", s),
                        DecisionFiltrageQpcModel::setDecisionRenvoi),

                ExcelImportColumn.of("decisionNonRenvoi",
                        s -> findOrCreateListe("decision_filtrage_qpc.decision_non_renvoi", s),
                        DecisionFiltrageQpcModel::setDecisionNonRenvoi),

                ExcelImportColumn.of("applicationTheorieChangementCirconstances",
                        s -> findOrCreateListe("decision_filtrage_qpc.application_theorie_changement_circonstances", s),
                        DecisionFiltrageQpcModel::setApplicationTheorieChangementCirconstances),

                // Textes simples
                ExcelImportColumn.of("origineJuridictionnelleQpc",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setOrigineJuridictionnelleQpc),

                ExcelImportColumn.of("formationJugement",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setFormationJugement),

                ExcelImportColumn.of("reference",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setReference),

                ExcelImportColumn.of("numeroDecision",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setNumeroDecision),

                ExcelImportColumn.of("referenceDispositionsContestees",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setReferenceDispositionsContestees),

                ExcelImportColumn.of("loiOrigineDisposition",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setLoiOrigineDisposition),

                ExcelImportColumn.of("identiteDemandeur",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setIdentiteDemandeur),

                ExcelImportColumn.of("nombreDroitsNonMentionnes",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setNombreDroitsNonMentionnes),

                ExcelImportColumn.of("autresRemarques",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setAutresRemarques),

                ExcelImportColumn.of("motsCles",
                        Function.identity(),
                        DecisionFiltrageQpcModel::setMotsCles),

                // Dates
                ExcelImportColumn.of("dateFiltrage",
                        this::parseDate,
                        DecisionFiltrageQpcModel::setDateFiltrage),

                // Métadonnées éventuelles si présentes dans le fichier
                ExcelImportColumn.of("createdAt",
                        this::parseDateTime,
                        DecisionFiltrageQpcModel::setCreatedAt),

                ExcelImportColumn.of("updatedAt",
                        this::parseDateTime,
                        DecisionFiltrageQpcModel::setUpdatedAt)

                // droitsLibertes => via postProcessor (cf. plus bas)
        );

        return ExcelImportConfig.<DecisionFiltrageQpcModel>builder()
                .sheetName("Décisions filtrage QPC") // ou null si tu t'en fous
                .headerRowIndex(1)                  // ligne 2 Excel
                .dataStartRowIndex(2)               // ligne 3 Excel
                .targetSupplier(DecisionFiltrageQpcModel::new)
                .columns(cols)
                .postProcessor(this::postProcessDroitsLibertes)
                .build();
    }

    // ----------------------------------------------------------------------
    // Helpers de parsing
    // ----------------------------------------------------------------------

    private <E extends Enum<E>> Function<String, E> enumParser(Class<E> enumClass) {
        return s -> {
            if (s == null || s.isBlank()) return null;
            String trimmed = s.trim();
            try {
                return Enum.valueOf(enumClass, trimmed);
            } catch (IllegalArgumentException e) {
                // valeur inconnue => on met null comme tu le veux
                return null;
            }
        };
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        // deux cas possibles: "2024-01-01T00:00" depuis POI ou "01/01/2024" direct
        s = s.trim();
        try {
            if (s.contains("T")) {
                return LocalDate.parse(s.substring(0, 10)); // "yyyy-MM-dd..."
            }
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDateTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private ListeDeroulanteModel findOrCreateListe(String champ, String valeur) {
        if (valeur == null || valeur.isBlank()) return null;
        String v = valeur.trim();
        return listeDeroulanteRepository.findByChampAndValeur(champ, v)
                .orElseGet(() -> {
                    ListeDeroulanteModel m = ListeDeroulanteModel.builder()
                            .champ(champ)
                            .valeur(v)
                            .actif(true)
                            .build();
                    return listeDeroulanteRepository.save(m);
                });
    }

    // ----------------------------------------------------------------------
    // Post-traitement : droits / libertés
    // ----------------------------------------------------------------------

    /**
     * Exemple : tu as dans ton XLS des colonnes "droitLiberte1", "droitLiberte2", ... avec la valeur
     * "CODE - Libellé" (comme à l'export).
     *
     * Tu peux adapter le prefix / format si besoin.
     */
    private void postProcessDroitsLibertes(DecisionFiltrageQpcModel target,
                                           Map<String, String> raw) {

        Set<DroitLiberteModel> set = new HashSet<>();

        raw.forEach((header, value) -> {
            if (value == null || value.isBlank()) return;

            // Exemple de convention : headers qui commencent par "droitLiberte" // FIXME
            if (header.startsWith("droitLiberte")) {
                droitLiberteRepository.findByTexte(value)
                        .ifPresent(set::add);
            }
        });

        target.setDroitsLibertes(set);
    }
}
