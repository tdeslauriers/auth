package world.deslauriers.repository;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.User;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserRepository extends PageableRepository<User, Long> {

    @Join(value = "userRoles", type = Join.Type.LEFT_FETCH)
    @Join(value = "userRoles.role", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses", type = Join.Type.LEFT_FETCH)
    @Join(value = "userAddresses.address", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones", type = Join.Type.LEFT_FETCH)
    @Join(value = "userPhones.phone", type = Join.Type.LEFT_FETCH)
    Optional<User> findByUsername(String email);

    @Query("SELECT username FROM user u WHERE username = :email")
    Optional<String> findUsername(String email);
}
