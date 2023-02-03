package world.deslauriers.repository;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Phone;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface PhoneRepository extends ReactorCrudRepository<Phone, Long> {

    Mono<Phone> findByPhone(String phone);
}
