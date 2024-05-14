package guru.springframework.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    @SneakyThrows
    public List<BeerCsvRecord> convertCSV(File file) {
        return new CsvToBeanBuilder<BeerCsvRecord>(new FileReader(file))
                .withType(BeerCsvRecord.class)
                .build()
                .parse();
    }
}
