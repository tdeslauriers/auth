package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Address;
import world.deslauriers.service.AddressService;
import world.deslauriers.service.UserAddressService;

import java.security.Principal;

@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @Inject
    protected final AddressService addressService;

    @Inject
    protected final UserAddressService userAddressService;

    public AddressController(AddressService addressService, UserAddressService userAddressService) {
        this.addressService = addressService;
        this.userAddressService = userAddressService;
    }


    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    HttpResponse deleteAddress(Principal principal, Long id){

        // user must own device
        var toDelete = addressService.findAddressUserXref(principal.getName(), id);
        if (toDelete.isEmpty()){
            log.warn("Attempt to delete address user({}) does not own: address id: {}", principal.getName(), id);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body("Address either does not exist, or you don't own it.");
        }
        addressService.deleteAddress(toDelete.get());
        return HttpResponse.ok().body(new Address(id));
    }

    @Secured({"PROFILE_ADMIN"})
    @Delete("/delete/{id}")
    HttpResponse deleteUserAddress(Long id){

        userAddressService.getByAddressId(id).forEach(addressService::deleteAddress);
        return HttpResponse.ok().body(new Address(id));
    }
}
