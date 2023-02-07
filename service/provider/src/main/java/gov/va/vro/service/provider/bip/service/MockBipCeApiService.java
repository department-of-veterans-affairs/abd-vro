package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.service.provider.bip.BipException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Mock some claim data returned by the BIP API. */
@Service
@Slf4j
@Conditional(BipConditions.LocalEnvironmentCondition.class)
public class MockBipCeApiService implements IBipCeApiService {
  @Override
  public BipFileUploadResp uploadEvidenceFile(
      FileIdType fileIdType, String fileId, BipFileUploadPayload payload, byte[] fileContent)
      throws BipException {
    log.info("Entered MockBipCeApiService for {}:{}", fileIdType, fileId);
    String message =
        String.format(
            "This is a mock response to upload multipart file for %s.",
            fileIdType.name() + ":" + fileId);
    return new BipFileUploadResp(HttpStatus.OK, message);
  }
}
