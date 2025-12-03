package back.app.domain.mapper;

import back.app.data.model.setting.SettingsModel;
import back.app.domain.entity.SettingsDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    // --- Entity → DTO ---
    @Mapping(target = "updatedById",      source = "updatedBy.id")
    @Mapping(target = "updatedByNom",     source = "updatedBy.nom")
    @Mapping(target = "updatedByPrenom",  source = "updatedBy.prenom")
    SettingsDTO toDTO(SettingsModel model);

    List<SettingsDTO> toDTOList(List<SettingsModel> models);

    // --- DTO → Entity ---
    SettingsModel toEntity(SettingsDTO dto);

    List<SettingsModel> toEntityList(List<SettingsDTO> dtos);
}
