package co.com.nequi.api;

import co.com.nequi.api.mapper.UserMapper;
import co.com.nequi.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserMapper mapper;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.saveUser(id)
                .map(mapper::toResponse)
                .flatMap(user -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(user));

    }

}
