package world.deslauriers.model.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.Role;
import world.deslauriers.validation.LettersOnly;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;

@Serdeable
public record ProfileDto(
        Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32) String firstname,
        @NonNull @NotBlank @LettersOnly @Size(min = 1, max = 32) String lastname,
        @JsonFormat(pattern="yyyy-MM-dd") LocalDate dateCreated,
        Boolean enabled,
        Boolean accountExpired,
        Boolean accountLocked,
        @JsonFormat(pattern="yyyy-MM-dd") @Nullable LocalDate birthday,
        String uuid,
        HashSet<Role> roles,
        HashSet<Address> addresses,
        HashSet<Phone> phones
) {}
