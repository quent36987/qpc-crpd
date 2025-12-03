package back.app.domain.mapper;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.domain.entity.DecisionFiltrageQpcDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "applicationTheorieChangementCirconstances",
            source = "applicationTheorieChangementCirconstances.valeur")
    DecisionFiltrageQpcDTO toDTO(DecisionFiltrageQpcModel model);
}