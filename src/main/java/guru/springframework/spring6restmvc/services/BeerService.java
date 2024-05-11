package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> beers();

    Optional<BeerDTO> findById(UUID id);

    BeerDTO saveNewBeer(BeerDTO beerDto);

    Optional<BeerDTO> updateById(UUID id, BeerDTO beerDto);

    BeerDTO deleteById(UUID id);

    Optional<BeerDTO> patchById(UUID id, BeerDTO beerDto);
}