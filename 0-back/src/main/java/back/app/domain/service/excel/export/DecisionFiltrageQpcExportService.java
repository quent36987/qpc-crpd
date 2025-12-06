package back.app.domain.service.excel.export;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.data.model.qpc.EJuridiction;
import back.app.data.model.qpc.ENiveauFiltrage;
import back.app.data.model.qpc.EOrdreJuridictionnel;
import back.app.data.repository.interfaces.DecisionFiltrageQpcRepository;
import back.app.domain.entity.DecisionFiltrageQpcDTO;
import back.app.domain.entity.DroitLiberteDTO;
import back.app.domain.mapper.DecisionFiltrageQpcMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionFiltrageQpcExportService {

        private final DecisionFiltrageQpcRepository decisionFiltrageQpcRepository;
        private final DecisionFiltrageQpcMapper decisionFiltrageQpcMapper;
        private final GenericExcelExportService genericExcelExportService;

        // ----------------------------------------------------------------------
        //  XLS pour décisions filtrage QPC
        // ----------------------------------------------------------------------

        @Transactional(readOnly = true)
        public byte[] getXlsDecisionsFiltrage(Specification<DecisionFiltrageQpcModel> specification) {
            List<DecisionFiltrageQpcModel> entities = decisionFiltrageQpcRepository.findAll(specification);
            List<DecisionFiltrageQpcDTO> dtos = entities.stream()
                    .map(decisionFiltrageQpcMapper::toDTO)
                    .collect(Collectors.toList());

            ExcelExportConfig<DecisionFiltrageQpcDTO> config = buildDecisionsFiltrageConfig();

            return genericExcelExportService.export(config, dtos);
        }

        private ExcelExportConfig<DecisionFiltrageQpcDTO> buildDecisionsFiltrageConfig() {
            // Valeurs pour les enums (listes déroulantes)
            String[] ordreValues = Arrays.stream(EOrdreJuridictionnel.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] juridictionValues = Arrays.stream(EJuridiction.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] niveauFiltrageValues = Arrays.stream(ENiveauFiltrage.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            List<ExcelColumn<DecisionFiltrageQpcDTO>> columns = List.of(
                    // ---------- Métadonnées ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("ID")
                            .groupName("Métadonnées")
                            .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                            .width(10)
                            .valueExtractor(DecisionFiltrageQpcDTO::getId)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Créé le")
                            .groupName("Métadonnées")
                            .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                            .width(18)
                            .valueExtractor(DecisionFiltrageQpcDTO::getCreatedAt)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Modifié le")
                            .groupName("Métadonnées")
                            .groupColorIndex(IndexedColors.GREY_25_PERCENT.getIndex())
                            .width(18)
                            .valueExtractor(DecisionFiltrageQpcDTO::getUpdatedAt)
                            .build(),

                    // ---------- Bloc "Juridiction" ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Ordre juridictionnel")
                            .groupName("Juridiction")
                            .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                            .width(25)
                            .allowedValues(ordreValues)
                            .valueExtractor(dto -> dto.getOrdreJuridictionnel())
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Juridiction")
                            .groupName("Juridiction")
                            .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                            .width(25)
                            .allowedValues(juridictionValues)
                            .valueExtractor(DecisionFiltrageQpcDTO::getJuridiction)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Chambre / sous-section")
                            .groupName("Juridiction")
                            .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                            .width(30)
                            .valueExtractor(DecisionFiltrageQpcDTO::getChambreSousSection)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("N° chambres réunies")
                            .groupName("Juridiction")
                            .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                            .width(20)
                            .valueExtractor(DecisionFiltrageQpcDTO::getNumeroChambresReunies)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Niveau de filtrage")
                            .groupName("Juridiction")
                            .groupColorIndex(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex())
                            .width(20)
                            .allowedValues(niveauFiltrageValues)
                            .valueExtractor(DecisionFiltrageQpcDTO::getNiveauFiltrage)
                            .build(),

                    // ---------- Bloc "QPC" ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Origine juridictionnelle QPC")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(30)
                            .valueExtractor(DecisionFiltrageQpcDTO::getOrigineJuridictionnelleQpc)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Niveau de compétence")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(25)
                            .valueExtractor(DecisionFiltrageQpcDTO::getNiveauCompetence)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Date filtrage")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(15)
                            .valueExtractor(DecisionFiltrageQpcDTO::getDateFiltrage)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Formation de jugement")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(30)
                            .valueExtractor(DecisionFiltrageQpcDTO::getFormationJugement)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Référence")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(25)
                            .valueExtractor(DecisionFiltrageQpcDTO::getReference)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("N° décision")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(20)
                            .valueExtractor(DecisionFiltrageQpcDTO::getNumeroDecision)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Dispositions contestées")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(40)
                            .valueExtractor(DecisionFiltrageQpcDTO::getReferenceDispositionsContestees)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Loi d'origine")
                            .groupName("QPC")
                            .groupColorIndex(IndexedColors.LIGHT_GREEN.getIndex())
                            .width(40)
                            .valueExtractor(DecisionFiltrageQpcDTO::getLoiOrigineDisposition)
                            .build(),

                    // ---------- Bloc "Parties" ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Qualité demandeur")
                            .groupName("Parties")
                            .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                            .width(25)
                            .valueExtractor(DecisionFiltrageQpcDTO::getQualiteDemandeur)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Qualité précise demandeur")
                            .groupName("Parties")
                            .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                            .width(30)
                            .valueExtractor(DecisionFiltrageQpcDTO::getQualitePreciseDemandeur)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Identité demandeur")
                            .groupName("Parties")
                            .groupColorIndex(IndexedColors.LIGHT_ORANGE.getIndex())
                            .width(30)
                            .valueExtractor(DecisionFiltrageQpcDTO::getIdentiteDemandeur)
                            .build(),

                    // ---------- Bloc "Décision" ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Décision renvoi")
                            .groupName("Décision")
                            .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                            .width(25)
                            .valueExtractor(DecisionFiltrageQpcDTO::getDecisionRenvoi)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Décision non renvoi")
                            .groupName("Décision")
                            .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                            .width(25)
                            .valueExtractor(DecisionFiltrageQpcDTO::getDecisionNonRenvoi)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Théorie changement circonstances")
                            .groupName("Décision")
                            .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                            .width(35)
                            .valueExtractor(DecisionFiltrageQpcDTO::getApplicationTheorieChangementCirconstances)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Nb droits non mentionnés")
                            .groupName("Décision")
                            .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                            .width(20)
                            .valueExtractor(DecisionFiltrageQpcDTO::getNombreDroitsNonMentionnes)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Autres remarques")
                            .groupName("Décision")
                            .groupColorIndex(IndexedColors.LIGHT_YELLOW.getIndex())
                            .width(40)
                            .valueExtractor(DecisionFiltrageQpcDTO::getAutresRemarques)
                            .build(),

                    // ---------- Bloc "Indexation" ----------
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Mots-clés")
                            .groupName("Indexation")
                            .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                            .width(35)
                            .valueExtractor(DecisionFiltrageQpcDTO::getMotsCles)
                            .build(),
                    ExcelColumn.<DecisionFiltrageQpcDTO>builder()
                            .header("Droits / libertés")
                            .groupName("Indexation")
                            .groupColorIndex(IndexedColors.LIGHT_TURQUOISE.getIndex())
                            .width(40)
                            .valueExtractor(dto ->
                                    dto.getDroitsLibertes() == null ? null :
                                            dto.getDroitsLibertes().stream()
                                                    .map(DroitLiberteDTO::getTexte)
                                                    .collect(Collectors.joining("; "))
                            )
                            .build()
            );

            return ExcelExportConfig.<DecisionFiltrageQpcDTO>builder()
                    .sheetName("Décisions filtrage QPC")
                    .columns(columns)
                    .maxRowsForValidation(10000)
                    .build();
        }


}
