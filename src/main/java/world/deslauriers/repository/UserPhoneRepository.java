package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserPhone;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserPhoneRepository extends ReactorCrudRepository<UserPhone, Long> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "phone", type = Join.Type.LEFT_FETCH)
    Flux<UserPhone> findByUser(User user);

}
