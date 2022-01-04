package world.deslauriers.model.database;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Introspected
@MappedEntity
public record Phone(
        @Id @GeneratedValue Long id,
        @NotNull @Size(min = 9, max = 32) Integer phone,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "phone")
        @JoinTable(name = "user_phone")
        Set<UserPhone> userPhones
) {
        public Phone(Integer phone) {
                this(null, phone, null);
        }

        public Phone(Long id, Integer phone) {
                this(id, phone, null);
        }
}
