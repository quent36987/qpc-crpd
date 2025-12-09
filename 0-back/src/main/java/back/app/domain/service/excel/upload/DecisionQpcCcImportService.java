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
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DecisionQpcCcImportService {


    private final GenericExcelImportService genericExcelImportService;
    private final DecisionQpcCcRepository decisionQpcCcRepository;
    private final ListeDeroulanteRepository listeDeroulanteRepository;
    private final DroitLiberteRepository droitLiberteRepository;

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
                ExcelImportColumn.of("Référence décision du Conseil",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setReferenceDecisionConseil),

                ExcelImportColumn.of("n°",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setNumero),

                ExcelImportColumn.of("Date",
                        GenericExcelImportService::parseDate,
                        DecisionQpcCcModel::setDateDecision),

                ExcelImportColumn.of("Référence de la décision de transmission (Cass ou CE)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setReferenceDecisionTransmission),

                ExcelImportColumn.of("Dispositif de la décision du CC ",
                        s -> findOrCreateListe("decision_qpc_cc.dispositif_decision_cc", s),
                        DecisionQpcCcModel::setDispositifDecisionCc),

                ExcelImportColumn.of("Date d'abrogation différée",
                        GenericExcelImportService::parseDate,
                        DecisionQpcCcModel::setDateAbrogationDifferee),

                ExcelImportColumn.of("Délai avant abrogation (en mois)",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setDelaiAvantAbrogationMois),

                ExcelImportColumn.of("Traitement des effets passés",
                        s -> findOrCreateListe("decision_qpc_cc.traitement_effets_passes", s),
                        DecisionQpcCcModel::setTraitementEffetsPasses),

                // ---------- Bloc "Origine / demandeur" ----------
                ExcelImportColumn.of("Origine de la QPC",
                        s -> findOrCreateListe("decision_qpc_cc.origine_qpc", s),
                        DecisionQpcCcModel::setOrigineQpc),

                ExcelImportColumn.of("Qualité du demandeur",
                        s -> findOrCreateListe("decision_qpc_cc.qualite_demandeur", s),
                        DecisionQpcCcModel::setQualiteDemandeur),

                ExcelImportColumn.of("Identité du demandeur",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentiteDemandeur),

                // ---------- Bloc "Objet de la QPC" ----------
                ExcelImportColumn.of("Dispositions législatives contestées",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setDispositionsLegislativesContestees),

                ExcelImportColumn.of("Type de disposition législative contestées",
                        s -> findOrCreateListe("decision_qpc_cc.type_disposition_legislative", s),
                        DecisionQpcCcModel::setTypeDispositionLegislative),

                ExcelImportColumn.of("Matière",
                        s -> findOrCreateListe("decision_qpc_cc.matiere", s),
                        DecisionQpcCcModel::setMatiere),

                // ---------- Bloc "Composition / audience" ----------
                ExcelImportColumn.of("Nombre de membres ayant siégé",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreMembresSieges),

                ExcelImportColumn.of("demande de récusation",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setDemandeRecusation),

                ExcelImportColumn.of("Déports",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setDeport),

                ExcelImportColumn.of("Nom du membre déporté ou récusé",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setNomMembreDeporteOuRecuse),

                ExcelImportColumn.of("Oralité",
                        GenericExcelImportService::parseBoolean,
                        DecisionQpcCcModel::setOralite),

                // ---------- Bloc "Interventions / tiers" ----------
                ExcelImportColumn.of("Nombre de droits et libertés invoqués",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreDroitsLibertesInvoques),

                ExcelImportColumn.of("Nombre d'intervention(s) admise(s)",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreInterventionsAdmises),

                ExcelImportColumn.of("Qualité du/des tiers dont l'intervention est admise",
                        s -> findOrCreateListe("decision_qpc_cc.qualite_tiers_intervention", s),
                        DecisionQpcCcModel::setQualiteTiersIntervention),

                ExcelImportColumn.of("Personnes physiques",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombrePersonnesPhysiques),

                ExcelImportColumn.of("identité des PP        (liste)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentitePersonnesPhysiques),

                ExcelImportColumn.of("Associations (sauf associations professionnelles)",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreAssociations),

                ExcelImportColumn.of("Identité des Ass.         (liste)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentiteAssociations),

                ExcelImportColumn.of("Entreprise (publiques ou privées dont coopératives et GIE)",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreEntreprises),

                ExcelImportColumn.of("Identité des ent.      (liste)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentiteEntreprises),

                ExcelImportColumn.of("Syndicats, Associations professionnelles et ordres professionnels",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreSyndicatsApOp),

                ExcelImportColumn.of("Identité  des Synd et AP et OP (liste)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentiteSyndicatsApOp),

                ExcelImportColumn.of("Collectivités territoriales et groupements de collectivités territoriales",
                        GenericExcelImportService::parseInteger,
                        DecisionQpcCcModel::setNombreCollectivitesTerritoriales),

                ExcelImportColumn.of("Identité des CT et grpt de CT  (liste)",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setIdentiteCollectivites),

                // ---------- Bloc "Appréciation / technique" ----------
                ExcelImportColumn.of("Application de la théorie du changement des circonstances",
                        GenericExcelImportService::parseBoolean,
                        DecisionQpcCcModel::setApplicationTheorieChangementCirconstances),

                ExcelImportColumn.of("Réserve d'opportunité (non empiètement sur le rôle du législateur) ",
                        GenericExcelImportService::parseBoolean,
                        DecisionQpcCcModel::setReserveOpportunite),

                ExcelImportColumn.of("Réserve d'incompétence du Conseil",
                        s -> findOrCreateListe("decision_qpc_cc.reserve_incompetence_conseil", s),
                        DecisionQpcCcModel::setReserveIncompetenceConseil),

                ExcelImportColumn.of("Prise en compte de l'interprétation jurisprudentielle faite de la loi dans l'appréciation de sa constitutionnalité",
                        GenericExcelImportService::parseBoolean,
                        DecisionQpcCcModel::setPriseEnCompteInterpretationJurisprudentielle),

                ExcelImportColumn.of("Technique(s) de contrôle",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setTechniquesControle),

                ExcelImportColumn.of("En cas de censure : Motif de l'inconstitutionnalité",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setMotifInconstitutionnalite),

                ExcelImportColumn.of("Autres remarques",
                        GenericExcelImportService::str,
                        DecisionQpcCcModel::setAutresRemarques),

                ExcelImportColumn.of("Caractère notable de la décision",
                        GenericExcelImportService::parseBoolean,
                        DecisionQpcCcModel::setCaractereNotableDecision)
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
     * Exemple : tu as dans ton XLS des colonnes "droitLiberte1", "droitLiberte2", ... avec la valeur
     * du texte du droit/liberté.
     * On va chercher les entités correspondantes et les lier à la décision.
     *
     */
    private void postProcessDroitsLibertes(DecisionQpcCcModel target,
                                           Map<String, String> raw) {

        Set<DroitLiberteModel> set = new HashSet<>();

        raw.forEach((header, value) -> {
            if (value == null || value.isBlank()) return;

            // Colonnes du type "droitLiberte1", "droitLiberte2", ...
            if (header.startsWith("Droit et liberté invoqué")) {
                String texte = value.trim();

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

