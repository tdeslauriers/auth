package world.deslauriers.model.registration;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Serdeable
public record ExistingUserDto(
        @NonNull @NotBlank @Email @Size(max = 254) String username) {}
