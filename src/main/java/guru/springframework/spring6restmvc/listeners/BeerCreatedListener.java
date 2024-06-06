package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.entities.BeerAudit;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeerCreatedListener {
    private final BeerAuditRepository repository;
    private final BeerMapper mapper;

    @EventListener
    @Async
    public void listen(BeerCreatedEvent event) {
        BeerAudit audit = mapper.toBeerAudit(event.getBeer());
        audit.setAuditEventType("BEER_CREATED");

        Authentication authentication = event.getAuthentication();
        if (authentication != null && StringUtils.hasText(authentication.getName()))
            audit.setPrincipalName(authentication.getName());

        BeerAudit saved = repository.save(audit);
        log.info("Saved audit event: {}", saved);

    }
}
