package back.app.domain.service.excel.upload;

import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.data.model.qpc.DroitLiberteModel;
import back.app.data.model.qpc.ListeDeroulanteModel;
import back.app.data.repository.interfaces.DecisionQpcCcRepository;
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
public class DecisionQpcCcImportService {

    private final GenericExcelImportService genericExcelImportService;
    private final DecisionQpcCcRepository decisionQpcCcRepository;
    private final ListeDeroulanteRepository listeDeroulanteRepository;
    private final DroitLiberteRepository droitLiberteRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void importFromXls(InputStream in) {
        ExcelImportConfig<DecisionQpcCcModel> config = buildConfig();
        List<DecisionQpcCcModel> entities = genericExcelImportService.importFromXls(in, config);

        LocalDateTime now = LocalDateTime.now();
        for (DecisionQpcCcModel e : entities) {
            if (e.getCreatedAt() == null) e.setCreatedAt(now);
            if (e.getUpdatedAt() == null) e.setUpdatedAt(now);
        }

        decisionQpcCcRepository.saveAll(entities);
    }

    // ----------------------------------------------------------------------
    // Config d’import
    // ----------------------------------------------------------------------

    private ExcelImportConfig<DecisionQpcCcModel> buildConfig() {

        List<ExcelImportColumn<DecisionQpcCcModel>> cols = List.of(

                // ID (ignoré, on laisse l’auto-incrément)
                ExcelImportColumn.of("id",
                        s -> null,
                        (DecisionQpcCcModel t, Object ignored) -> {}),

                // ---------- Bloc "Décision" ----------
                ExcelImportColumn.of("referenceDecisionConseil",
                        Function.identity(),
                        DecisionQpcCcModel::setReferenceDecisionConseil),

                ExcelImportColumn.of("numero",
                        Function.identity(),
                        DecisionQpcCcModel::setNumero),

                ExcelImportColumn.of("dateDecision",
                        this::parseDate,
                        DecisionQpcCcModel::setDateDecision),

                ExcelImportColumn.of("referenceDecisionTransmission",
                        Function.identity(),
                        DecisionQpcCcModel::setReferenceDecisionTransmission),

                ExcelImportColumn.of("dispositifDecisionCc",
                        s -> findOrCreateListe("decision_qpc_cc.dispositif_decision_cc", s),
                        DecisionQpcCcModel::setDispositifDecisionCc),

                ExcelImportColumn.of("dateAbrogationDifferee",
                        this::parseDate,
                        DecisionQpcCcModel::setDateAbrogationDifferee),

                ExcelImportColumn.of("delaiAvantAbrogationMois",
                        this::parseInteger,
                        DecisionQpcCcModel::setDelaiAvantAbrogationMois),

                ExcelImportColumn.of("traitementEffetsPasses",
                        s -> findOrCreateListe("decision_qpc_cc.traitement_effets_passes", s),
                        DecisionQpcCcModel::setTraitementEffetsPasses),

                // ---------- Bloc "Origine / demandeur" ----------
                ExcelImportColumn.of("origineQpc",
                        s -> findOrCreateListe("decision_qpc_cc.origine_qpc", s),
                        DecisionQpcCcModel::setOrigineQpc),

                ExcelImportColumn.of("qualiteDemandeur",
                        s -> findOrCreateListe("decision_qpc_cc.qualite_demandeur", s),
                        DecisionQpcCcModel::setQualiteDemandeur),

                ExcelImportColumn.of("identiteDemandeur",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentiteDemandeur),

                // ---------- Bloc "Objet de la QPC" ----------
                ExcelImportColumn.of("dispositionsLegislativesContestees",
                        Function.identity(),
                        DecisionQpcCcModel::setDispositionsLegislativesContestees),

                ExcelImportColumn.of("typeDispositionLegislative",
                        s -> findOrCreateListe("decision_qpc_cc.type_disposition_legislative", s),
                        DecisionQpcCcModel::setTypeDispositionLegislative),

                ExcelImportColumn.of("matiere",
                        s -> findOrCreateListe("decision_qpc_cc.matiere", s),
                        DecisionQpcCcModel::setMatiere),

                // ---------- Bloc "Composition / audience" ----------
                ExcelImportColumn.of("nombreMembresSieges",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreMembresSieges),

                ExcelImportColumn.of("demandeRecusation",
                        this::parseInteger,
                        DecisionQpcCcModel::setDemandeRecusation),

                ExcelImportColumn.of("deport",
                        this::parseInteger,
                        DecisionQpcCcModel::setDeport),

                ExcelImportColumn.of("nomMembreDeporteOuRecuse",
                        Function.identity(),
                        DecisionQpcCcModel::setNomMembreDeporteOuRecuse),

                ExcelImportColumn.of("oralite",
                        s -> findOrCreateListe("decision_qpc_cc.oralite", s),
                        DecisionQpcCcModel::setOralite),

                // ---------- Bloc "Interventions / tiers" ----------
                ExcelImportColumn.of("nombreDroitsLibertesInvoques",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreDroitsLibertesInvoques),

                ExcelImportColumn.of("nombreInterventionsAdmises",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreInterventionsAdmises),

                ExcelImportColumn.of("qualiteTiersIntervention",
                        s -> findOrCreateListe("decision_qpc_cc.qualite_tiers_intervention", s),
                        DecisionQpcCcModel::setQualiteTiersIntervention),

                ExcelImportColumn.of("nombrePersonnesPhysiques",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombrePersonnesPhysiques),

                ExcelImportColumn.of("identitePersonnesPhysiques",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentitePersonnesPhysiques),

                ExcelImportColumn.of("nombreAssociations",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreAssociations),

                ExcelImportColumn.of("identiteAssociations",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentiteAssociations),

                ExcelImportColumn.of("nombreEntreprises",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreEntreprises),

                ExcelImportColumn.of("identiteEntreprises",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentiteEntreprises),

                ExcelImportColumn.of("nombreSyndicatsApOp",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreSyndicatsApOp),

                ExcelImportColumn.of("identiteSyndicatsApOp",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentiteSyndicatsApOp),

                ExcelImportColumn.of("nombreCollectivitesTerritoriales",
                        this::parseInteger,
                        DecisionQpcCcModel::setNombreCollectivitesTerritoriales),

                ExcelImportColumn.of("identiteCollectivites",
                        Function.identity(),
                        DecisionQpcCcModel::setIdentiteCollectivites),

                // ---------- Bloc "Appréciation / technique" ----------
                ExcelImportColumn.of("applicationTheorieChangementCirconstances",
                        this::parseBoolean,
                        DecisionQpcCcModel::setApplicationTheorieChangementCirconstances),

                ExcelImportColumn.of("reserveOpportunite",
                        this::parseBoolean,
                        DecisionQpcCcModel::setReserveOpportunite),

                ExcelImportColumn.of("reserveIncompetenceConseil",
                        s -> findOrCreateListe("decision_qpc_cc.reserve_incompetence_conseil", s),
                        DecisionQpcCcModel::setReserveIncompetenceConseil),

                ExcelImportColumn.of("priseEnCompteInterpretationJurisprudentielle",
                        this::parseBoolean,
                        DecisionQpcCcModel::setPriseEnCompteInterpretationJurisprudentielle),

                ExcelImportColumn.of("techniquesControle",
                        Function.identity(),
                        DecisionQpcCcModel::setTechniquesControle),

                ExcelImportColumn.of("motifInconstitutionnalite",
                        Function.identity(),
                        DecisionQpcCcModel::setMotifInconstitutionnalite),

                ExcelImportColumn.of("autresRemarques",
                        Function.identity(),
                        DecisionQpcCcModel::setAutresRemarques),

                ExcelImportColumn.of("caractereNotableDecision",
                        this::parseBoolean,
                        DecisionQpcCcModel::setCaractereNotableDecision),

                // ---------- Métadonnées éventuelles ----------
                ExcelImportColumn.of("createdAt",
                        this::parseDateTime,
                        DecisionQpcCcModel::setCreatedAt),

                ExcelImportColumn.of("updatedAt",
                        this::parseDateTime,
                        DecisionQpcCcModel::setUpdatedAt)

                // droitsLibertes -> postProcessor
        );

        return ExcelImportConfig.<DecisionQpcCcModel>builder()
                .sheetName("Décisions QPC CC")   // même nom que l’export
                .headerRowIndex(1)               // ligne 2 Excel
                .dataStartRowIndex(2)            // ligne 3 Excel
                .targetSupplier(DecisionQpcCcModel::new)
                .columns(cols)
                .postProcessor(this::postProcessDroitsLibertes)
                .build();
    }

