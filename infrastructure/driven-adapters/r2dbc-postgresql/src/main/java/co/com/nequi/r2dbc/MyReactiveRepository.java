package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>,
        ReactiveQueryByExampleExecutor<UserEntity> {


    Flux<UserEntity> findByFirstNameContainingIgnoreCase(String name);
}
