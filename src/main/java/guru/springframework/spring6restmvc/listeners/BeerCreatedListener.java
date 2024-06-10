package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.entities.BeerAudit;
import guru.springframework.spring6restmvc.events.*;
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
    public void listenCreate(BeerCreatedEvent event) {
        listenCommon(event, "BEER_CREATED");
    }

    private void listenCommon(BeerEvent event, String eventType) {
        BeerAudit audit = mapper.toBeerAudit(event.getBeer());
        audit.setAuditEventType(eventType);

        fillAudit(event.getAuthentication(), audit);

        BeerAudit saved = repository.save(audit);
        log(saved);
    }

    private void fillAudit(Authentication authentication, BeerAudit audit) {
        if (authentication != null && StringUtils.hasText(authentication.getName()))
            audit.setPrincipalName(authentication.getName());
    }

    private void log(BeerAudit saved) {
        log.info("Saved audit event: {}", saved);
    }

    @EventListener
    @Async
    public void listenUpdate(BeerUpdatedEvent event) {
        listenCommon(event, "BEER_UPDATED");
    }

    @EventListener
    @Async
    public void listenPatch(BeerPatchedEvent event) {
        listenCommon(event, "BEER_PATCHED");
    }

    @EventListener
    @Async
    public void deletePatch(BeerDeletedEvent event) {
        BeerAudit audit = BeerAudit.builder()
                .id(event.getBeerId())
                .build();
        audit.setAuditEventType("BEER_DELETED");

        fillAudit(event.getAuthentication(), audit);

        BeerAudit saved = repository.save(audit);
        log(saved);
    }
}
