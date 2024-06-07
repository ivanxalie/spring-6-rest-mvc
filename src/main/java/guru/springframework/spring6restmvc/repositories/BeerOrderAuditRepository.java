package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.BeerOrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderAuditRepository extends JpaRepository<BeerOrderAudit, java.util.UUID> {
}