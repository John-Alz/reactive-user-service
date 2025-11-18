package co.com.nequi.usecase.usereventprocessor;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserNoSQLRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserEventProcessorUseCase {

    private final UserNoSQLRepository userNoSQLRepository;

    public Mono<User> saveUser(User user) {
        user.setFirstName(user.getFirstName().toUpperCase());
        user.setLastName(user.getLastName().toUpperCase());
        user.setEmail(user.getEmail().toUpperCase());
        user.setAvatar(user.getAvatar().toUpperCase());
        return userNoSQLRepository.saveUser(user);
    }

}
