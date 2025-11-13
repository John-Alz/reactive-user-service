package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserRepository;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long,
        MyReactiveRepository> implements UserRepository {

    private final R2dbcEntityTemplate entityTemplate;

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
        return  entityTemplate.insert(entity)
                .map(this::toEntity);
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return this.findById(id);
    }
}
