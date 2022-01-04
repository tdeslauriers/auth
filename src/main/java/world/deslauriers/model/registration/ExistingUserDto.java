package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Introspected
public record ExistingUserDto(
        @NonNull @NotBlank @Email @Size(max = 254) String username) {}
