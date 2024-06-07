package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class BeerOrderCreatedEvent {
    private BeerOrder beerOrder;
    private Authentication authentication;
}
