package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.enums.TechnicalMessage;
import co.com.nequi.model.user.exception.TechnicalException;
import co.com.nequi.model.user.gateways.UserRepository;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Repository
@Slf4j
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long,
        MyReactiveRepository> implements UserRepository {

    private final R2dbcEntityTemplate entityTemplate;
    private static final String DB_ERROR = "dbError";

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, R2dbcEntityTemplate entityTemplate) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<User> saveUser(User user) {
        UserEntity entity = this.toData(user);
        return entityTemplate.insert(entity)
                .map(this::toEntity)
                .onErrorMap(ex -> {
                    log.error("Error guardando usuario en la BD",
                            kv("userId", user.getId()),
                            kv(DB_ERROR, ex.getMessage()),
                            ex);
                    return new TechnicalException(TechnicalMessage.DATABASE_ERROR);
                });
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return this.findById(id)
                .onErrorMap(ex -> {
                    log.error("Error buscando usuario en la BD",
                            kv(DB_ERROR, ex.getMessage()),
                            ex);
                    return new TechnicalException(TechnicalMessage.DATABASE_ERROR);
                });
    }

    @Override
    public Flux<User> findAllUsers() {
        return this.findAll()
                .onErrorMap(ex -> {
                    log.error("Error buscando usuarios en la BD",
                            kv(DB_ERROR, ex.getMessage()),
                            ex);
                    return new TechnicalException(TechnicalMessage.DATABASE_ERROR);
                });
    }

    @Override
    public Flux<User> findAllUsersByName(String name) {
        return this.repository.findByFirstNameContainingIgnoreCase(name)
                .map(this::toEntity)
                .onErrorMap(ex -> {
                    log.error("Error buscando usuarios por nombre en la BD",
                            kv(DB_ERROR, ex.getMessage()),
                            ex);
                    return new TechnicalException(TechnicalMessage.DATABASE_ERROR);
                });
    }
}
