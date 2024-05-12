package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
class BeerControllerIntegrationTest {
    @Autowired
    private BeerController controller;

    @Autowired
    private BeerRepository repository;

    @Autowired
    private BeerMapper mapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void beers() {
        List<BeerDTO> beers = controller.beers();

        assertThat(beers.size()).isEqualTo(3);
    }

    @Test
    void emptyBeers() {
        repository.deleteAll();
        List<BeerDTO> beers = controller.beers();

        assertThat(beers.size()).isEqualTo(0);
    }

    @Test
    void findById() {
        Beer beer = repository.findAll().getFirst();
        assertThat(beer).isNotNull();

        BeerDTO beerDTO = controller.findBeerById(beer.getId());
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getId()).isEqualTo(beer.getId());
    }

    @Test
    void findByIdIsNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.findBeerById(UUID.randomUUID()));
    }

    @Test
    void addNewBeer() {
        long countBefore = repository.count();

        ResponseEntity<BeerDTO> entity = controller.addNewBeer(BeerDTO.builder()
                .name("Buh-lo")
                .upc("12312")
                .beerStyle(BeerStyle.WHEAT)
                .price(BigDecimal.ONE)
                .build());

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(entity.getHeaders().getLocation()).isNotNull();

        BeerDTO beerDTO = entity.getBody();
        assertThat(beerDTO).isNotNull();

        assertThat(repository.count()).isEqualTo(countBefore + 1);

        Optional<Beer> beer = repository.findById(beerDTO.getId());
        assertThat(beer).isNotEmpty();
        assertThat(beer.get().getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void updateById() {
        Beer beer = repository.findAll().getFirst();

        BeerStyle newStyle = BeerStyle.WHEAT;
        String newName = "Good Beer";
        BigDecimal newPrice = BigDecimal.valueOf(-500.5);

        BeerDTO beerDTO = mapper.toBeerDto(beer);
        beerDTO.setBeerStyle(newStyle);
        beerDTO.setName(newName);
        beerDTO.setPrice(newPrice);

        controller.updateById(beer.getId(), beerDTO);

        beer = repository.findById(beer.getId()).orElseThrow(NotFountException::new);

        assertThat(beer).isNotNull();
        assertThat(beer.getBeerStyle()).isEqualTo(newStyle);
        assertThat(beer.getName()).isEqualTo(newName);
        assertThat(beer.getPrice()).isEqualTo(newPrice);
    }

    @Test
    void updateByIdNotFound() {
        repository.deleteAll();

        assertThrows(NotFountException.class,
                () -> controller.updateById(UUID.randomUUID(), null));
    }

    @Test
    void deleteById() {
        long beforeCount = repository.count();

        Beer first = repository.findAll().getFirst();

        ResponseEntity<?> response = controller.deleteById(first.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.count()).isEqualTo(beforeCount - 1);

        assertThat(repository.findById(first.getId())).isEmpty();
    }

    @Test
    void deleteByIdNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.deleteById(UUID.randomUUID()));
    }

    @Test
    void testPatchBeerBadName() throws Exception {
        Beer beer = repository.findAll().getFirst();

        Map<String, Object> beerMap = Collections.singletonMap("name", "New Name".repeat(500));

        mockMvc.perform(
                        patch(BeerController.PATH_ID, beer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(beerMap))
                )
                .andExpect(status().isBadRequest());
    }
}