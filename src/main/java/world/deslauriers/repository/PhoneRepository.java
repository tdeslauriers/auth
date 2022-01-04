package world.deslauriers.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.Phone;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface PhoneRepository extends PageableRepository<Phone, Long> {
}
