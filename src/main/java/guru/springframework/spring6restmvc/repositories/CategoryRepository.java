package guru.springframework.spring6restmvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import guru.springframework.spring6restmvc.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, java.util.UUID> {
}