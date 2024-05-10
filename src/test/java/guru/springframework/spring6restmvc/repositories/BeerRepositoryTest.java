package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

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
                .build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(countBefore + 1);
    }
}