package world.deslauriers.repository;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import world.deslauriers.model.database.Address;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface AddressRepository extends ReactorCrudRepository<Address, Long> {

}
