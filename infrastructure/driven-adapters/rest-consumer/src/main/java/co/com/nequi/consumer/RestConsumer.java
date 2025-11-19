package co.com.nequi.consumer;

import co.com.nequi.consumer.mapper.UserClientMapper;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.TechnicalException;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestConsumer implements ExternalUserGateway /* implements Gateway from domain */{

    private final WebClient client;
    private final UserClientMapper mapper;


    @Override
    public Mono<User> fetchUserById(Long id) {
        return client
                .get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(ReqResApiResponse.class)
                .map(item -> mapper.toDomain(item.getData()))
                .doOnError(ex ->
                    log.error("Error consumiendo servicio externo Users",
                            kv("userId", id),
                            kv("errorType", ex.getClass().getSimpleName()),
                            kv("originalMessage", ex.getMessage()),
                            ex)
                )
                .onErrorMap(ex ->
                    new TechnicalException(TechnicalMessage.EXTERNAL_SERVICE_ERROR)
                );
    }
}
