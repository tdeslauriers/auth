package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.UserRole;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserRoleRepository extends ReactorCrudRepository<UserRole, Long> {
    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "role", type = Join.Type.LEFT_FETCH)
    Mono<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "role", type = Join.Type.LEFT_FETCH)
    Flux<UserRole> findByRoleId(Long id);

}
