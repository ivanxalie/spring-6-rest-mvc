package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Beer;
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
    private final Map<UUID, Beer> beers = new HashMap<>();

    public BeerServiceImpl() {
        Beer beer1 = Beer.builder()
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
        Beer beer2 = Beer.builder()
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
        Beer beer3 = Beer.builder()
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

        beers.put(beer1.getId(), beer1);
        beers.put(beer2.getId(), beer2);
        beers.put(beer3.getId(), beer3);

        log.debug("Beer1 id: {}", beer1.getId());
        log.debug("Beer2 id: {}", beer2.getId());
        log.debug("Beer3 id: {}", beer3.getId());
    }

    @Override
    public Optional<Beer> findBeerById(UUID id) {
        log.debug("Get Beer by Id - in service. Id: {}", id);
        return Optional.of(beers.get(id));
    }

    @Override
    public List<Beer> beers() {
        return new ArrayList<>(beers.values());
    }

    @Override
    public Beer saveNewBeer(Beer beer) {
        beer.setId(UUID.randomUUID());
        beer.setVersion(1);
        beer.setCreatedDate(now());
        beer.setUpdateDate(now());
        beers.put(beer.getId(), beer);
        log.debug("Successfully added new beer. Id: {}", beer.getId());
        return beer;
    }

    @Override
    public void updateById(UUID id, Beer beer) {
        Beer beerToUpdate = beers.get(id);
        if (beerToUpdate != null) {
            beerToUpdate.setName(beer.getName());
            beerToUpdate.setBeerStyle(beer.getBeerStyle());
            beerToUpdate.setUpc(beer.getUpc());
            beerToUpdate.setQuantityOnHand(beer.getQuantityOnHand());
            beerToUpdate.setPrice(beer.getPrice());
            beerToUpdate.setUpdateDate(now());
            beerToUpdate.setVersion(beerToUpdate.getVersion() + 1);
        }
    }

    @Override
    public void deleteById(UUID id) {
        beers.remove(id);
    }

    @Override
    public void patchBeerById(UUID id, Beer beer) {
        Beer saved = beers.get(id);

        if (saved != null) {
            if (StringUtils.hasText(beer.getName()))
                saved.setName(beer.getName());

            if (beer.getBeerStyle() != null)
                saved.setBeerStyle(beer.getBeerStyle());

            if (beer.getPrice() != null)
                saved.setPrice(beer.getPrice());

            if (beer.getQuantityOnHand() != null)
                saved.setQuantityOnHand(beer.getQuantityOnHand());

            if (StringUtils.hasText(beer.getUpc()))
                saved.setUpc(beer.getUpc());
        }
    }
}