package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserRoleRepository extends PageableRepository<UserRole, Long> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "role", type = Join.Type.LEFT_FETCH)
    Optional<UserRole> findByUserAndRole(User user, Role role);
}
