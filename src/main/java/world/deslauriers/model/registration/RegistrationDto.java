package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Introspected
public record RegistrationDto(
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @Size(min = 12, max = 64) String password, // different size from db cuz raw
        @NonNull @NotBlank @Size(min = 12, max = 64) String confirmPassword,
        @NonNull @NotBlank @Size(min = 1, max = 32)String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32)String lastname
) {}
