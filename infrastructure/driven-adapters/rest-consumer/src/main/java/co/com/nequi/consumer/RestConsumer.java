package co.com.nequi.consumer;

import co.com.nequi.consumer.mapper.UserClientMapper;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
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
                .map(item -> mapper.toDomain(item.getData()));

    }
}