    // ----------------------------------------------------------------------
    // Parsing helpers
    // ----------------------------------------------------------------------

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        s = s.trim();
        try {
            if (s.contains("T")) {
                // format "yyyy-MM-ddTHH:mm:ss"
                return LocalDate.parse(s.substring(0, 10));
            }
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String s) {
        if (s == null || s.isBlank()) return null;
        String v = s.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "1", "true", "vrai", "oui", "o", "x" -> Boolean.TRUE;
            case "0", "false", "faux", "non", "n" -> Boolean.FALSE;
            default -> null;
        };
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDateTime.parse(s.trim());
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
    // Post-traitement droits / libertés
    // ----------------------------------------------------------------------

    /**
     * Même principe que pour le filtrage :
     * colonnes "droitLiberte1", "droitLiberte2", etc., avec "CODE - Libellé" ou juste "CODE".
     */
    private void postProcessDroitsLibertes(DecisionQpcCcModel target,
                                           Map<String, String> rawValues) {

        Set<DroitLiberteModel> set = new HashSet<>();

        rawValues.forEach((header, value) -> {
            if (value == null || value.isBlank()) return;


                droitLiberteRepository.findByTexte(value.trim())
                        .ifPresent(set::add);

        });

        target.setDroitsLibertes(set);
    }
}

