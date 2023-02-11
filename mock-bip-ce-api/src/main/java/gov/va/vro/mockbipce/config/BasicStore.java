package gov.va.vro.mockbipce.config;

import gov.va.vro.mockbipce.model.EvidenceFile;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class BasicStore {
  private final Map<String, EvidenceFile> store = new HashMap<>();

  public EvidenceFile get(String fileNumber) {
    return store.get(fileNumber);
  }

  public void remove(String fileNumber) {
    store.remove(fileNumber);
  }

  public void put(EvidenceFile evidenceFile) {
    String fileNumber = evidenceFile.getFileNumber();
    store.put(fileNumber, evidenceFile);
  }
}
