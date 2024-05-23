package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void beers() {
        Page<BeerDTO> beers = controller.beers(null, null, false, 1, 25);

        assertThat(beers.getTotalElements()).isEqualTo(2413);
    }

    @Test
    void emptyBeers() {
        repository.deleteAll();
        Page<BeerDTO> beers = controller.beers(null, null, false, 1, 25);

        assertThat(beers.getTotalElements()).isEqualTo(0);
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
                                .with(httpBasic(username, password))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(PATH)
                        .with(httpBasic(username, password))
                        .queryParam("name", "%IPA%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(336)));
    }

    @Test
    void testListBeersByBeerStyle() throws Exception {
        mockMvc.perform(get(PATH)
                        .queryParam("beerStyle", BeerStyle.PORTER.name())
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(68)));
    }

    @Test
    void testListBeersByStyleAndNameShowInventoryTrue() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .with(httpBasic(username, password))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(310)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testListBeersByStyleAndNameShowInventoryFalse() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .with(httpBasic(username, password))
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "false")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(310)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void testListBeersByStyleAndName() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .with(httpBasic(username, password))
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(310)));
    }

    @Test
    void testListBeerByStyleAndNameShowInventoryTruePage1() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "50")
                                .with(httpBasic(username, password))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }
}