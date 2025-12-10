package back.app.domain.mapper;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.domain.entity.DecisionFiltrageQpcDTO;
import back.app.domain.entity.DecisionFiltrageQpcRowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                DroitLiberteMapper.class
        }
)
public interface DecisionFiltrageQpcMapper {

    @Mapping(target = "chambreSousSection",                  source = "chambreSousSection.valeur")
    @Mapping(target = "numeroChambresReunies",              source = "numeroChambresReunies.valeur")
    @Mapping(target = "niveauCompetence",                   source = "niveauCompetence.valeur")
    @Mapping(target = "matiere",                            source = "matiere.valeur")
    @Mapping(target = "qualiteDemandeur",                   source = "qualiteDemandeur.valeur")
    @Mapping(target = "qualitePreciseDemandeur",           source = "qualitePreciseDemandeur.valeur")
    @Mapping(target = "decisionRenvoi",                     source = "decisionRenvoi.valeur")
    @Mapping(target = "decisionNonRenvoi",                  source = "decisionNonRenvoi.valeur")
    @Mapping(target = "formationJugement",                  source = "formationJugement.valeur")
    @Mapping(target = "loiOrigineDisposition",                  source = "loiOrigineDisposition.valeur")
    @Mapping(target = "origineJuridictionnelleQpc",                  source = "origineJuridictionnelleQpc.valeur")
    @Mapping(target = "applicationTheorieChangementCirconstances",
            source = "applicationTheorieChangementCirconstances.valeur")
    DecisionFiltrageQpcDTO toDTO(DecisionFiltrageQpcModel model);

    @Mapping(target = "decisionRenvoi",                     source = "decisionRenvoi.valeur")
    @Mapping(target = "decisionNonRenvoi",                  source = "decisionNonRenvoi.valeur")
    @Mapping(target = "niveauCompetence",                   source = "niveauCompetence.valeur")
    @Mapping(target = "formationJugement",                  source = "formationJugement.valeur")
    DecisionFiltrageQpcRowDTO toRowDTO(DecisionFiltrageQpcModel model);

    List<DecisionFiltrageQpcRowDTO> toRowDTOs(List<DecisionFiltrageQpcModel> models);

    List<DecisionFiltrageQpcDTO> toDTOs(List<DecisionFiltrageQpcModel> models);
}