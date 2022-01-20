package world.deslauriers.model.database;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Introspected
@MappedEntity
public record Address(
        @Id @GeneratedValue Long id,
        @NonNull @NotBlank @Size(max = 128) String address,
        @NonNull @NotBlank @Size(max = 64) String city,
        @NonNull @NotBlank @Size(max = 32) String state,
        @NotNull @NotBlank @Size(min = 5, max = 10) String zip,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "address")
        @JoinTable(name = "user_address")
        Set<UserAddress> userAddresses

) {
        public Address(@NonNull String address, @NonNull String city, @NonNull String state, String zip) {
                this(null, address, city, state, zip, null);
        }

        public Address(Long id, @NonNull String address, @NonNull String city, @NonNull String state, String zip) {
                this(id, address, city, state, zip, null);
        }
}
