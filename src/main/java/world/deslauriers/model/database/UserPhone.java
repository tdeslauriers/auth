package world.deslauriers.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

import static io.micronaut.data.annotation.Relation.Kind;

@Serdeable
@MappedEntity
public record UserPhone(
        @Id @GeneratedValue Long id,

        @JsonIgnore
        @Nullable
        @Relation(value = Kind.MANY_TO_ONE)
        User user,

        @Nullable
        @Relation(value = Kind.MANY_TO_ONE)
        Phone phone
) {
        public UserPhone(@Nullable User user, @Nullable Phone phone) {
                this(null, user, phone);
        }
}
