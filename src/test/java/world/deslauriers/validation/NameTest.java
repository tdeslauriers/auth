package world.deslauriers.validation;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record NameTest(
        @LettersOnly String name
) {
}
