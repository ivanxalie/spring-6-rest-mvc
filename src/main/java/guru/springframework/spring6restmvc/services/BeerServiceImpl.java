package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static java.time.LocalDateTime.now;

@Service
@Slf4j
public class BeerServiceImpl implements BeerService {
    private final Map<UUID, BeerDTO> beers = new HashMap<>();

    public BeerServiceImpl() {
        BeerDTO beerDTO1 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123456")
                .price(BigDecimal.valueOf(12.99))
                .quantityOnHand(122)
                .createdDate(now())
                .updateDate(now())
                .build();
        BeerDTO beerDTO2 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123456890")
                .price(BigDecimal.valueOf(11.99))
                .quantityOnHand(392)
                .createdDate(now())
                .updateDate(now())
                .build();
        BeerDTO beerDTO3 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("1234")
                .price(BigDecimal.valueOf(13.99))
                .quantityOnHand(144)
                .createdDate(now())
                .updateDate(now())
                .build();

        beers.put(beerDTO1.getId(), beerDTO1);
        beers.put(beerDTO2.getId(), beerDTO2);
        beers.put(beerDTO3.getId(), beerDTO3);

        log.debug("Beer1 id: {}", beerDTO1.getId());
        log.debug("Beer2 id: {}", beerDTO2.getId());
        log.debug("Beer3 id: {}", beerDTO3.getId());
    }

    @Override
    public Optional<BeerDTO> findBeerById(UUID id) {
        log.debug("Get Beer by Id - in service. Id: {}", id);
        return Optional.of(beers.get(id));
    }

    @Override
    public List<BeerDTO> beers() {
        return new ArrayList<>(beers.values());
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDto) {
        beerDto.setId(UUID.randomUUID());
        beerDto.setVersion(1);
        beerDto.setCreatedDate(now());
        beerDto.setUpdateDate(now());
        beers.put(beerDto.getId(), beerDto);
        log.debug("Successfully added new beer. Id: {}", beerDto.getId());
        return beerDto;
    }

    @Override
    public void updateById(UUID id, BeerDTO beerDto) {
        BeerDTO beerDTOToUpdate = beers.get(id);
        if (beerDTOToUpdate != null) {
            beerDTOToUpdate.setName(beerDto.getName());
            beerDTOToUpdate.setBeerStyle(beerDto.getBeerStyle());
            beerDTOToUpdate.setUpc(beerDto.getUpc());
            beerDTOToUpdate.setQuantityOnHand(beerDto.getQuantityOnHand());
            beerDTOToUpdate.setPrice(beerDto.getPrice());
            beerDTOToUpdate.setUpdateDate(now());
            beerDTOToUpdate.setVersion(beerDTOToUpdate.getVersion() + 1);
        }
    }

    @Override
    public void deleteById(UUID id) {
        beers.remove(id);
    }

    @Override
    public void patchBeerById(UUID id, BeerDTO beerDto) {
        BeerDTO saved = beers.get(id);

        if (saved != null) {
            if (StringUtils.hasText(beerDto.getName()))
                saved.setName(beerDto.getName());

            if (beerDto.getBeerStyle() != null)
                saved.setBeerStyle(beerDto.getBeerStyle());

            if (beerDto.getPrice() != null)
                saved.setPrice(beerDto.getPrice());

            if (beerDto.getQuantityOnHand() != null)
                saved.setQuantityOnHand(beerDto.getQuantityOnHand());

            if (StringUtils.hasText(beerDto.getUpc()))
                saved.setUpc(beerDto.getUpc());
        }
    }
}