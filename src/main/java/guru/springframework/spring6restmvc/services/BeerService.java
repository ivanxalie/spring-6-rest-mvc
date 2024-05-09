package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Beer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Optional<Beer> findBeerById(UUID id);

    List<Beer> beers();

    Beer saveNewBeer(Beer beer);

    void updateById(UUID id, Beer beer);

    void deleteById(UUID id);

    void patchBeerById(UUID id, Beer beer);
}