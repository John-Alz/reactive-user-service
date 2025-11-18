package co.com.nequi.sqs.listener.mapper;

import co.com.nequi.model.user.User;
import co.com.nequi.sqs.listener.dto.UserEventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEventMapper {

    User toModel(UserEventDto userEventDto);

}
