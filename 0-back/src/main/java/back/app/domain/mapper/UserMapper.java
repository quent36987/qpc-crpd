package back.app.domain.mapper;

import back.app.data.model.*;
import back.app.data.model.*;
import back.app.domain.entity.UserDTO;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserModel model);


    @Mapping(target = "password", ignore = true)
    @Mapping(target = "hash", ignore = true)
    UserModel toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<UserModel> models);

}
