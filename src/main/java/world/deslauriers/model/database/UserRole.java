package world.deslauriers.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinTable;

@Introspected
@MappedEntity
public record UserRole(
        @Id @GeneratedValue Long id,

        @JsonIgnore
        @Nullable
        @Relation(Relation.Kind.MANY_TO_ONE)
        @JoinTable(name = "user")
        User user,

        @Nullable
        @Relation(Relation.Kind.MANY_TO_ONE)
        @JoinTable(name = "role")
        Role role
) {
        public UserRole(@Nullable User user, @Nullable Role role) {
                this(null, user, role);
        }
}
