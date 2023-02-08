package world.deslauriers.model.registration;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Serdeable
public record RegistrationDto(
        @NonNull @NotBlank @Email @Size(max = 255) String username,
        @NonNull @NotBlank @PasswordComplexity String password, // different size from db cuz raw
        @NonNull @NotBlank @PasswordComplexity String confirmPassword,
        @NonNull @NotBlank @Size(min = 1, max = 32)String firstname,
        @NonNull @NotBlank @Size(min = 1, max = 32)String lastname
) {}
