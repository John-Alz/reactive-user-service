package co.com.nequi.usecase.user;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.ExternalUserGateway;
import co.com.nequi.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
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

}
