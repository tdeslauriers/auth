package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.AddressRepository;
import world.deslauriers.repository.UserAddressRepository;

import javax.validation.ConstraintViolationException;
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

    @Test
    void testAddressServiceMethods(){

        // from test data
        var user = userService.lookupUserByUsername("admin@deslauriers.world").get();
        var userAddresses = userAddressRepository.findByUser(user);
        assertTrue(userAddresses.iterator().hasNext());
        assertEquals(1, ((Collection<?>) userAddresses).size());

        var address = addressRepository.findByAddress(
                userAddresses.iterator().next().address().address(),
                userAddresses.iterator().next().address().city(),
                userAddresses.iterator().next().address().state(),
                userAddresses.iterator().next().address().zip());
        assertTrue(address.isPresent());

        // edit existing
        var addresses = new HashSet<Address>();
        addresses.add(new Address(
                userAddresses.iterator().next().id(),
                "111 New Address Street",
                userAddresses.iterator().next().address().city(),
                userAddresses.iterator().next().address().state(),
                userAddresses.iterator().next().address().zip()));
        addressService.resolveAddresses(addresses, user);
        userAddresses = userAddressRepository.findByUser(user);
        assertEquals(1, ((Collection<?>) userAddresses).size());
        userAddresses.forEach(userAddress -> System.out.println(userAddress.address()));
        assertEquals("111 New Address Street", ((Collection<UserAddress>) userAddresses)
                .stream()
                .filter(userAddress -> userAddress.address().address().equals("111 New Address Street"))
                .findFirst().get().address().address());

        // must fail to add more than one address.
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
        assertEquals("Address record exists. Cannot add new. Edit existing record.",
                    thrown.getMessage());

        // add new address
        // first need to clear xrefs
        userAddresses.forEach(userAddress -> userAddressRepository.delete(userAddress));
        userAddresses = userAddressRepository.findByUser(user);
        assertFalse(userAddresses.iterator().hasNext());
        addresses.add(new Address("789 Condo Lane", "Cityopolis", "DC", "33333"));
        addressService.resolveAddresses(addresses, user);

        // must check for existing address before adding address record:
        userAddressRepository
                .findByUser(user)
                .forEach(userAddress -> userAddressRepository.delete(userAddress));
        userAddresses = userAddressRepository.findByUser(user);
        userAddresses.forEach(userAddress -> System.out.println("Testing:" + userAddress));
        assertFalse(userAddresses.iterator().hasNext());
        addresses.clear();
        var existing = addressService.getByAddress("789 Condo Lane", "Cityopolis", "DC", "33333");
        assertTrue(existing.isPresent());
        addresses.add(existing.get());
        addressService.resolveAddresses(addresses, user);
        userAddresses = userAddressRepository.findByUser(user);
        userAddresses.forEach(userAddress -> {
           if (userAddress.address().address().equals("789 Condo Lane")){
               assertEquals(existing.get().id(), userAddress.address().id());
            }
        });

        // fail validation
        userAddressRepository
                .findByUser(user)
                .forEach(userAddress -> userAddressRepository.delete(userAddress));
        addresses.clear();
        addresses.add(new Address("", "Nowheresville", "DC", "33333"));

        var fail = assertThrows(ConstraintViolationException.class, () -> {
            addressService.resolveAddresses(addresses, user);
        });
        assertEquals("save.entity.address: must not be blank", fail.getMessage());
    }

}
