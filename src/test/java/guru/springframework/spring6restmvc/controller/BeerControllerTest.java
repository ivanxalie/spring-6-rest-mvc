package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.*;

import static guru.springframework.spring6restmvc.controller.BeerController.PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    private BeerService serviceImpl;
    private Beer beer;

    @MockBean
    private BeerService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private ArgumentCaptor<Beer> beerArgumentCaptor;

    @BeforeEach
    void setUp() {
        serviceImpl = new BeerServiceImpl();
        beer = serviceImpl.beers().getFirst();
    }

    @Test
    void getBeerById() {
        given(service.findBeerById(beer.getId())).willReturn(Optional.of(beer));

        performAndExpect(
                get(PATH_ID, beer.getId()).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.id", is(beer.getId().toString())),
                jsonPath("$.name", is(beer.getName()))
        );
    }

    @SneakyThrows
    private void performAndExpect(RequestBuilder perform, ResultMatcher... expect) {
        ResultActions resultActions = mockMvc.perform(perform);
        for (ResultMatcher matcher : expect) resultActions.andExpect(matcher);
    }

    @Test
    void beers() {
        List<Beer> beers = serviceImpl.beers();
        given(service.beers()).willReturn(beers);

        performAndExpect(
                get(PATH).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.length()", is(beers.size()))
        );
    }

    @Test
    @SneakyThrows
    void saveNewBeer() {
        Beer beer = serviceImpl.beers().getFirst();
        beer.setVersion(null);
        beer.setId(null);

        given(service.saveNewBeer(any(Beer.class))).willReturn(serviceImpl.beers().get(1));

        performAndExpect(
                post(PATH).contentType(APPLICATION_JSON).content(mapper.writeValueAsBytes(beer)),

                status().isCreated(),
                header().exists("Location")
        );
    }

    @Test
    @SneakyThrows
    void updateBeer() {
        Beer beer = serviceImpl.beers().getFirst();

        performAndExpect(
                put(PATH_ID, beer.getId()).contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beer)),

                status().isNoContent()
        );

        verify(service).updateById(beer.getId(), beer);
    }

    @Test
    void deleteBeer() {
        UUID id = UUID.randomUUID();

        performAndExpect(
                delete(PATH_ID, id),
                status().isNoContent()
        );
        verify(service).deleteById(uuidArgumentCaptor.capture());

        assertThat(id).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void patchBeer() throws Exception {
        Beer beer = serviceImpl.beers().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("name", "New Beer");

        performAndExpect(
                patch(PATH_ID, beer.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beerMap)),

                status().isNoContent()
        );

        verify(service).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("name")).isEqualTo(beerArgumentCaptor.getValue().getName());
    }

    @Test
    void getBeerByIdNotFound() {

        given(service.findBeerById(any())).willReturn(Optional.empty());
        performAndExpect(
                get(PATH_ID, UUID.randomUUID()),

                status().isNotFound()
        );
    }
}