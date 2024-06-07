package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerControllerTest.processor;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
@RecordApplicationEvents
class BeerOrderControllerIntegrationTest {
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
    void findByIdIsNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.findBeerById(UUID.randomUUID()));
    }

    private BeerOrderDTO createTestBeer() {
        return BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void updateByIdNotFound() {
        repository.deleteAll();

        assertThrows(NotFountException.class,
                () -> controller.updateById(UUID.randomUUID(), null));
    }

    @Test
    void deleteByIdNotFound() {
        repository.deleteAll();
        assertThrows(NotFountException.class, () -> controller.deleteById(UUID.randomUUID()));
    }

    @Test
    void testListOrdersByBeerStyle() throws Exception {
        mockMvc.perform(get(PATH)
                        .with(processor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }
}