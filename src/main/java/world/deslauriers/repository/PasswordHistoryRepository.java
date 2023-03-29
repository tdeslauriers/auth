package world.deslauriers.repository;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import world.deslauriers.model.database.PasswordHistory;
import world.deslauriers.model.database.User;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface PasswordHistoryRepository extends ReactorCrudRepository<PasswordHistory, Long> {

    Flux<PasswordHistory> findByUser(User user);
}
