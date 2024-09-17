package gov.va.vro.services.bie.service.repo;

import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import gov.va.vro.persistence.repository.bieevent.ContentionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentionEventsRepo {

  private final ContentionEventRepository repo;

  public ContentionEventEntity save(ContentionEventEntity contentionEventEntity) {
    return repo.save(contentionEventEntity);
  }
}
