package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.User;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserRepository extends ReactorCrudRepository<User, Long> {

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses.address", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones.phone", type = Join.Type.LEFT_FETCH)
    Mono<User> findByUsername(String email);

    @Query("SELECT username FROM user u WHERE username = :email")
    Mono<String> findUsername(String email);

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses.address", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones.phone", type = Join.Type.LEFT_FETCH)
    Mono<User> findById(Long id);

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses.address", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones.phone", type = Join.Type.LEFT_FETCH)
    Flux<User> findAll();

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses.address", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones.phone", type = Join.Type.LEFT_FETCH)
    Mono<User> findByUuid(String uuid);
}
