package co.com.nequi.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>,
        ReactiveQueryByExampleExecutor<UserEntity> {


    Flux<UserEntity> findByFirstNameContainingIgnoreCase(String name);
}
