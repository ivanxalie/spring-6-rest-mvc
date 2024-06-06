package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.BeerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerAuditRepository extends JpaRepository<BeerAudit, java.util.UUID> {
}