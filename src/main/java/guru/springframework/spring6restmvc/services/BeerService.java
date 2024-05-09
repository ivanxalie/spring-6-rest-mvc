package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Optional<BeerDTO> findBeerById(UUID id);

    List<BeerDTO> beers();

    BeerDTO saveNewBeer(BeerDTO beerDto);

    void updateById(UUID id, BeerDTO beerDto);

    void deleteById(UUID id);

    void patchBeerById(UUID id, BeerDTO beerDto);
}