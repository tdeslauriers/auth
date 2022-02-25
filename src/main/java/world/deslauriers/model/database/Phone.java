package world.deslauriers.model.database;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.Size;
import java.util.Set;

@Introspected
@MappedEntity
public record Phone(
        @Id @GeneratedValue Long id,
        @NonNull @Size(min = 9, max = 32) String phone,
        @NonNull @Size(min = 4, max= 4) String type,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "phone")
        @JoinTable(name = "user_phone")
        Set<UserPhone> userPhones
) {
        public Phone(@NonNull String phone, @NonNull String type) {
                this(null, phone, type, null);
        }

        public Phone(Long id, @NonNull String phone, @NonNull String type) {
                this(id, phone, type, null);
        }
}
