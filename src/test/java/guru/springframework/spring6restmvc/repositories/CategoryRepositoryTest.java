package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Category;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BeerRepository beerRepository;

    @MockBean
    private CacheManager manager;

    private Beer beer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().getFirst();
    }

    @Test
    @Transactional
    void testAddCategory() {
        Category saved = categoryRepository.save(Category.builder()
                .description("Ales")
                .build());

        beer.addCategory(saved);
        Beer savedBeer = beerRepository.save(beer);
        assertThat(savedBeer).isNotNull();
        log.info("beer name: {}", savedBeer.getName());
    }
}