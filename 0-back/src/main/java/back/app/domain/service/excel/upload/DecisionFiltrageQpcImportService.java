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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
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
                ExcelImportColumn.of("Ordre juridictionnel",
                        EOrdreJuridictionnel::fromExcel,
                        DecisionFiltrageQpcModel::setOrdreJuridictionnel),

                ExcelImportColumn.of("Juridiction",
                        EJuridiction::fromExcel,
                        DecisionFiltrageQpcModel::setJuridiction),

                ExcelImportColumn.of("Niveau de filtrage",
                        ENiveauFiltrage::fromExcel,
                        DecisionFiltrageQpcModel::setNiveauFiltrage),

                // Listes déroulantes
                ExcelImportColumn.of("Chambre ou Sous-section",
                        s -> findOrCreateListe("decision_filtrage_qpc.chambre_sous_section", s),
                        DecisionFiltrageQpcModel::setChambreSousSection),

                ExcelImportColumn.of("n° Si Chambre ou sous-Section réunies (CE)",
                        s -> findOrCreateListe("decision_filtrage_qpc.numero_chambres_reunies", s),
                        DecisionFiltrageQpcModel::setNumeroChambresReunies),

                ExcelImportColumn.of("Niveau de compétence",
                        s -> findOrCreateListe("decision_filtrage_qpc.niveau_competence", s),
                        DecisionFiltrageQpcModel::setNiveauCompetence),

                ExcelImportColumn.of("Matières",
                        s -> findOrCreateListe("decision_filtrage_qpc.matiere", s),
                        DecisionFiltrageQpcModel::setMatiere),

                ExcelImportColumn.of("Qualité du demandeur",
                        s -> findOrCreateListe("decision_filtrage_qpc.qualite_demandeur", s),
                        DecisionFiltrageQpcModel::setQualiteDemandeur),

                ExcelImportColumn.of("Qualité précise du demandeur",
                        s -> findOrCreateListe("decision_filtrage_qpc.qualite_precise_demandeur", s),
                        DecisionFiltrageQpcModel::setQualitePreciseDemandeur),

                ExcelImportColumn.of("Décisions de renvoi (filtrage)",
                        s -> findOrCreateListe("decision_filtrage_qpc.decision_renvoi", s),
                        DecisionFiltrageQpcModel::setDecisionRenvoi),

                ExcelImportColumn.of("Décisions de non renvoi (filtrage)",
                        s -> findOrCreateListe("decision_filtrage_qpc.decision_non_renvoi", s),
                        DecisionFiltrageQpcModel::setDecisionNonRenvoi),

                ExcelImportColumn.of("Application de la théorie du changement des circonstances",
                        s -> findOrCreateListe("decision_filtrage_qpc.application_theorie_changement_circonstances", s),
                        DecisionFiltrageQpcModel::setApplicationTheorieChangementCirconstances),

                ExcelImportColumn.of("Formation de jugement",
                        s -> findOrCreateListe("decision_filtrage_qpc.formation_jugement", s),
                        DecisionFiltrageQpcModel::setFormationJugement),

                ExcelImportColumn.of("Loi(s) à l'origine de la disposition en cause (date)",
                        s -> findOrCreateListe("decision_filtrage_qpc.loi_origine_disposition", s),
                        DecisionFiltrageQpcModel::setLoiOrigineDisposition),

                ExcelImportColumn.of("Origine juridictionnelle de la QPC (si suite à transmission)",
                        s -> findOrCreateListe("decision_filtrage_qpc.origine_juridictionnelle_qpc", s),
                        DecisionFiltrageQpcModel::setOrigineJuridictionnelleQpc),

                // Textes simples
                ExcelImportColumn.of("Références",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setReference),

                ExcelImportColumn.of("n° de la décision",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setNumeroDecision),

                ExcelImportColumn.of("Références de la ou des dispositions législatives contestée(s) dans la QPC",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setReferenceDispositionsContestees),

                ExcelImportColumn.of("Identité du demandeur",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setIdentiteDemandeur),

                ExcelImportColumn.of("Autres remarques",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setAutresRemarques),

                ExcelImportColumn.of("Mots-clés",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setMotsCles),

                ExcelImportColumn.of("Nombre de droits et libertés invoqués",
                        DecisionFiltrageQpcImportService::str,
                        DecisionFiltrageQpcModel::setNombreDroitsNonMentionnes),

                // Dates
                ExcelImportColumn.of("Date",
                        this::parseDate,
                        DecisionFiltrageQpcModel::setDateFiltrage)

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

    private static String str(String raw) {
        return raw == null ? null : raw.trim();
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
     * du texte du droit/liberté.
     * On va chercher les entités correspondantes et les lier à la décision.
     *
     */
    private void postProcessDroitsLibertes(DecisionFiltrageQpcModel target,
                                           Map<String, String> raw) {

        Set<DroitLiberteModel> set = new HashSet<>();

        raw.forEach((header, value) -> {
            if (value == null || value.isBlank()) return;

            // Colonnes du type "droitLiberte1", "droitLiberte2", ...
            if (header.startsWith("Droits et libertés invoqués")) {
                String texte = String.valueOf(value).trim();

                Optional<DroitLiberteModel> droitOpt = droitLiberteRepository.findByTexte(texte);
                if (droitOpt.isPresent()) {
                    set.add(droitOpt.get());
                } else {
                    // add new DroitLiberteModel if not found
                    DroitLiberteModel newDroit = DroitLiberteModel.builder()
                            .texte(texte)
                            .build();
                    droitLiberteRepository.save(newDroit);
                    set.add(newDroit);
                }

            }
        });

        target.setDroitsLibertes(set);
    }

}
