package gov.va.vro.service.provider.bgs.service;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String entityType, Object id) {
    super("Could not find "+entityType+" with id: " + id);
  }
}
