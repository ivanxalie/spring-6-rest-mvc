package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    private BeerRepository repository;

    @Test
    void saveBeer() {
        Beer saved = repository.save(Beer.builder()
                .name("My Beer")
                .build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);
    }
}