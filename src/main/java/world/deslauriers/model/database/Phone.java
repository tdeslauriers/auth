package world.deslauriers.model.database;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.NumbersOnly;
import world.deslauriers.validation.ValidPhoneType;

import javax.validation.constraints.Size;
import java.util.Set;

import static io.micronaut.data.annotation.Relation.Kind;

@Serdeable
@MappedEntity
public record Phone(
        @Id @GeneratedValue Long id,
        @NonNull @NumbersOnly @Size(min = 9, max = 32) String phone,
        @NonNull @ValidPhoneType @Size(min = 4, max= 4) String type,

        @Nullable
        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "phone")
        Set<UserPhone> userPhones
) {
        public Phone(@NonNull String phone, @NonNull String type) {
                this(null, phone, type, null);
        }

        public Phone(Long id, @NonNull String phone, @NonNull String type) {
                this(id, phone, type, null);
        }
}
