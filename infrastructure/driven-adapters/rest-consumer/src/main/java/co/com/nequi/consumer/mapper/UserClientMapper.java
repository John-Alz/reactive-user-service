package co.com.nequi.consumer.mapper;

import co.com.nequi.consumer.UserResponseDTO;
import co.com.nequi.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserClientMapper {

    User toDomain(UserResponseDTO userResponseDTO);

}
