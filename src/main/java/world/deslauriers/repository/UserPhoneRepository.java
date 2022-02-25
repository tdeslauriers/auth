package world.deslauriers.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserPhone;

import java.util.List;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserPhoneRepository extends PageableRepository<UserPhone, Long> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "phone", type = Join.Type.LEFT_FETCH)
    List<UserPhone> findByUser(User user);

}
