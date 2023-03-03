package world.deslauriers.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Refresh;

import javax.validation.constraints.NotBlank;

@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RefreshTokenRepository extends ReactorCrudRepository<Refresh, Long> {

    Mono<Refresh> findByRefreshToken(@NonNull @NotBlank String refreshToken);
}
