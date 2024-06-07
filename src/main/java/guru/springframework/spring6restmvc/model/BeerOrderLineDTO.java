package guru.springframework.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
public class BeerOrderLineDTO {
    private UUID id;
    private Integer version;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;
    private Integer orderQuantity;
    private Integer quantityAllocated;
    private BeerOrderDTO beerOrder;
    private BeerDTO beer;
}