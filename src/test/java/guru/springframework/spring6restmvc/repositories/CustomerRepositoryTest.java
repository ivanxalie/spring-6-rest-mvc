package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository repository;

    @Test
    void saveCustomer() {
        Customer saved = repository.save(Customer.builder().name("Alex").build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);
    }
}