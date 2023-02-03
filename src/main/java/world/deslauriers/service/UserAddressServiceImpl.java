package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.UserAddressRepository;

import java.util.List;

@Singleton
public class UserAddressServiceImpl implements UserAddressService{

    @Inject
    private final UserAddressRepository userAddressRepository;

    public UserAddressServiceImpl(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public List<UserAddress> getByAddressId(Long id) {
        return userAddressRepository.findByAddressId(id);
    }
}
