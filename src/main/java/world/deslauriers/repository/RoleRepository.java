package world.deslauriers.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.Role;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface RoleRepository extends PageableRepository<Role, Long> {
}
