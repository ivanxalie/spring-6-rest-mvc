package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.entities.BeerOrderAudit;
import guru.springframework.spring6restmvc.events.BeerOrderCreatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.repositories.BeerOrderAuditRepository;
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
public class BeerOrderCreatedListener {
    private final BeerOrderAuditRepository repository;
    private final BeerOrderMapper mapper;

    @EventListener
    @Async
    public void listen(BeerOrderCreatedEvent event) {
        BeerOrderAudit audit = mapper.toBeerOrderAudit(event.getBeerOrder());
        audit.setAuditEventType("BEER_ORDER_CREATED");

        Authentication authentication = event.getAuthentication();
        if (authentication != null && StringUtils.hasText(authentication.getName()))
            audit.setPrincipalName(authentication.getName());

        BeerOrderAudit saved = repository.save(audit);
        log.info("Saved audit event: {}", saved);

    }
}
