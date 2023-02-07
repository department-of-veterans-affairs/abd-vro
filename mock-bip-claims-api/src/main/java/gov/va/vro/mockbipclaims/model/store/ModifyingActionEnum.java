package gov.va.vro.mockbipclaims.model.store;

public enum ModifyingActionEnum {
  LIFECYCLE_PUT("lifecycle_put"),
  CONTENTION_PUT("contention_put");

  private String description;

  ModifyingActionEnum(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
