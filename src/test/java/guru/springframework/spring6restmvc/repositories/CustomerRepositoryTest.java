package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BootstrapData.class)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository repository;

    @Test
    void testInit() {
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void saveCustomer() {
        long countBefore = repository.count();
        Customer saved = repository.save(Customer.builder().name("Alex").build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(countBefore + 1);
    }
}