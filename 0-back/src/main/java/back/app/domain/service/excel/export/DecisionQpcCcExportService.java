package back.app.domain.service.excel.export;

import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.data.repository.interfaces.DecisionQpcCcRepository;
import back.app.domain.entity.DecisionQpcCcDTO;
import back.app.domain.entity.DroitLiberteDTO;
import back.app.domain.mapper.DecisionQpcCcMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DecisionQpcCcExportService {

    private final DecisionQpcCcRepository decisionQpcCcRepository;
    private final DecisionQpcCcMapper decisionQpcCcMapper;
    private final GenericExcelExportService genericExcelExportService;

    // ----------------------------------------------------------------------
    //  XLS pour décisions QPC CC
    // ----------------------------------------------------------------------

    @Transactional(readOnly = true)
    public byte[] getXlsDecisionsQpcCc(Specification<DecisionQpcCcModel> specification) {
        List<DecisionQpcCcModel> entities = decisionQpcCcRepository.findAll(specification);
        List<DecisionQpcCcDTO> dtos = entities.stream()
                .map(decisionQpcCcMapper::toDTO)
                .collect(Collectors.toList());

        ExcelExportConfig<DecisionQpcCcDTO> config = buildDecisionQpcCcConfig();

        return genericExcelExportService.export(config, dtos);
    }

    private ExcelExportConfig<DecisionQpcCcDTO> buildDecisionQpcCcConfig() {

        List<ExcelColumn<DecisionQpcCcDTO>> columns = List.of(
                // ---------- Métadonnées ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("ID")
                        .groupName("Métadonnées")
                        .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                        .width(10)
                        .valueExtractor(DecisionQpcCcDTO::getId)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Créé le")
                        .groupName("Métadonnées")
                        .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getCreatedAt)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Modifié le")
                        .groupName("Métadonnées")
                        .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getUpdatedAt)
                        .build(),

                // ---------- Bloc "Décision" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Réf. décision Conseil")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getReferenceDecisionConseil)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Numéro")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(15)
                        .valueExtractor(DecisionQpcCcDTO::getNumero)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Date décision")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(15)
                        .valueExtractor(DecisionQpcCcDTO::getDateDecision)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Réf. décision transmission")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getReferenceDecisionTransmission)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Dispositif décision CC")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getDispositifDecisionCc)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Date abrogation différée")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(20)
                        .valueExtractor(DecisionQpcCcDTO::getDateAbrogationDifferee)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Délai avant abrogation (mois)")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(15)
                        .valueExtractor(DecisionQpcCcDTO::getDelaiAvantAbrogationMois)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Traitement effets passés")
                        .groupName("Décision")
                        .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                        .width(25)
                        .valueExtractor(DecisionQpcCcDTO::getTraitementEffetsPasses)
                        .build(),

                // ---------- Bloc "Origine / demandeur" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Origine QPC")
                        .groupName("Origine / Demandeur")
                        .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                        .width(25)
                        .valueExtractor(DecisionQpcCcDTO::getOrigineQpc)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Qualité demandeur")
                        .groupName("Origine / Demandeur")
                        .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                        .width(25)
                        .valueExtractor(DecisionQpcCcDTO::getQualiteDemandeur)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité demandeur")
                        .groupName("Origine / Demandeur")
                        .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getIdentiteDemandeur)
                        .build(),

                // ---------- Bloc "Objet de la QPC" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Dispositions législatives contestées")
                        .groupName("Objet de la QPC")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(40)
                        .valueExtractor(DecisionQpcCcDTO::getDispositionsLegislativesContestees)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Type disposition législative")
                        .groupName("Objet de la QPC")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getTypeDispositionLegislative)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Matière")
                        .groupName("Objet de la QPC")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(25)
                        .valueExtractor(DecisionQpcCcDTO::getMatiere)
                        .build(),

                // ---------- Bloc "Composition / audience" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb membres siégeant")
                        .groupName("Composition / Audience")
                        .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getNombreMembresSieges)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Demandes récusation")
                        .groupName("Composition / Audience")
                        .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getDemandeRecusation)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Déports")
                        .groupName("Composition / Audience")
                        .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                        .width(15)
                        .valueExtractor(DecisionQpcCcDTO::getDeport)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Membres déportés / récusés")
                        .groupName("Composition / Audience")
                        .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getNomMembreDeporteOuRecuse)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Oralité")
                        .groupName("Composition / Audience")
                        .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                        .width(20)
                        .valueExtractor(DecisionQpcCcDTO::getOralite)
                        .build(),

                // ---------- Bloc "Interventions / tiers" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb droits / libertés invoqués")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(22)
                        .valueExtractor(DecisionQpcCcDTO::getNombreDroitsLibertesInvoques)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb interventions admises")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(22)
                        .valueExtractor(DecisionQpcCcDTO::getNombreInterventionsAdmises)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Qualité tiers intervention")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(25)
                        .valueExtractor(DecisionQpcCcDTO::getQualiteTiersIntervention)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb personnes physiques")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getNombrePersonnesPhysiques)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité personnes physiques")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getIdentitePersonnesPhysiques)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb associations")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getNombreAssociations)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité associations")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getIdentiteAssociations)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb entreprises")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(18)
                        .valueExtractor(DecisionQpcCcDTO::getNombreEntreprises)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité entreprises")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getIdentiteEntreprises)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb syndicats / AP-OP")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(22)
                        .valueExtractor(DecisionQpcCcDTO::getNombreSyndicatsApOp)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité syndicats / AP-OP")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getIdentiteSyndicatsApOp)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Nb collectivités territoriales")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(26)
                        .valueExtractor(DecisionQpcCcDTO::getNombreCollectivitesTerritoriales)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Identité collectivités")
                        .groupName("Interventions / Tiers")
                        .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getIdentiteCollectivites)
                        .build(),

                // ---------- Bloc "Appréciation / technique" ----------
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Théorie changement circonstances")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getApplicationTheorieChangementCirconstances)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Réserve d'opportunité")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(22)
                        .valueExtractor(DecisionQpcCcDTO::getReserveOpportunite)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Réserve incompétence Conseil")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(28)
                        .valueExtractor(DecisionQpcCcDTO::getReserveIncompetenceConseil)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Prise en compte interprétation jurisprudentielle")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getPriseEnCompteInterpretationJurisprudentielle)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Techniques de contrôle")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(35)
                        .valueExtractor(DecisionQpcCcDTO::getTechniquesControle)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Motif d'inconstitutionnalité")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(40)
                        .valueExtractor(DecisionQpcCcDTO::getMotifInconstitutionnalite)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Autres remarques")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(40)
                        .valueExtractor(DecisionQpcCcDTO::getAutresRemarques)
                        .build(),
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Caractère notable de la décision")
                        .groupName("Appréciation / Technique")
                        .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                        .width(30)
                        .valueExtractor(DecisionQpcCcDTO::getCaractereNotableDecision)
                        .build(),

                // ---------- Bloc "Indexation" ---------- //FIXME put multible col ? or better way to show ?
                ExcelColumn.<DecisionQpcCcDTO>builder()
                        .header("Droits / libertés")
                        .groupName("Indexation")
                        .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                        .width(45)
                        .valueExtractor(dto ->
                                dto.getDroitsLibertes() == null ? null :
                                        dto.getDroitsLibertes().stream()
                                                .map(DroitLiberteDTO::getTexte)
                                                .collect(Collectors.joining("; "))
                        )
                        .build()
        );

        return ExcelExportConfig.<DecisionQpcCcDTO>builder()
                .sheetName("Décisions QPC CC")
                .columns(columns)
                .maxRowsForValidation(10000)
                .build();
    }
}


