package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserAddressRepository extends PageableRepository<UserAddress, Long> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "address", type = Join.Type.LEFT_FETCH)
    Iterable<UserAddress> findByUser(User user);
}
