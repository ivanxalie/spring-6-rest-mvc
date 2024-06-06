package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback
class CustomerControllerIntegrationTest {
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CustomerController controller;

    @MockBean
    private CacheManager manager;

    @Test
    void customers() {
        List<CustomerDTO> customers = controller.customers();
        assertThat(customers).isNotNull();
        assertThat(customers.size()).isEqualTo(repository.count());
    }

    @Test
    void customersAreEmpty() {
        repository.deleteAll();

        List<CustomerDTO> customers = controller.customers();
        assertThat(customers.size()).isEqualTo(0);
    }

    @Test
    void findById() {
        Customer customer = repository.findAll().getFirst();
        assertThat(customer).isNotNull();

        CustomerDTO customerDTO = controller.findById(customer.getId());
        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.getId()).isEqualTo(customer.getId());
    }

    @Test
    void findByIdNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.findById(UUID.randomUUID()));
    }

    @Test
    void saveNewCustomer() {
        long countBefore = repository.count();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Alex")
                .build();

        ResponseEntity<CustomerDTO> response = controller.saveNewCustomer(customerDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        CustomerDTO responseCustomer = response.getBody();

        assertThat(responseCustomer).isNotNull();
        assertThat(repository.existsById(responseCustomer.getId())).isNotNull();
        assertThat(repository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void updateById() {
        Customer customer = repository.findAll().getFirst();

        String newName = "Harry Houdini";

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name(newName)
                .build();

        ResponseEntity<?> response = controller.updateById(customer.getId(), customerDTO);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        customer = repository.findById(customer.getId()).orElseThrow();

        assertThat(customer.getName()).isEqualTo(newName);
    }

    @Test
    void updateByIdNotFound() {
        repository.deleteAll();

        assertThrows(NotFountException.class, () -> controller.findById(UUID.randomUUID()));
    }

    @Test
    void deleteById() {
        long countBefore = repository.count();
        Customer customer = repository.findAll().getFirst();
        ResponseEntity<?> response = controller.deleteById(customer.getId());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.existsById(customer.getId())).isFalse();
        assertThat(repository.count()).isEqualTo(countBefore - 1);
    }

    @Test
    void deleteByIdNotFound() {
        repository.deleteAll();

        assertThrows(NotFountException.class, () -> controller.deleteById(UUID.randomUUID()));
    }
}