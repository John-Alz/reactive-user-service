package co.com.nequi.api;

import co.com.nequi.api.dto.UserResponseDTO;
import co.com.nequi.api.mapper.UserMapper;
import co.com.nequi.model.user.User;
import co.com.nequi.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private Handler handler;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        userResponseDTO = new UserResponseDTO(1L, "john@gmail.com", "john", "angel", "https://avatar.jpg");
    }

    @Test
    void saveUser_Success() {
        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(userUseCase.saveUser(1L)).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponseDTO);

        Mono<ServerResponse> response = handler.saveUser(serverRequest);

        StepVerifier.create(response)
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.statusCode());
                })
                .verifyComplete();
    }

    @Test
    void saveUser_BusinessError() {
        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(userUseCase.saveUser(1L)).thenReturn(Mono.error(new RuntimeException("Error DB")));

        Mono<ServerResponse> response = handler.saveUser(serverRequest);

        StepVerifier.create(response)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void saveUser_InvalidId() {
        when(serverRequest.pathVariable("id")).thenReturn("abc");

        assertThrows(NumberFormatException.class, () -> {
            handler.saveUser(serverRequest);
        });
    }


}