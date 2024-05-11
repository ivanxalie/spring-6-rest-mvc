package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
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

import static guru.springframework.spring6restmvc.controller.CustomerController.PATH;
import static guru.springframework.spring6restmvc.controller.CustomerController.PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    private CustomerService serviceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        serviceImpl = new CustomerServiceImpl();
    }

    @Test
    void customers() {
        List<CustomerDTO> customerDTOS = serviceImpl.customers();
        given(customerService.customers()).willReturn(customerDTOS);

        performAndExpect(
                get(PATH).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.length()", is(customerDTOS.size()))
        );
    }

    @Test
    void getCustomerById() {
        CustomerDTO customerDTO = serviceImpl.customers().getFirst();

        given(customerService.findById(customerDTO.getId())).willReturn(Optional.of(customerDTO));

        performAndExpect(
                get(PATH_ID, customerDTO.getId()).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.id", is(customerDTO.getId().toString())),
                jsonPath("$.name", is(customerDTO.getName()))
        );
    }

    @SneakyThrows
    private void performAndExpect(RequestBuilder perform, ResultMatcher... expect) {
        ResultActions resultActions = mockMvc.perform(perform);
        for (ResultMatcher matcher : expect) resultActions.andExpect(matcher);
    }

    @Test
    void saveNewCustomer() throws Exception {
        CustomerDTO customerDTO = serviceImpl.customers().getFirst();

        given(customerService.saveNewCustomer(any())).willReturn(customerDTO);

        performAndExpect(
                post(PATH).contentType(APPLICATION_JSON).content(mapper.writeValueAsBytes(customerDTO)),

                status().isCreated(),
                jsonPath("$.version", is(1)),
                header().exists("Location")
        );
    }

    @Test
    void updateCustomer() throws Exception {
        CustomerDTO customerDTO = serviceImpl.customers().getFirst();

        performAndExpect(
                put(PATH_ID, customerDTO.getId()).contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(customerDTO)),

                status().isNoContent()
        );

        verify(customerService).updateById(customerDTO.getId(), customerDTO);
    }

    @Test
    void deleteCustomer() {
        UUID id = UUID.randomUUID();

        given(customerService.deleteById(any())).willReturn(Optional.ofNullable(serviceImpl.customers().getFirst()));

        performAndExpect(
                delete(PATH_ID, id),
                status().isNoContent()
        );

        verify(customerService).deleteById(id);
    }

    @Test
    void patchCustomer() throws JsonProcessingException {
        CustomerDTO customerDTO = serviceImpl.customers().getFirst();

        Map<String, Object> patch = Collections.singletonMap("name", "Gay");

        performAndExpect(
                patch(PATH_ID, customerDTO.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(patch)),

                status().isNoContent()
        );

        verify(customerService).patchById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(customerDTO.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(patch.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
    }

    @Test
    void findByIdIfNotFound() {
        given(customerService.findById(any())).willReturn(Optional.empty());

        performAndExpect(
                get(PATH_ID, UUID.randomUUID()),

                status().isNotFound()
        );
    }
}