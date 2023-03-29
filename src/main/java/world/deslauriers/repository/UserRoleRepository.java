package world.deslauriers.repository;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserRoleRepository extends ReactorCrudRepository<UserRole, Long> {

    Mono<UserRole> findByUserAndRole(User user, Role role);

    Flux<UserRole> findByRoleId(Long id);

}
