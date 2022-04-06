package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.AddressRepository;
import world.deslauriers.repository.UserAddressRepository;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AddressServiceTest {

    @Inject private AddressRepository addressRepository;
    @Inject private AddressService addressService;
    @Inject private UserService userService;
    @Inject private UserAddressRepository userAddressRepository;

    private static final String VALID_EMAIL = "admin@deslauriers.world"; // test data

    @Test
    void testAddressServiceMethods(){

        // from test data
        var user = userService.lookupUserByUsername(VALID_EMAIL).get();
        var current = addressRepository.findById(user.userAddresses().iterator().next().id()).get();

        // edit existing
        var addresses = new HashSet<Address>();
        addresses.add(new Address(current.id(), "111 New Address Street", current.city(), current.state(), current.zip()));
        addressService.resolveAddresses(addresses, user);
        var userAddresses = userAddressRepository.findByUser(user);
        assertEquals(1, ((Collection<?>) userAddresses).size());
        assertEquals("111 New Address Street", userAddresses.iterator().next().address().address());

        // must fail to add more than one address at input.
        addresses.add(new Address("789 Condo Lane", "Cityopolis", "DC", "33333"));
        var thrown = assertThrows(IllegalArgumentException.class, () ->{
            addressService.resolveAddresses(addresses, user);
        });
        assertEquals("Can only add/edit one address.", thrown.getMessage());

        // must fail to add new address if one already associated.
        addresses.clear();
        addresses.add(new Address("789 Condo Lane", "Cityopolis", "DC", "33333"));
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            addressService.resolveAddresses(addresses, user);
        });
        assertEquals("Address record exists. Cannot add new. Edit existing record.", thrown.getMessage());

        // clear xrefs
        userAddresses.forEach(userAddress -> userAddressRepository.delete(userAddress));
        assertFalse(userAddressRepository.findByUser(user).iterator().hasNext());

        // must not add existing/possible record (has id) to user
        addresses.clear();
        addresses.add(current);
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            addressService.resolveAddresses(addresses, userService.lookupUserByUsername("admin@deslauriers.world").get());
        });
        assertEquals("Cannot add possible existing record.", thrown.getMessage());

        addresses.clear();

        // add new address
        addresses.add(new Address("789 Condo Lane", "Cityopolis", "DC", "33333"));
        addressService.resolveAddresses(addresses, userService.lookupUserByUsername("admin@deslauriers.world").get());

        // must not allow edit of record that user does not own:
        addresses.clear();
        addresses.add(new Address(2L, current.address(), current.city(), current.state(), current.zip()));
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            addressService.resolveAddresses(addresses, userService.lookupUserByUsername("admin@deslauriers.world").get());
        });
        assertEquals("Can only edit record user owns.", thrown.getMessage());

        // fail validation
        // happens prior to service firing.
        userAddressRepository
                .findByUser(user)
                .forEach(userAddress -> userAddressRepository.delete(userAddress));
        addresses.clear();
        addresses.add(new Address("", "Nowheresville", "DC", "33333"));

        var fail = assertThrows(ConstraintViolationException.class, () -> {
            addressService.resolveAddresses(addresses, userService.lookupUserByUsername("admin@deslauriers.world").get());
        });
        assertEquals("save.entity.address: must not be blank", fail.getMessage());

        // empty array should delete xref and delete record.
        addresses.clear();
        addressService.resolveAddresses(addresses, user);
        assertFalse(userService.lookupUserByUsername(VALID_EMAIL).get().userAddresses().iterator().hasNext());
        assertEquals(0, userService.lookupUserByUsername(VALID_EMAIL).get().userAddresses().size());

    }

}
