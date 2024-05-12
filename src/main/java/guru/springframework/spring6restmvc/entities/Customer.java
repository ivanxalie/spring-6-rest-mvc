package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @Column(nullable = false, updatable = false, length = 36, columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    private String name;
    @Version
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
