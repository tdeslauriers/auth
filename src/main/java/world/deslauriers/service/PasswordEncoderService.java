package world.deslauriers.service;

import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;

public interface PasswordEncoderService {

    String encode(@NotBlank @NonNull String rawPassword);
    Boolean matches (@NotBlank @NonNull String rawPassword,
                     @NotBlank @NonNull String encodedPassword);
}
