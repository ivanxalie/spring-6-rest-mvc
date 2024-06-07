package guru.springframework.spring6restmvc.model;

import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.entities.Customer;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class BeerOrderDTO {
    private UUID id;
    private Integer version;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;
    private String customerRef;
    private Customer customer;

    private Set<BeerOrderLine> orderLines;
    private BeerOrderShipment beerOrderShipment;
}
