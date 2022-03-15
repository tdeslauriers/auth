package world.deslauriers.model.profile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;

@Introspected
public record ProfileDto(
        Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @Size(min = 1, max = 32) String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32) String lastname,
        LocalDate dateCreated,
        Boolean enabled,
        Boolean accountExpired,
        Boolean accountLocked,
        HashSet<Role> roles,
        HashSet<Address> addresses,
        HashSet<Phone> phones
) {
    public ProfileDto(@NonNull String username, @NonNull String firstname, @NonNull String lastname, LocalDate dateCreated, Boolean enabled, Boolean accountExpired, Boolean accountLocked, HashSet<Role> roles, HashSet<Address> addresses, HashSet<Phone> phones) {
        this(null, username, firstname, lastname, dateCreated, enabled, accountExpired, accountLocked, roles, addresses, phones);
    }
}
