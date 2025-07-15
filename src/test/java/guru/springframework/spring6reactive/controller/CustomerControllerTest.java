package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.model.CustomerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void testListCustomers() {
        webTestClient.get().uri(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.size()").isEqualTo(3);

    }

    @Test
    @Order(2)
    void testGetCustomer() {
        webTestClient.get().uri(CustomerController.CUSTOMER_ID_PATH, 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerDTO.class);
    }

    @Test
    void testGetCustomerNotFound() {
        webTestClient.get().uri(CustomerController.CUSTOMER_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testCreateCustomer() {

        webTestClient.post().uri(CustomerController.CUSTOMER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getCustomerDTO()), CustomerDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost:8080/api/v2/customer/4");
    }

    @Test
    void testCreateCustomerBadRequest() {

        CustomerDTO customerDTO = getCustomerDTO();
        customerDTO.setCustomerName("");

        webTestClient.post().uri(CustomerController.CUSTOMER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testUpdateCustomer() {
        webTestClient.put().uri(CustomerController.CUSTOMER_ID_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getCustomerDTO()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateCustomerNotFound() {
        webTestClient.put().uri(CustomerController.CUSTOMER_ID_PATH, 999)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getCustomerDTO()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateCustomerBadRequest() {
        CustomerDTO customerDTO = getCustomerDTO();
        customerDTO.setCustomerName("");

        webTestClient.put().uri(CustomerController.CUSTOMER_ID_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPatchCustomerNotFound() {
        webTestClient.patch().uri(CustomerController.CUSTOMER_ID_PATH, 999)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getCustomerDTO()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchCustomerBadRequest() {
        CustomerDTO customerDTO = getCustomerDTO();
        customerDTO.setCustomerName("");

        webTestClient.patch().uri(CustomerController.CUSTOMER_ID_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void testDeleteCustomer() {
        webTestClient.delete().uri(CustomerController.CUSTOMER_ID_PATH, 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteCustomerNotFound() {
        webTestClient.delete().uri(CustomerController.CUSTOMER_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    public static CustomerDTO getCustomerDTO() {
        return CustomerDTO.builder()
                .customerName("Test Customer")
                .build();
    }
}