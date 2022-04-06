package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.AddressRepository;
import world.deslauriers.repository.UserAddressRepository;

import java.util.HashSet;
import java.util.Optional;

@Singleton
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Inject
    private final AddressRepository addressRepository;

    @Inject
    private final UserAddressRepository userAddressRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserAddressRepository userAddressRepository) {
        this.addressRepository = addressRepository;
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public void resolveAddresses(HashSet<Address> addresses, User user) {

        // remove address
        if (addresses.size() == 0){
            user.userAddresses().forEach(userAddress -> {
                userAddressRepository.delete(userAddress);
                addressRepository.delete(userAddress.address());
            });
        }

        // only allowed to add one or edit one
        if (addresses.size() > 1){
            log.error("Attempt to add more than one address to user " + user.username());
            throw new IllegalArgumentException("Can only add/edit one address.");
        }

        // user has no address
        if (addresses.iterator().hasNext() && !user.userAddresses().iterator().hasNext()){


            // cannot add existing record to user
            if (addresses.iterator().next().id() != null){
                log.error("Cannot add existing or possible existing record: " + addresses.iterator().next());
                throw new IllegalArgumentException("Cannot add possible existing record.");
            }

            var userAddress = userAddressRepository.save(
                    new UserAddress(user, addressRepository.save(addresses.iterator().next())));
            log.info("Added address " + userAddress.address() + " to " + user.username());
        }

        // user has address
        if (addresses.iterator().hasNext() && user.userAddresses().iterator().hasNext()){

            // cannot add if address exists
            if (addresses.iterator().next().id() == null){
                log.error("Attempt to add additional address " + addresses.iterator().next() + " to user " + user.username());
                throw new IllegalArgumentException("Address record exists. Cannot add new. Edit existing record.");
            }

            // user must own record being edited
            if (!addresses.iterator().next().id().equals(user.userAddresses().iterator().next().address().id())){
                log.error("Attempt to edit address " + addresses.iterator().next() + " that isn't owned by user " + user.username());
                throw new IllegalArgumentException("Can only edit record user owns.");
            }

            var updated = addressRepository.update(addresses.iterator().next());
            log.info("Updated " + user.userAddresses().iterator().next().address() + " to " + updated + "on user " + user.username());
        }

    }
}
