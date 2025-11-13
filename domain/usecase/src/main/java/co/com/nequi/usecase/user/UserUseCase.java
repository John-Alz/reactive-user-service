package co.com.nequi.usecase.user;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.BusinessException;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import co.com.nequi.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final ExternalUserGateway userGateway;

    public Mono<User> saveUser(Long id) {
        return userRepository.findUserById(id)
                .switchIfEmpty(
                        Mono.defer(() ->
                                userGateway.fetchUserById(id)
                                        .flatMap(userRepository::saveUser)
                        )
                );
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findUserById(id)
                .switchIfEmpty(
                        Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND))
                );
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public Flux<User> findAllUsersByName(String name) {
        return userRepository.findAllUsersByName(name);
    }

}
