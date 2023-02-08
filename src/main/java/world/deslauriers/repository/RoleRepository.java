package world.deslauriers.repository;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RoleRepository extends ReactorCrudRepository<Role, Long> {

    Mono<Role> findByRole(String role);
}
