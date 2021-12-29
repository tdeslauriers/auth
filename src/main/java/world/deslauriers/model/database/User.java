package world.deslauriers.model.database;


import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.*;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Introspected
@MappedEntity
public record User(
        @Id @GeneratedValue Long id,
        @NotNull @NotBlank @Email @Size(max = 40) String username,
        @NotNull @NotBlank String password,
        @DateCreated @NotNull LocalDate dateCreated,
        @NotNull Boolean enabled,
        @NotNull Boolean accountExpired,
        @NotNull Boolean accountLocked,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
        @JoinTable(name = "user_role")
        Set<UserRole> userRoles
) {
        public User(String username, String password, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked) {
                this(null, username, password, dateCreated, enabled, accountExpired, accountLocked, null);
        }

        public User(Long id, String username, String password, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked) {
                this(id, username, password, dateCreated, enabled, accountExpired, accountLocked, null);
        }
}
