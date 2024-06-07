package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.model.BeerOrderLineDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderLineMapper {
    BeerOrderLineDTO toBeerOrderLineDTO(BeerOrderLine beerOrderLine);

    BeerOrderLine toBeerOrderLine(BeerOrderLineDTO beerOrderLineDTO);
}
