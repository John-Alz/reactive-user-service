package co.com.nequi.model.user.gateways;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserCacheRepository {

    Mono<User> findUserByID(Long id);
    Mono<User> saveUser(User user);

}
