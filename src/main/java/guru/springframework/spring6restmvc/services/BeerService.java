package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Page<BeerDTO> beers(String name, BeerStyle style, Boolean showInventory, Integer pageNumber, Integer pageSize);

    Optional<BeerDTO> findById(UUID id);

    BeerDTO saveNewBeer(BeerDTO beerDto);

    Optional<BeerDTO> updateById(UUID id, BeerDTO beerDto);

    BeerDTO deleteById(UUID id);

    Optional<BeerDTO> patchById(UUID id, BeerDTO beerDto);
}