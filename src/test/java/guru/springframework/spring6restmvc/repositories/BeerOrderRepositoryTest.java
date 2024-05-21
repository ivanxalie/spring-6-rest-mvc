package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.entities.Customer;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class BeerOrderRepositoryTest {
    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeerRepository beerRepository;

    private Customer customer;
    private Beer beer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().getFirst();
        beer = beerRepository.findAll().getFirst();
    }

    @Test
    @Transactional
    void testBeerOrders() {
        BeerOrder order = BeerOrder.builder()
                .customerRef("Test order")
                .customer(customer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber(UUID.randomUUID().toString())
                        .build())
                .build();

        BeerOrder saved = beerOrderRepository.save(order);

        assertThat(saved).isNotNull();
    }
}