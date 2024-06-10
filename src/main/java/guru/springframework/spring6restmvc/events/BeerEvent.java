package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.entities.Beer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Builder
@Getter
@Setter
public class BeerEvent {
    private Beer beer;
    private Authentication authentication;

    public BeerEvent(Beer beer, Authentication authentication) {
        this.beer = beer;
        this.authentication = authentication;
    }
}
