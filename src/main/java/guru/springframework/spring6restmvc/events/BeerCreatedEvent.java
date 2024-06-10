package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.entities.Beer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;


@Getter
@Setter
public class BeerCreatedEvent extends BeerEvent {
    public BeerCreatedEvent(Beer beer, Authentication authentication) {
        super(beer, authentication);
    }
}
