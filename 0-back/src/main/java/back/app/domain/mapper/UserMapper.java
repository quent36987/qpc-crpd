package back.app.domain.mapper;

import back.app.data.model.user.UserModel;
import back.app.domain.entity.UserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserModel model);


    @Mapping(target = "password", ignore = true)
    @Mapping(target = "hash", ignore = true)
    UserModel toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<UserModel> models);

}
