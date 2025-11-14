package co.com.nequi.redis.template;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserCacheRepository;
import co.com.nequi.redis.template.helper.ReactiveTemplateAdapterOperations;
import co.com.nequi.redis.template.model.UserCache;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveRedisTemplateAdapter extends ReactiveTemplateAdapterOperations<User/* change for domain model */,
        String, UserCache/* change for adapter model */> implements UserCacheRepository
// implements ModelRepository from domain
{

    private final long ttl;

    public ReactiveRedisTemplateAdapter(ReactiveRedisConnectionFactory connectionFactory, ObjectMapper mapper,
                                        @Value("${adapter.redis.ttl}") long ttl) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, User.class/* change for domain model */));
        this.ttl = ttl;
    }

    @Override
    public Mono<User> findUserByID(Long id) {
        return this.findById(String.valueOf(id));
    }

    @Override
    public Mono<User> saveUser(User user) {
        return this.save(String.valueOf(user.getId()), user, ttl);
    }
}
