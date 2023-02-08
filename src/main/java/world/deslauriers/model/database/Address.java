package world.deslauriers.model.database;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.LettersOnly;
import world.deslauriers.validation.NoSpecialChars;
import world.deslauriers.validation.NumbersOnly;
import world.deslauriers.validation.UsState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Serdeable
@MappedEntity
public record Address(
        @Id @GeneratedValue Long id,
        @NonNull @NotBlank @NoSpecialChars @Size(max = 128) String address,
        @NonNull @NotBlank @LettersOnly @Size(max = 64) String city,
        @NonNull @NotBlank @UsState @Size(min = 2, max = 2) String state,
        @NotNull @NotBlank @NumbersOnly @Size(min = 5, max = 5) String zip,

        @Nullable
        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "address")
        Set<UserAddress> userAddresses

) {
    public Address(@NonNull String address, @NonNull String city, @NonNull String state, String zip) {
            this(null, address, city, state, zip, null);
    }

    public Address(Long id, @NonNull String address, @NonNull String city, @NonNull String state, String zip) {
            this(id, address, city, state, zip, null);
    }

    // for deletion success
    public Address(Long id) {
        this(id, null, null, null, null, null);
    }
}
