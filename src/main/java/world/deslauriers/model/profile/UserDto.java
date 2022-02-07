package world.deslauriers.model.profile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Introspected
public record UserDto(
        @NonNull Long id,
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @Size(min = 1, max = 32) String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32) String lastname,
        @NonNull LocalDate dateCreated,
        @NonNull Boolean enabled,
        @NonNull Boolean accountExpired,
        @NonNull Boolean accountLocked
) {}
