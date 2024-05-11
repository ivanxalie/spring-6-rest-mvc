package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class})
class BeerRepositoryTest {

    @Autowired
    private BeerRepository repository;

    @Test
    void testInit() {
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void saveBeer() {
        long countBefore = repository.count();
        Beer saved = repository.save(Beer.builder()
                .name("My Beer")
                .upc("1231231")
                .price(BigDecimal.ONE)
                .beerStyle(BeerStyle.WHEAT)
                .build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void saveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.save(Beer.builder()
                    .name("A".repeat(51))
                    .upc("1231231")
                    .price(BigDecimal.ONE)
                    .beerStyle(BeerStyle.WHEAT)
                    .build());

            repository.flush();
        });
    }
}