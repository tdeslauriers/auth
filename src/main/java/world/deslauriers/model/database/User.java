package world.deslauriers.model.database;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;
import io.micronaut.data.jdbc.annotation.JoinTable;
import world.deslauriers.validation.LettersOnly;
import world.deslauriers.validation.PasswordComplexity;

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
        @NonNull @NotBlank @PasswordComplexity String password,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32)String firstname,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32)String lastname,
        @JsonFormat(pattern="yyyy-MM-dd") @DateCreated @NotNull LocalDate dateCreated,
        @NotNull Boolean enabled,
        @NotNull Boolean accountExpired,
        @NotNull Boolean accountLocked,
        @Nullable LocalDate birthday,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_role")
        Set<UserRole> userRoles,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_address")
        Set<UserAddress> userAddresses,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_phone")
        Set<UserPhone> userPhones
) {
        public User(@NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, LocalDate birthday) {
                this(null, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, birthday, null, null, null);
        }

        public User(Long id, @NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, LocalDate birthday) {
                this(id, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, birthday, null, null, null);
        }

        public User(@NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked) {
                this(null, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, null, null, null, null);
        }

        public User(Long id, @NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked) {
                this(id, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, null, null, null, null);
        }
}
