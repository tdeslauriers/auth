package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;

import static io.micronaut.data.annotation.Join.*;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserAddressRepository extends ReactorCrudRepository<UserAddress, Long> {

    @Join(value = "user", type = Type.LEFT_FETCH)
    @Join(value = "address", type = Type.LEFT_FETCH)
    Flux<UserAddress> findByUser(User user);

    @Join(value = "user", type = Type.LEFT_FETCH)
    @Join(value = "address", type = Type.LEFT_FETCH)
    Flux<UserAddress> findByAddressId(Long id);
}
