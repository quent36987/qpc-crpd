package back.app.domain.mapper;

import back.app.data.model.qpc.ListeDeroulanteModel;
import back.app.domain.entity.ListeDeroulanteDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ListeDeroulanteMapper {


    ListeDeroulanteDTO toDTO(ListeDeroulanteModel model);

    ListeDeroulanteModel toModel(ListeDeroulanteDTO dto);

    List<ListeDeroulanteDTO> toDTO(List<ListeDeroulanteModel> liste);
}
