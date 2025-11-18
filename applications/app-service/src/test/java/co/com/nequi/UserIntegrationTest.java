package co.com.nequi;

import co.com.nequi.dynamodb.DynamoDBTemplateAdapter;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import co.com.nequi.model.user.gateways.UserEventRepository;
import co.com.nequi.model.user.gateways.UserRepository;
import co.com.nequi.sqs.listener.helper.SQSListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@EnableAutoConfiguration(exclude = {
        R2dbcAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class
})
class UserIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ExternalUserGateway userGateway;

    @MockitoBean
    private UserEventRepository userEventRepository;

    @MockitoBean
    private SQSListener sqsListener;

    @MockitoBean
    private DynamoDBTemplateAdapter dynamoDBAdapter;

    @Test
    void saveUser_ShouldWorkCorrectly_WhenUserIsNew() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("Integracion");
        mockUser.setEmail("test@nequi.com");

        when(userRepository.findUserById(userId)).thenReturn(Mono.empty());
        when(userGateway.fetchUserById(userId)).thenReturn(Mono.just(mockUser));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(mockUser));
        when(userEventRepository.sendUserCreatedEvent(any(User.class))).thenReturn(Mono.just(mockUser));

        webTestClient.post()
                .uri("/api/v1/users/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.messageId").isNotEmpty()
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.firstName").isEqualTo("Integracion");

        verify(userRepository).saveUser(any(User.class));
        verify(userEventRepository).sendUserCreatedEvent(any(User.class));
    }

}
