package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class UserPhoneDaoTest {

    @Inject private UserPhoneRepository userPhoneRepository;
    @Inject private UserRepository userRepository;

    // from test data
    public static final String VALID_USER = "admin@deslauriers.world";
    public static final String VALID_PHONE = "1112223333";


    @Test
    void testUserPhoneCrud() {

        var user = userRepository.findByUsername(VALID_USER).get();
        var userphones = userPhoneRepository.findByUser(user);
        assertEquals(1L, userphones.spliterator().getExactSizeIfKnown());
        StreamSupport.stream(userphones.spliterator(), false).anyMatch(userPhone -> userPhone.phone().phone().equals(VALID_PHONE));
        assertTrue(StreamSupport
                .stream(userphones.spliterator(), false)
                .anyMatch(userPhone -> userPhone.phone().phone().equals(VALID_PHONE)));
    }
}
