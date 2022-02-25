package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class PhoneDaoTest {

    @Inject private PhoneRepository phoneRepository;

    @Test
    void testPhoneCrud(){

        // from test data
        var phone = phoneRepository.findByPhone("1112223333");
        assertTrue(phone.isPresent());
        assertNotNull(phone.get().id());
        assertEquals("1112223333", phone.get().phone());
        assertEquals("CELL", phone.get().type());

        phone = phoneRepository.findByPhone("Not a phone");
        assertTrue(phone.isEmpty());
    }
}
