package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.controller.NotFountException;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
    private final BeerRepository repository;
    private final BeerMapper mapper;

    @Override
    public List<BeerDTO> beers() {
        return repository.findAll().stream().map(mapper::toBeerDto).toList();
    }

    @Override
    public Optional<BeerDTO> findById(UUID id) {
        return repository.findById(id).map(mapper::toBeerDto);
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDto) {
        return mapper.toBeerDto(repository.save(mapper.toBeer(beerDto)));
    }

    @Override
    public Optional<BeerDTO> updateById(UUID id, BeerDTO beerDto) {
        return repository.findById(id).map(beer -> {
            updateBeer(beer, beerDto);
            return mapper.toBeerDto(repository.save(beer));
        });
    }

    private void updateBeer(Beer beer, BeerDTO beerDto) {
        beer.setName(beerDto.getName());
        beer.setBeerStyle(beerDto.getBeerStyle());
        beer.setUpc(beerDto.getUpc());
        beer.setPrice(beerDto.getPrice());
        beer.setQuantityOnHand(beerDto.getQuantityOnHand());
    }

    @Override
    public BeerDTO deleteById(UUID id) {
        Optional<Beer> found = repository.findById(id);
        repository.delete(found.orElseThrow(NotFountException::new));
        return found.map(mapper::toBeerDto).orElseThrow();
    }

    @Override
    public Optional<BeerDTO> patchById(UUID id, BeerDTO beerDto) {
        return repository.findById(id).map(beer -> {
            patchBeer(beer, beerDto);
            return mapper.toBeerDto(repository.save(beer));
        });
    }

    private void patchBeer(Beer beer, BeerDTO beerDto) {
        if (StringUtils.hasText(beerDto.getName()))
            beer.setName(beerDto.getName());

        if (beerDto.getBeerStyle() != null)
            beer.setBeerStyle(beerDto.getBeerStyle());

        if (beerDto.getPrice() != null)
            beer.setPrice(beerDto.getPrice());

        if (beerDto.getQuantityOnHand() != null)
            beer.setQuantityOnHand(beerDto.getQuantityOnHand());

        if (StringUtils.hasText(beerDto.getUpc()))
            beer.setUpc(beerDto.getUpc());
    }
}
