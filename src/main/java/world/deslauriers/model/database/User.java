package world.deslauriers.model.database;


import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.*;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Introspected
@MappedEntity
public record User(
        @Id @GeneratedValue Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank String password,
        @NonNull @NotBlank @Size(min = 1, max = 32)String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32)String lastname,
        @DateCreated @NotNull LocalDate dateCreated,
        @NotNull Boolean enabled,
        @NotNull Boolean accountExpired,
        @NotNull Boolean accountLocked,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_role")
        Set<UserRole> userRoles,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_address")
        Set<UserAddress> userAddresses,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_phone")
        Set<UserPhone> userPhones
) {}
