package world.deslauriers.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.Address;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface AddressRepository extends PageableRepository<Address, Long> {
}
