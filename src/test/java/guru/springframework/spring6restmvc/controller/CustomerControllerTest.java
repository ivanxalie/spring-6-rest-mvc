package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
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
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        serviceImpl = new CustomerServiceImpl();
    }

    @Test
    void customers() {
        List<Customer> customers = serviceImpl.customers();
        given(customerService.customers()).willReturn(customers);

        performAndExpect(
                get(PATH).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.length()", is(customers.size()))
        );
    }

    @Test
    void getCustomerById() {
        Customer customer = serviceImpl.customers().getFirst();

        given(customerService.findById(customer.getId())).willReturn(Optional.of(customer));

        performAndExpect(
                get(PATH_ID, customer.getId()).accept(APPLICATION_JSON),

                status().isOk(),
                content().contentType(APPLICATION_JSON),
                jsonPath("$.id", is(customer.getId().toString())),
                jsonPath("$.name", is(customer.getName()))
        );
    }

    @SneakyThrows
    private void performAndExpect(RequestBuilder perform, ResultMatcher... expect) {
        ResultActions resultActions = mockMvc.perform(perform);
        for (ResultMatcher matcher : expect) resultActions.andExpect(matcher);
    }

    @Test
    void saveNewCustomer() throws Exception {
        Customer customer = serviceImpl.customers().getFirst();

        given(customerService.saveNewCustomer(any())).willReturn(customer);

        performAndExpect(
                post(PATH).contentType(APPLICATION_JSON).content(mapper.writeValueAsBytes(customer)),

                status().isCreated(),
                jsonPath("$.version", is(1)),
                header().exists("Location")
        );
    }

    @Test
    void updateCustomer() throws Exception {
        Customer customer = serviceImpl.customers().getFirst();

        performAndExpect(
                put(PATH_ID, customer.getId()).contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(customer)),

                status().isNoContent()
        );

        verify(customerService).updateById(customer.getId(), customer);
    }

    @Test
    void deleteCustomer() {
        UUID id = UUID.randomUUID();

        performAndExpect(
                delete(PATH_ID, id),
                status().isNoContent()
        );

        verify(customerService).deleteById(id);
    }

    @Test
    void patchCustomer() throws JsonProcessingException {
        Customer customer = serviceImpl.customers().getFirst();

        Map<String, Object> patch = Collections.singletonMap("name", "Gay");

        performAndExpect(
                patch(PATH_ID, customer.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(patch)),

                status().isNoContent()
        );

        verify(customerService).patchById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(patch.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
    }

    @Test
    void getByIdIfNotFound() {
        given(customerService.findById(any())).willReturn(Optional.empty());

        performAndExpect(
                get(PATH_ID, UUID.randomUUID()),

                status().isNotFound()
        );
    }
}