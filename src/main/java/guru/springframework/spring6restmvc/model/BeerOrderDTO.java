package guru.springframework.spring6restmvc.model;

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
    private CustomerDTO customer;

    private Set<BeerOrderLineDTO> orderLines;
    private BeerOrderShipmentDTO beerOrderShipment;
}
