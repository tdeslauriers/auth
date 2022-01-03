package world.deslauriers.model.database;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
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
public record Address(
        @Id @GeneratedValue Long id,
        @NonNull @Size(max = 128) String address,
        @NonNull @Size(max = 64) String city,
        @NonNull @Size(max = 32) String state,
        @NotNull @Size(min = 5, max = 16) Integer zip,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "address")
        @JoinTable(name = "user_address")
        Set<UserAddress> userAddresses

) {}
