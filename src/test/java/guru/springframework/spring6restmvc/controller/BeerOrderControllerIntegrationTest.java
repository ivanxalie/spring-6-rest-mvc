package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerControllerTest.processor;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
@RecordApplicationEvents
class BeerOrderControllerIntegrationTest {
    @Autowired
    ApplicationEvents applicationEvents;
    @Autowired
    private BeerOrderController controller;
    @Autowired
    private BeerOrderRepository repository;
    @Autowired
    private BeerOrderMapper mapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void orders() {
        Page<BeerOrderDTO> beers = controller.orders(1, 25);

        assertThat(beers.getTotalElements()).isEqualTo(0);
    }

    @Test
    void emptyOrders() {
        repository.deleteAll();
        Page<BeerOrderDTO> beers = controller.orders(1, 25);

        assertThat(beers.getTotalElements()).isEqualTo(0);
    }

    @Test
    void findById() {
        BeerOrder beer = repository.findAll().getFirst();
        assertThat(beer).isNotNull();

        BeerOrderDTO BeerOrderDTO = controller.findBeerById(beer.getId());
        assertThat(BeerOrderDTO).isNotNull();
        assertThat(BeerOrderDTO.getId()).isEqualTo(beer.getId());
    }

    @Test
    void findByIdIsNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.findBeerById(UUID.randomUUID()));
    }

    @Test
    void addNewBeer() {
        long countBefore = repository.count();

        BeerOrderDTO beerToSave = createTestBeer();
        ResponseEntity<BeerOrderDTO> entity = controller.addNewBeer(beerToSave);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(entity.getHeaders().getLocation()).isNotNull();

        BeerOrderDTO BeerOrderDTO = entity.getBody();
        assertThat(BeerOrderDTO).isNotNull();

        assertThat(repository.count()).isEqualTo(countBefore + 1);

        Optional<BeerOrder> beer = repository.findById(BeerOrderDTO.getId());
        assertThat(beer).isNotEmpty();
        assertThat(beer.get().getId()).isEqualTo(BeerOrderDTO.getId());
    }

    private BeerOrderDTO createTestBeer() {
        return BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void updateById() {
        BeerOrder beer = repository.findAll().getFirst();

        BeerOrderDTO BeerOrderDTO = mapper.toBeerOrderDto(beer);
        BeerOrderDTO.setVersion(5);
        BeerOrderDTO.setCustomerRef("???");

        controller.updateById(beer.getId(), BeerOrderDTO);

        beer = repository.findById(beer.getId()).orElseThrow(NotFountException::new);

        assertThat(beer).isNotNull();
        assertThat(beer.getVersion()).isEqualTo(5);
        assertThat(beer.getCustomerRef()).isEqualTo("???");
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

        BeerOrder first = repository.findAll().getFirst();

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
        BeerOrder beer = repository.findAll().getFirst();

        Map<String, Object> beerMap = Collections.singletonMap("name", "New Name".repeat(500));

        mockMvc.perform(
                        patch(BeerOrderController.PATH_ID, beer.getId())
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(beerMap))
                                .with(processor)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListOrdersByName() throws Exception {
        mockMvc.perform(get(PATH)
                        .with(processor)
                        .queryParam("name", "%IPA%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(336)));
    }

    @Test
    void testListOrdersByBeerStyle() throws Exception {
        mockMvc.perform(get(PATH)
                        .queryParam("beerStyle", BeerStyle.PORTER.name())
                        .with(processor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(68)));
    }

    @Test
    void testListOrdersByStyleAndNameShowInventoryTrue() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .with(processor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(310)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testListOrdersByStyleAndNameShowInventoryFalse() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .with(processor)
                                .queryParam("name", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "false")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(310)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void testListOrdersByStyleAndName() throws Exception {
        mockMvc.perform(
                        get(PATH)
                                .with(processor)
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
                                .with(processor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testAddBeerMVC() throws Exception {
        BeerOrderDTO testBeer = createTestBeer();

        mockMvc.perform(
                        post(PATH)
                                .with(processor)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBeer))

                )
                .andExpect(status().isCreated());

        assertThat(applicationEvents.stream(BeerCreatedEvent.class).count()).isEqualTo(1);
    }
}