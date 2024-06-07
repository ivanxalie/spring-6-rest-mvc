package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "Quantity on hand must be greater that 0")
    private Integer orderQuantity;
    private Integer quantityAllocated;
    private BeerDTO beer;
}