package co.com.nequi.model.user.gateways;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserNoSQLRepository {

    Mono<User> saveUser(User user);

}
