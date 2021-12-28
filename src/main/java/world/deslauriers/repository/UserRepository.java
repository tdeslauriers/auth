package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.User;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserRepository extends PageableRepository<User, Long> {

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    Optional<User> findByEmail(String email);
}
