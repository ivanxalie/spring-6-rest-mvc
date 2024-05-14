package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {
    @Value("classpath:csvdata/beers.csv")
    Resource resource;

    @Autowired
    private BeerRepository beerRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private BeerCsvService csvService;

    private BootstrapData data;

    @BeforeEach
    void setUp() {
        data = new BootstrapData(beerRepository, customerRepository, csvService);
        data.setResource(resource);
        data.init();
    }

    @Test
    void testInit() {
        assertThat(beerRepository.count()).isEqualTo(3);
    }

    @Test
    void saveBeer() {
        long countBefore = beerRepository.count();
        Beer saved = beerRepository.save(Beer.builder()
                .name("My Beer")
                .upc("1231231")
                .price(BigDecimal.ONE)
                .beerStyle(BeerStyle.WHEAT)
                .build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(beerRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void saveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(Beer.builder()
                    .name("A".repeat(51))
                    .upc("1231231")
                    .price(BigDecimal.ONE)
                    .beerStyle(BeerStyle.WHEAT)
                    .build());

            beerRepository.flush();
        });
    }
}