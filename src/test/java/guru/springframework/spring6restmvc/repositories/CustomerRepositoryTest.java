package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private BeerCsvService csvService;

    @Value("classpath:csvdata/beers.csv")
    Resource resource;

    private BootstrapData data;

    @MockBean
    private BeerRepository beerRepository;


    @BeforeEach
    void setUp() {
        data = new BootstrapData(beerRepository, customerRepository, csvService);
        data.setResource(resource);
        data.init();
    }

    @Test
    void testInit() {
        assertThat(customerRepository.count()).isEqualTo(3);
        data.init();
    }

    @Test
    void saveCustomer() {
        long countBefore = customerRepository.count();
        Customer saved = customerRepository.save(Customer.builder().name("Alex").build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(customerRepository.count()).isEqualTo(countBefore + 1);
    }
}