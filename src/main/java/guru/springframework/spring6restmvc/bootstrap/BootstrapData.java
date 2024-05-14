package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class BootstrapData {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;
    private Resource resource;

    @Autowired
    public void setResource(@Value("classpath:csvdata/beers.csv") Resource resource) {
        this.resource = resource;
    }

    @PostConstruct
    @Transactional
    public void init() {
        initBeers();
        loadCsvData();
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

    @SneakyThrows
    private void loadCsvData() {
        if (beerRepository.count() < 10) {
            beerCsvService
                    .convertCSV(resource.getFile())
                    .forEach(record -> {
                        BeerStyle style = defineStyle(record.getStyle());

                        beerRepository.save(Beer.builder()
                                .name(StringUtils.abbreviate(record.getBeer(), 50))
                                .beerStyle(style)
                                .price(BigDecimal.TEN)
                                .upc(record.getRow().toString())
                                .quantityOnHand(record.getCount())
                                .build());
                    });
        }
    }

    private BeerStyle defineStyle(String style) {
        return switch (style) {
            case "American Pale Lager" -> BeerStyle.LAGER;
            case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                    BeerStyle.ALE;
            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
            case "American Porter" -> BeerStyle.PORTER;
            case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
            case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
            case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
            case "English Pale Ale" -> BeerStyle.PALE_ALE;
            default -> BeerStyle.PILSNER;
        };
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
