package world.deslauriers.service;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import java.security.SecureRandom;

@Singleton
public class PassWordEncoderServiceImpl implements PasswordEncoderService {

    // spring security pw encoder
    PasswordEncoder delegate = new BCryptPasswordEncoder(
            BCryptPasswordEncoder.BCryptVersion.$2A, 13, new SecureRandom());

    @Override
    public String encode(@NotBlank @NonNull String rawPassword) {

        return delegate.encode(rawPassword);
    }

    @Override
    public Boolean matches(@NotBlank @NonNull String rawPassword,
                           @NotBlank @NonNull String encodedPassword) {

        return delegate.matches(rawPassword, encodedPassword);
    }
}
