package back.app.domain.mapper;

import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.domain.entity.DecisionQpcCcDTO;
import back.app.domain.entity.DecisionQpcCcRowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                DroitLiberteMapper.class
        }
)
public interface DecisionQpcCcMapper {
    
    @Mapping(target = "origineQpc",                source = "origineQpc.valeur")
    @Mapping(target = "qualiteDemandeur",          source = "qualiteDemandeur.valeur")
    @Mapping(target = "typeDispositionLegislative",source = "typeDispositionLegislative.valeur")
    @Mapping(target = "matiere",                   source = "matiere.valeur")
    @Mapping(target = "dispositifDecisionCc",      source = "dispositifDecisionCc.valeur")
    @Mapping(target = "traitementEffetsPasses",    source = "traitementEffetsPasses.valeur")
    @Mapping(target = "qualiteTiersIntervention",  source = "qualiteTiersIntervention.valeur")
    @Mapping(target = "reserveIncompetenceConseil",source = "reserveIncompetenceConseil.valeur")
    DecisionQpcCcDTO toDTO(DecisionQpcCcModel model);

    @Mapping(target = "qualiteDemandeur",          source = "qualiteDemandeur.valeur")
    @Mapping(target = "traitementEffetsPasses",    source = "traitementEffetsPasses.valeur")
    @Mapping(target = "matiere",                   source = "matiere.valeur")
    @Mapping(target = "dispositifDecisionCc",      source = "dispositifDecisionCc.valeur")
    @Mapping(target = "origineQpc",                source = "origineQpc.valeur")
    DecisionQpcCcRowDTO toRowDTO(DecisionQpcCcModel model);

    List<DecisionQpcCcDTO> toDTOList(List<DecisionQpcCcModel> models);


    List<DecisionQpcCcRowDTO> toRowDTOList(List<DecisionQpcCcModel> models);
}