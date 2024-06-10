package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.controller.NotFountException;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.events.BeerDeletedEvent;
import guru.springframework.spring6restmvc.events.BeerPatchedEvent;
import guru.springframework.spring6restmvc.events.BeerUpdatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int DEFAULT_PAGE_SIZE_LIMIT = 1000;

    private final BeerRepository repository;
    private final BeerMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @Override
    public Page<BeerDTO> beers(String name, BeerStyle style, Boolean showInventory,
                               Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);


        Page<Beer> beers = Page.empty();

        if (!StringUtils.hasText(name) && style == null)
            beers = repository.findAll(pageRequest);
        else {
            if (StringUtils.hasText(name) && style != null) {
                beers = repository.findAllByNameIsLikeIgnoreCaseAndBeerStyle(wrapName(name), style, pageRequest);
            } else if (StringUtils.hasText(name))
                beers = repository.findAllByNameIsLikeIgnoreCase(wrapName(name), pageRequest);
            else if (style != null)
                beers = repository.findAllByBeerStyle(style, pageRequest);
        }

        if (Boolean.FALSE.equals(showInventory))
            beers.stream().forEach(beer -> beer.setQuantityOnHand(null));

        return beers
                .map(mapper::toBeerDto);
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = DEFAULT_PAGE;
        else if (pageNumber > 0) pageNumber--;

        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        else if (pageSize > DEFAULT_PAGE_SIZE_LIMIT) pageSize = DEFAULT_PAGE_SIZE_LIMIT;

        Sort sort = Sort.by(Sort.Order.asc("name"));

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private String wrapName(String name) {
        return "%" + name + "%";
    }

    @Override
    public Optional<BeerDTO> findById(UUID id) {
        return repository.findById(id).map(mapper::toBeerDto);
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDto) {
        Beer beer = repository.save(mapper.toBeer(beerDto));
        BeerDTO result = mapper.toBeerDto(beer);

        eventPublisher.publishEvent(new BeerCreatedEvent(beer, authentication));

        return result;
    }

    @Override
    public Optional<BeerDTO> updateById(UUID id, BeerDTO beerDto) {
        return repository.findById(id).map(beer -> {
            updateBeer(beer, beerDto);
            eventPublisher.publishEvent(new BeerUpdatedEvent(beer, authentication));
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
        eventPublisher.publishEvent(new BeerDeletedEvent(id, authentication));
        return found.map(mapper::toBeerDto).orElseThrow();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "beerCache", key = "#beerId"),
            @CacheEvict(cacheNames = "beerListCache")
    })
    public Optional<BeerDTO> patchById(UUID id, BeerDTO beerDto) {
        return repository.findById(id).map(beer -> {
            patchBeer(beer, beerDto);
            eventPublisher.publishEvent(new BeerPatchedEvent(beer, authentication));
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
