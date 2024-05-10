package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class BootstrapData {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        initBeers();
        initCustomers();
    }

    private void initBeers() {
        if (beerRepository.count() == 0) {
            Beer beerDTO1 = Beer.builder()
                    .name("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123456")
                    .price(BigDecimal.valueOf(12.99))
                    .quantityOnHand(122)
                    .createdDate(now())
                    .updateDate(now())
                    .build();
            Beer beerDTO2 = Beer.builder()
                    .name("Crank")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123456890")
                    .price(BigDecimal.valueOf(11.99))
                    .quantityOnHand(392)
                    .createdDate(now())
                    .updateDate(now())
                    .build();
            Beer beerDTO3 = Beer.builder()
                    .name("Sunshine City")
                    .beerStyle(BeerStyle.IPA)
                    .upc("1234")
                    .price(BigDecimal.valueOf(13.99))
                    .quantityOnHand(144)
                    .createdDate(now())
                    .updateDate(now())
                    .build();
            beerRepository.saveAll(
                    List.of(
                            beerDTO1,
                            beerDTO2,
                            beerDTO3
                    )
            );
        }
    }

    private void initCustomers() {
        if (customerRepository.count() == 0) {
            Customer customerDTO1 = createCustomer("Alex");
            Customer customerDTO2 = createCustomer("Alice");
            Customer customerDTO3 = createCustomer("Roberto");

            customerRepository.saveAll(
                    List.of(
                            customerDTO1,
                            customerDTO2,
                            customerDTO3
                    )
            );
        }
    }

    private Customer createCustomer(String name) {
        return Customer.builder()
                .name(name)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
    }
}
