package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.model.BeerOrderShipmentDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderShipmentMapper {
    BeerOrderShipmentDTO toBeerOrderShipmentDto(BeerOrderShipment beerOrderShipment);

    BeerOrderShipment toShipment(BeerOrderShipmentDTO beerOrderShipmentDTO);
}
