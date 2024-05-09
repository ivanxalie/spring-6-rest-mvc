package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {
    @GeneratedValue
    @Id
    @Column(nullable = false, updatable = false, length = 36, columnDefinition = "varchar")
    private UUID id;
    private String name;
    @Version
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
