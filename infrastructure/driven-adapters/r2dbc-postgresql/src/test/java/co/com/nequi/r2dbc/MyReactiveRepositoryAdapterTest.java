package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository myReactiveRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private R2dbcEntityTemplate entityTemplate;

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("john");
        user.setLastName("angel");
        user.setEmail("John@email.com");
        user.setAvatar("https://image.jpg");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("john");
        userEntity.setLastName("angel");
        userEntity.setEmail("John@email.com");
        userEntity.setAvatar("https://image.jpg");
    }

    @Test
    void saveUser_ShouldInsertAndReturnUser() {
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(entityTemplate.insert(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.saveUser(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(entityTemplate).insert(userEntity);
    }

    @Test
    void findUserById_ShouldReturnUser() {
        Long id = 1L;

        when(myReactiveRepository.findById(id)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findUserById(id);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(myReactiveRepository).findById(id);
    }

}
