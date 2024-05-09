package guru.springframework.spring6restmvc.entities;

import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Beer {
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false, length = 36, columnDefinition = "varchar")
    private UUID id;
    @Version
    private Integer version;
    private String name;
    private BeerStyle beerStyle;
    private String upc;
    private Integer quantityOnHand;
    private BigDecimal price;
    @CreatedDate
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
