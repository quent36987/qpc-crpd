package back.app.domain.mapper;

import back.app.data.model.qpc.DroitLiberteModel;
import back.app.domain.entity.DroitLiberteDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DroitLiberteMapper {

    DroitLiberteDTO toDTO(DroitLiberteModel model);

    DroitLiberteModel toModel(DroitLiberteDTO dto);

    List<DroitLiberteDTO> toDTOList(List<DroitLiberteModel> models);
}