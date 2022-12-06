package world.deslauriers.service;

import world.deslauriers.model.database.UserAddress;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> getByAddressId(Long id);
}
