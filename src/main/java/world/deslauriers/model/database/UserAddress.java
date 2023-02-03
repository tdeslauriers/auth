package world.deslauriers.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity
public record UserAddress(
        @Id @GeneratedValue Long id,

        @JsonIgnore
        @Nullable
        @Relation(Relation.Kind.MANY_TO_ONE)
        User user,

        @Nullable
        @Relation(Relation.Kind.MANY_TO_ONE)
        Address address
) {
        public UserAddress(@Nullable User user, @Nullable Address address) {
                this(null, user, address);
        }
}
