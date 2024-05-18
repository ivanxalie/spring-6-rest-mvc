package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

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

    private BeerCsvService csvService = new BeerCsvServiceImpl();

    private BootstrapData data;

    @BeforeEach
    void setUp() {
        data = new BootstrapData(beerRepository, customerRepository, csvService);
        data.setResource(resource);
        data.init();
    }

    @Test
    void testInit() {
        assertThat(beerRepository.count()).isEqualTo(2413);
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

    @Test
    void getListByName() {
        Page<Beer> beers = beerRepository.findAllByNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(beers.getTotalElements()).isEqualTo(336);
    }

    @Test
    void getListByStyle() {
        Page<Beer> beers = beerRepository.findAllByBeerStyle(BeerStyle.WHEAT, null);

        assertThat(beers.getTotalElements()).isEqualTo(75);
    }
}