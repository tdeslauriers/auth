package world.deslauriers.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

import static io.micronaut.data.annotation.Relation.Kind;

@Serdeable
@MappedEntity
public record UserRole(
        @Id Long id,

        @JsonIgnore
        @Nullable
        @Relation(Kind.MANY_TO_ONE)
        User user,

        @Nullable
        @Relation(Kind.MANY_TO_ONE)
        Role role
) {
        public UserRole(@Nullable User user, @Nullable Role role) {
                this(null, user, role);
        }
}
