package co.com.nequi.api;

import co.com.nequi.api.dto.APISuccessResponse;
import co.com.nequi.api.dto.error.APIErrorResponse;
import co.com.nequi.api.dto.error.ErrorDTO;
import co.com.nequi.api.mapper.UserMapper;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.BusinessException;
import co.com.nequi.model.user.exception.TechnicalException;
import co.com.nequi.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCase userUseCase;
    private final UserMapper mapper;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        String messageId = "msg-" + System.currentTimeMillis();
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        log.info("Iniciando creación de usuario {}", kv("userId", id));
        return userUseCase.saveUser(id)
                .map(mapper::toResponse)
                .flatMap(user -> {
                    Map<String, String> data = Map.of("firstName", user.firstName(), "email", user.email());
                    log.info("Usuario creado exitosamente",
                            kv("my-map", data),
                            kv("messageId", messageId)
                    );
                    return buildSuccessResponse(messageId, user, HttpStatus.OK);
                })
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.EXTERNAL_SERVICE_ERROR.getCode())
                                        .message(TechnicalMessage.EXTERNAL_SERVICE_ERROR.getMessage())
                                .build())
                ));
    }

    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.getUserById(id)
                .map(mapper::toResponse)
                .flatMap(result -> {
                    log.info("Usuario encontrado exitosamente",
                            kv("userId", id), // Contexto claro
                            kv("firstName", result.firstName()),
                            kv("email", result.email()),      
                            kv("messageId", messageId));
                    return buildSuccessResponse(messageId, result, HttpStatus.OK);
                })
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        messageId,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.USER_NOT_FOUND.getCode())
                                .message(TechnicalMessage.USER_NOT_FOUND.getMessage())
                                .build())
                ))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.DATABASE_ERROR.getCode())
                                .message(TechnicalMessage.DATABASE_ERROR.getMessage())
                                .build())
                ));
    }

    public Mono<ServerResponse> getUsers(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        return userUseCase.findAllUsers()
                .map(mapper::toResponse)
                .collectList()
                .flatMap(result -> {
                    log.info("Consulta general finalizada exitosamente",
                            kv("totalUsersFetched", result.size()),
                            kv("messageId", messageId));
                    return buildSuccessResponse(messageId, result, HttpStatus.OK);
                })
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.DATABASE_ERROR.getCode())
                                .message(TechnicalMessage.DATABASE_ERROR.getMessage())
                                .build())
                ));
    }

    public Mono<ServerResponse> getUsersByName(ServerRequest serverRequest) {
        String messageId = UUID.randomUUID().toString();
        String name = serverRequest.pathVariable("name");
        log.info("Iniciando búsqueda de usuarios por nombre",
                kv("firstName", name),
                kv("messageId", messageId));
        return userUseCase.findAllUsersByName(name)
                .map(mapper::toResponse)
                .collectList()
                .flatMap(result -> {
                    log.info("Búsqueda de usuarios finalizada exitosamente",
                            kv("usersFoundCount", result.size()),
                            kv("messageId", messageId));
                    return buildSuccessResponse(messageId, result, HttpStatus.OK);
                })
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.DATABASE_ERROR.getCode())
                                .message(TechnicalMessage.DATABASE_ERROR.getMessage())
                                .build())
                ));
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
