package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.model.CustomerDTO;
import guru.springframework.spring6reactive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public static final String CUSTOMER_PATH = "/api/v2/customer";
    public static final String CUSTOMER_ID_PATH = CUSTOMER_PATH + "/{customerId}";

    @GetMapping(CUSTOMER_PATH)
    Flux<CustomerDTO> listBeers() {
        return customerService.listCustomers();
    }

    @GetMapping(CUSTOMER_ID_PATH)
    Mono<CustomerDTO> customerById(@PathVariable Integer customerId) {
        return customerService.getCustomerById(customerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping(CUSTOMER_PATH)
    Mono<ResponseEntity<Void>> saveNewCustomer(@Validated @RequestBody CustomerDTO customerDTO) {
        return customerService.saveNewCustomer(customerDTO)
                .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                                .fromUriString("http://localhost:8080/" + CUSTOMER_PATH + "/" + savedDto.getId())
                                .build().toUri())
                        .build());
    }

    @PutMapping(CUSTOMER_ID_PATH)
    Mono<ResponseEntity<Void>> updateCustomer(@PathVariable Integer customerId, @Validated @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(customerId, customerDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(savedDto -> ResponseEntity.noContent().build());
    }

    @PatchMapping(CUSTOMER_ID_PATH)
    Mono<ResponseEntity<Void>> partiallyUpdateCustomer(@PathVariable Integer customerId, @Validated @RequestBody CustomerDTO customerDTO) {
        return customerService.partiallyUpdateCustomer(customerId, customerDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(savedDto -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(CUSTOMER_ID_PATH)
    Mono<ResponseEntity<Void>> deleteCustomerById(@PathVariable Integer customerId) {

        return customerService.getCustomerById(customerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(customerDTO -> customerService.deleteCustomerById(customerDTO.getId()))
                .thenReturn(ResponseEntity.noContent().build());
    }

}
