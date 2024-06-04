package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.configs.SpringSecurityConfig;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@Import(SpringSecurityConfig.class)
class BeerControllerTest {

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor processor =
            jwt().jwt(jwt -> jwt.claims(claims -> {
                        claims.put("scope", "message-read");
                        claims.put("scope", "message-write");
                    })
                    .subject("messaging-client")
                    .notBefore(Instant.now().minusSeconds(5L)));

    private BeerService serviceImpl;
    private BeerDTO beerDto;
    @MockBean
    private BeerService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    private ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @BeforeEach
    void setUp() {
        serviceImpl = new BeerServiceImpl();
        beerDto = serviceImpl.beers(null, null, false, 1, 25).getContent()
                .getFirst();
    }

    @Test
    void findBeerById() {
        given(service.findById(beerDto.getId())).willReturn(Optional.of(beerDto));

        performAndExpect(
                get(PATH_ID, beerDto.getId())
                        .accept(APPLICATION_JSON)
                        .with(processor),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.id", is(beerDto.getId().toString())),
                jsonPath("$.name", is(beerDto.getName()))
        );
    }

    @Test
    void findBeerByIdSecurityViolation() {
        given(service.findById(beerDto.getId())).willReturn(Optional.of(beerDto));

        performAndExpect(
                get(PATH_ID, beerDto.getId())
                        .accept(APPLICATION_JSON),

                status().isUnauthorized()
        );
    }

    @SneakyThrows
    private MvcResult performAndExpect(RequestBuilder perform, ResultMatcher... expect) {
        ResultActions resultActions = mockMvc.perform(perform);
        for (ResultMatcher matcher : expect) resultActions.andExpect(matcher);
        return resultActions.andReturn();
    }

    @Test
    void beers() {
        Page<BeerDTO> beerDTOS = serviceImpl.beers(null, null, null, 1, 25);
        given(service.beers(any(), any(), any(), any(), any())).willReturn(beerDTOS);

        performAndExpect(
                get(PATH).accept(APPLICATION_JSON)
                        .with(processor),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.page.size", is(3))
        );
    }

    @Test
    @SneakyThrows
    void saveNewBeer() {
        BeerDTO beerDto = serviceImpl.beers(null, null, false, 1, 25)
                .getContent().getFirst();
        beerDto.setVersion(null);
        beerDto.setId(null);

        given(service.saveNewBeer(any(BeerDTO.class))).willReturn(serviceImpl.beers(null, null,
                false, 1, 25).getContent().getFirst());

        performAndExpect(
                post(PATH).contentType(APPLICATION_JSON).content(mapper.writeValueAsBytes(beerDto))
                        .with(processor),

                status().isCreated(),
                header().exists("Location")
        );
    }

    @Test
    @SneakyThrows
    void updateBeer() {
        BeerDTO beerDto = serviceImpl.beers(null, null, false, 1, 25)
                .getContent().getFirst();

        given(service.updateById(any(), any())).willReturn(Optional.of(beerDto));

        performAndExpect(
                put(PATH_ID, beerDto.getId()).contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beerDto)).with(processor),

                status().isNoContent()
        );

        verify(service).updateById(beerDto.getId(), beerDto);
    }

    @Test
    @SneakyThrows
    void updateBeerBlankName() {
        BeerDTO beerDto = serviceImpl.beers(null, null, false, 1, 25)
                .getContent().getFirst();
        beerDto.setName("");

        given(service.updateById(any(), any())).willReturn(Optional.of(beerDto));

        performAndExpect(
                put(PATH_ID, beerDto.getId()).contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beerDto)).with(processor),

                status().isBadRequest(),
                jsonPath("$.length()", is(1))
        );
    }

    @Test
    void deleteBeer() {
        UUID id = UUID.randomUUID();

        performAndExpect(
                delete(PATH_ID, id).with(processor),
                status().isNoContent()
        );
        verify(service).deleteById(uuidArgumentCaptor.capture());

        assertThat(id).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void patchBeer() throws Exception {
        BeerDTO beerDto = serviceImpl.beers(null, null, false, 1, 25)
                .getContent().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("name", "New Beer");
        beerMap.put("beerStyle", BeerStyle.WHEAT);
        beerMap.put("upc", 123123);
        beerMap.put("price", BigDecimal.valueOf(100));

        performAndExpect(
                patch(PATH_ID, beerDto.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beerMap))
                        .with(processor),

                status().isNoContent()
        );

        verify(service).patchById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beerDto.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("name")).isEqualTo(beerArgumentCaptor.getValue().getName());
    }

    @Test
    void findBeerByIdNotFound() {

        given(service.findById(any())).willReturn(Optional.empty());
        performAndExpect(
                get(PATH_ID, UUID.randomUUID()).with(processor),

                status().isNotFound()
        );
    }

    @Test
    void addBeerNullProperties() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder()
                .name("")
                .upc("")
                .build();

        given(service.saveNewBeer(any())).willReturn(beerDTO);

        MvcResult result = performAndExpect(
                post(PATH)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(beerDTO))
                        .with(processor),

                status().isBadRequest(),
                jsonPath("$.length()", is(4))
        );
        System.out.println(result.getResponse().getContentAsString());
    }
}