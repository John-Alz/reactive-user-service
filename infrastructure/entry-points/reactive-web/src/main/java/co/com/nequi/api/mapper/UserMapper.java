package co.com.nequi.api.mapper;

import co.com.nequi.api.dto.UserResponseDTO;
import co.com.nequi.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponse(User user);

}
