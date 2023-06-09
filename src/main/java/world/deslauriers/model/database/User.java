package world.deslauriers.model.database;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.LettersOnly;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

import static io.micronaut.data.annotation.Relation.Kind;

@Serdeable
@MappedEntity
public record User(
        @Id Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @PasswordComplexity String password,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32)String firstname,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32)String lastname,
        @JsonFormat(pattern="yyyy-MM-dd") @NotNull LocalDate dateCreated,
        @NotNull Boolean enabled,
        @NotNull Boolean accountExpired,
        @NotNull Boolean accountLocked,
        @JsonFormat(pattern="yyyy-MM-dd") @Nullable LocalDate birthday,

        @NonNull String uuid,

        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "user")
        Set<UserRole> userRoles,

        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "user")
        Set<UserAddress> userAddresses,

        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "user")
        Set<UserPhone> userPhones,

        @Nullable
        @JsonIgnore
        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "user")
        Set<PasswordHistory> passwordHistories
) {
        public User(@NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, @Nullable LocalDate birthday, @NonNull String uuid) {
                this(null, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, birthday, uuid, null, null, null, null);
        }

        public User(Long id, @NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, @Nullable LocalDate birthday, @NonNull String uuid) {
                this(id, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, birthday, uuid, null, null, null, null);
        }

        public User(@NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, @NonNull String uuid) {
                this(null, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, null, uuid, null, null, null, null);
        }

        public User(Long id, @NonNull String username, @NonNull String password, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, @NonNull String uuid) {
                this(id, username, password, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, null, uuid, null, null, null, null);
        }
}
