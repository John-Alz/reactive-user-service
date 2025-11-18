package co.com.nequi.sqs.listener;

import co.com.nequi.model.user.User;
import co.com.nequi.sqs.listener.dto.UserEventDto;
import co.com.nequi.sqs.listener.mapper.UserEventMapper;
import co.com.nequi.usecase.usereventprocessor.UserEventProcessorUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

     private final UserEventProcessorUseCase userEventProcessorUseCase;
     private final ObjectMapper objectMapper;
     private final UserEventMapper userEventMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() ->
                objectMapper.readValue(message.body(), UserEventDto.class)
                )
                .map(userEventMapper::toModel)
                .flatMap(userEventProcessorUseCase::saveUser).then();
    }
}
