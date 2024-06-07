package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.controller.NotFountException;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.events.BeerOrderCreatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BeerOrderServiceJPA implements BeerOrderService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int DEFAULT_PAGE_SIZE_LIMIT = 1000;

    private final BeerOrderRepository repository;
    private final BeerOrderMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<BeerOrderDTO> orders(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        return repository.findAll(pageRequest).map(mapper::toBeerOrderDto);
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = DEFAULT_PAGE;
        else if (pageNumber > 0) pageNumber--;

        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        else if (pageSize > DEFAULT_PAGE_SIZE_LIMIT) pageSize = DEFAULT_PAGE_SIZE_LIMIT;

        return PageRequest.of(pageNumber, pageSize);
    }

    @Override
    public Optional<BeerOrderDTO> findById(UUID id) {
        return repository.findById(id).map(mapper::toBeerOrderDto);
    }

    @Override
    public BeerOrderDTO saveNewBeerOrder(BeerOrderDTO BeerOrderDTO) {
        BeerOrder beer = repository.save(mapper.toBeer(BeerOrderDTO));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        eventPublisher.publishEvent(new BeerOrderCreatedEvent(beer, authentication));
        return mapper.toBeerOrderDto(beer);
    }

    @Override
    public Optional<BeerOrderDTO> updateById(UUID id, BeerOrderDTO beerOrderDTO) {
        return repository.findById(id).map(beer -> {
            updateBeer(beer, beerOrderDTO);
            return mapper.toBeerOrderDto(repository.save(beer));
        });
    }

    private void updateBeer(BeerOrder beer, BeerOrderDTO beerOrderDTO) {
        beer.setCustomer(beerOrderDTO.getCustomer());
        beer.setBeerOrderShipment(beerOrderDTO.getBeerOrderShipment());
        beer.setOrderLines(beerOrderDTO.getOrderLines());
    }

    @Override
    public BeerOrderDTO deleteById(UUID id) {

        Optional<BeerOrder> found = repository.findById(id);
        repository.delete(found.orElseThrow(NotFountException::new));
        return found.map(mapper::toBeerOrderDto).orElseThrow();
    }

    @Override
    public Optional<BeerOrderDTO> patchById(UUID id, BeerOrderDTO BeerOrderDTO) {
        return repository.findById(id).map(beer -> {
            patchBeer(beer, BeerOrderDTO);
            return mapper.toBeerOrderDto(repository.save(beer));
        });
    }

    private void patchBeer(BeerOrder beer, BeerOrderDTO beerOrderDTO) {
        updateBeer(beer, beerOrderDTO);
    }
}
