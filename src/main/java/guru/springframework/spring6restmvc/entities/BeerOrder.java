package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Builder
@Data
@Entity
public class BeerOrder {
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

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder")
    @Builder.Default
    private Set<BeerOrderLine> orderLines = new HashSet<>();

    @OneToOne
    private BeerOrderShipment beerOrderShipment;

    public BeerOrder(UUID id, Integer version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef,
                     Customer customer, Set<BeerOrderLine> orderLines, BeerOrderShipment beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customerRef = customerRef;
        setCustomer(customer);
        this.orderLines = orderLines;
        this.beerOrderShipment = beerOrderShipment;
    }

    public boolean isNew() {
        return id == null;
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            customer.getOrders().add(this);
        }
    }
}