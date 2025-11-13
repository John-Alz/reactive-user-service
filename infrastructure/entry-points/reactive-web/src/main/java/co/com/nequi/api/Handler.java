package co.com.nequi.api;

import co.com.nequi.api.dto.APISuccessResponse;
import co.com.nequi.api.dto.error.APIErrorResponse;
import co.com.nequi.api.dto.error.ErrorDTO;
import co.com.nequi.api.mapper.UserMapper;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.BusinessException;
import co.com.nequi.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserMapper mapper;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.saveUser(id)
                .map(mapper::toResponse)
                .flatMap(user -> buildSuccessResponse(messageId, user, HttpStatus.OK));

    }

    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.getUserById(id)
                .map(mapper::toResponse)
                .flatMap(result -> buildSuccessResponse(messageId, result, HttpStatus.OK))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        messageId,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.USER_NOT_FOUND.getCode())
                                .message(TechnicalMessage.USER_NOT_FOUND.getMessage())
                                .build())
                ));
    }

    public Mono<ServerResponse> getUsers(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        return userUseCase.findAllUsers()
                .map(mapper::toResponse)
                .collectList()
                .flatMap(result -> buildSuccessResponse(messageId, result, HttpStatus.OK));
    }

    public Mono<ServerResponse> getUsersByName(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        String name = serverRequest.pathVariable("name");
        return userUseCase.findAllUsersByName(name)
                .map(mapper::toResponse)
                .collectList()
                .flatMap(result -> buildSuccessResponse(messageId, result, HttpStatus.OK));
    }


    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String messageId, List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIErrorResponse apiErrorResponse = APIErrorResponse
                    .builder()
                    .messageId(messageId)
                    .status(status.value())
                    .title(status.getReasonPhrase())
                    .errors(errors)
                    .build();
            return ServerResponse.status(status)
                    .bodyValue(apiErrorResponse);
        });
    }

    private <T> Mono<ServerResponse> buildSuccessResponse(String messageId, T data,
                                                          HttpStatus status) {
        var apiSuccessResponse = APISuccessResponse.<T>builder()
                .messageId(messageId)
                .data(data)
                .build();
        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(apiSuccessResponse);
    }



}
