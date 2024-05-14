package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceImplTest {
    private final BeerCsvService service = new BeerCsvServiceImpl();

    @Test
    @SneakyThrows
    void convertCSV() {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

        List<BeerCsvRecord> records = service.convertCSV(file);

        System.out.println(records.size());

        assertThat(records.size()).isGreaterThan(0);
    }
}