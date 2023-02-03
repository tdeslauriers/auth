package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import world.deslauriers.service.AddressService;
import world.deslauriers.service.UserAddressService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    protected final AddressService addressService;
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
        return addressService.deleteAddress(toDelete.get())
                .then();

    }

    @Secured({"PROFILE_ADMIN"})
    @Delete("/delete/{id}")
    @Status(HttpStatus.NO_CONTENT)
    Mono<Void> deleteUserAddress(Long id){

        return userAddressService.getByAddressId(id).forEach(addressService::deleteAddress)
                .then();

    }
}
