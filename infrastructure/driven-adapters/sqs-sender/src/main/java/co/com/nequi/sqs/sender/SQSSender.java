package co.com.nequi.sqs.sender;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.TechnicalException;
import co.com.nequi.model.user.gateways.UserEventRepository;
import co.com.nequi.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements UserEventRepository /*implements SomeGateway*/ {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<User> sendUserCreatedEvent(User user) {
       return Mono.fromCallable(() -> objectMapper.writeValueAsString(user))
               .flatMap(this::send)
               .onErrorMap(ex -> {
                   log.error("Error enviando mensaje a SQS",
                           kv("userId", user.getId()),
                           kv("queueUrl", properties.queueUrl()),
                           kv("errorType", ex.getClass().getSimpleName()),
                           kv("originalMessage", ex.getMessage()),
                           ex);
                   return new TechnicalException(TechnicalMessage.EXTERNAL_SERVICE_ERROR);
               })
               .thenReturn(user);
    }
}
