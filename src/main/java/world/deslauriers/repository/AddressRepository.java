package world.deslauriers.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.Address;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface AddressRepository extends PageableRepository<Address, Long> {

    @Query("SELECT * FROM address WHERE address = :address AND city = :city AND state = :state AND zip = :zip")
    Optional<Address> findByAddress(String address, String city, String state, String zip);
}
