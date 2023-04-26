package gov.va.vro.service.provider;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String entityType, Object id) {
    super("Could not find " + entityType + " with id: " + id);
  }
}
