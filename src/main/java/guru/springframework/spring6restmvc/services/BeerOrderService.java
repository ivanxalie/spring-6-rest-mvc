package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {
    Page<BeerOrderDTO> orders(Integer pageNumber, Integer pageSize);

    Optional<BeerOrderDTO> findById(UUID id);

    BeerOrderDTO saveNewBeerOrder(BeerOrderDTO beerOrderDTO);

    Optional<BeerOrderDTO> updateById(UUID id, BeerOrderDTO beerOrderDTO);

    BeerOrderDTO deleteById(UUID id);

    Optional<BeerOrderDTO> patchById(UUID id, BeerOrderDTO beerOrderDTO);
}