package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class BeerOrderLine {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, updatable = false, length = 36, columnDefinition = "varchar(36)")
    private UUID id;

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Min(value = 1, message = "Quantity on hand must be greater that 0")
    private Integer orderQuantity = 1;

    private Integer quantityAllocated = 0;

    @ManyToOne
    private BeerOrder beerOrder;

    @ManyToOne
    private Beer beer;

    public boolean isNew() {
        return id == null;
    }
}