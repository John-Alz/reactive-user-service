package co.com.nequi.usecase.user;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.BusinessException;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import co.com.nequi.model.user.gateways.UserCacheRepository;
import co.com.nequi.model.user.gateways.UserEventRepository;
import co.com.nequi.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class UserUseCase {

    private final UserRepository userRepository;
    private final ExternalUserGateway userGateway;
    private final UserCacheRepository cacheRepository;
    private final UserEventRepository userEventRepository;

    public Mono<User> saveUser(Long id) {
        return userRepository.findUserById(id)
                .doOnNext(user -> log.info("Usuario encontrado en BD local, no es necesario crear. id={}", id))
                .switchIfEmpty(
                        Mono.defer(() ->
                                userGateway.fetchUserById(id)
                                        .flatMap(userRepository::saveUser)
                                        .flatMap(userEventRepository::sendUserCreatedEvent)
                        )
                );
    }

    public Mono<User> getUserById(Long id) {
        return cacheRepository.findUserByID(id)
                .doOnNext(user -> log.debug("Usuario recuperado de memoria. id={}", id))
                .switchIfEmpty(
                        Mono.defer(() -> {
                               log.info("Buscando en BD maestra. id={}", id);
                                return userRepository.findUserById(id)
                                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)))
                                        .delayElement(Duration.ofSeconds(4))
                                        .flatMap(cacheRepository::saveUser);
                        })
                );
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAllUsers()
                .doOnComplete(() -> log.info("Consulta masiva de usuarios completada en UseCase"));
    }

    public Flux<User> findAllUsersByName(String name) {
        log.info("Ejecutando regla de búsqueda por nombre: '{}'", name);
        return userRepository.findAllUsersByName(name);
    }

}
