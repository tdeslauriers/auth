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
    public Optional<Address> getByAddress(String address, String city, String state, String zip){
        return addressRepository.findByAddress(address, city, state, zip);
    }

    @Override
    public void resolveAddresses(HashSet<Address> addresses, User user) {

        // only allowed to add one or edit one
        if (addresses.size() > 1) throw new IllegalArgumentException("Can only add/edit one address.");

        // check if user has address
        var userAddresses = userAddressRepository.findByUser(user);

        // user has no address
        if (!userAddresses.iterator().hasNext()){
            if(addresses.stream().findFirst().isPresent()){
                linkUserToAddress(addresses.stream().findFirst().get(), user);
            }
        }

        // user has address
        if (userAddresses.iterator().hasNext() &&
            addresses.stream().findFirst().isPresent()){

            if (addresses.stream().findFirst().get().id() == null){
                throw new IllegalArgumentException("Address record exists. Cannot add new. Edit existing record.");
            }

            var sb = new StringBuilder();
            // no adding; take id from existing
            var updated = userAddresses.iterator().next().address();
            if (!addresses.stream().findFirst().get().address().equals(updated.address())){
                sb
                        .append("\nUpdating address:")
                        .append(updated.id())
                        .append("'s street address: ")
                        .append(updated.address())
                        .append(" --> ")
                        .append(addresses.iterator().next().address());
            }
            if (!addresses.stream().findFirst().get().city().equals(updated.city())){
                sb
                        .append("\nUpdating address:")
                        .append(updated.id())
                        .append("'s street address: ")
                        .append(updated.city())
                        .append(" --> ")
                        .append(addresses.iterator().next().city());
            }
            if (!addresses.stream().findFirst().get().state().equals(updated.state())){
                sb
                        .append("\nUpdating address:")
                        .append(updated.id())
                        .append("'s street address: ")
                        .append(updated.state())
                        .append(" --> ")
                        .append(addresses.iterator().next().state());
            }
            if (!addresses.stream().findFirst().get().zip().equals(updated.zip())){
                sb
                        .append("\nUpdating address:")
                        .append(updated.id())
                        .append("'s street address: ")
                        .append(updated.zip())
                        .append(" --> ")
                        .append(addresses.iterator().next().zip());
            }
            if (sb.length() > 0) {
                updated = addressRepository.update(new Address(
                        updated.id(),
                        addresses.stream().findFirst().get().address(),
                        addresses.stream().findFirst().get().city(),
                        addresses.stream().findFirst().get().state(),
                        addresses.stream().findFirst().get().zip()));
                log.info("Updating " + user.username() + "'s address:" + sb.toString());
            }
        }
    }

    private void linkUserToAddress(Address address, User user){

        var lookupAddress = addressRepository.findByAddress(
                address.address(),
                address.city(),
                address.state(),
                address.zip());

        if (lookupAddress.isEmpty()){
            var linkedAddress = userAddressRepository.save(new UserAddress(user, addressRepository.save(address)));
            log.info("Created new address: " + linkedAddress.address());
            log.info(user.id() + ":" + user.username() + " associated to: " + linkedAddress.address());
        }

        if (lookupAddress.isPresent()){
            var linkedAddress = userAddressRepository.save(new UserAddress(user, lookupAddress.get()));
            log.info(user.id() + ":" + user.username() + " associated to: " + linkedAddress.address());
        }
    }
}
