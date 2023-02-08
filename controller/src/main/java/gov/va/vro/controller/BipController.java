package gov.va.vro.controller;

import gov.va.vro.api.resources.BipResource;
import gov.va.vro.api.responses.BipClaimContentionsResponse;
import gov.va.vro.api.responses.BipClaimResponse;
import gov.va.vro.api.responses.BipClaimStatusResponse;
import gov.va.vro.api.responses.BipContentionCreationResponse;
import gov.va.vro.api.responses.BipContentionUpdateResponse;
import gov.va.vro.api.responses.BipFileUploadResponse;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipCreateClaimContentionPayload;
import gov.va.vro.model.bip.BipUpdateClaimContentionPayload;
import gov.va.vro.model.bip.BipUpdateClaimPayload;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.CreateContention;
import gov.va.vro.model.bip.CreateContentionReq;
import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bip.UpdateContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.bipevidence.BipFileProviderData;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import gov.va.vro.service.provider.bip.service.IBipCeApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

/**
 * It provides all the endpoints for BIP API services.
 *
 * @author warren @Date 11/7/22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("dev")
public class BipController implements BipResource {
  private final IBipApiService service;

  private final IBipCeApiService ceService;

  @Override
  public ResponseEntity<BipClaimStatusResponse> setClaimRfd(@Valid BipUpdateClaimPayload request) {
    log.info("Set the claim status to RFD for claim ID {}", request.getClaimId());

    long claimId = Long.parseLong(request.getClaimId());
    try {
      BipUpdateClaimResp resp = service.setClaimToRfdStatus(claimId);
      String msg = resp.getMessage();
      boolean isSuccessful = resp.getStatus() == HttpStatus.OK;
      BipClaimStatusResponse response =
          BipClaimStatusResponse.builder().updated(isSuccessful).message(msg).build();
      return ResponseEntity.ok(response);
    } catch (BipException e) {
      log.error("failed to update claim to RFD, {}", claimId);
      BipClaimStatusResponse badResp =
          BipClaimStatusResponse.builder().updated(false).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipClaimResponse> getClaim(@Valid String id) {
    try {
      long claimId = Long.parseLong(id);
      log.info("Received claim info for claim ID {}", claimId);

      BipClaim response = service.getClaimDetails(claimId);
      BipClaimResponse resp = BipClaimResponse.builder().claimId(claimId).claim(response).build();
      return ResponseEntity.ok(resp);
    } catch (SecurityException | NullPointerException e) {
      BipClaimResponse errResp =
          BipClaimResponse.builder().message("Invalid claim ID: " + id).build();
      return ResponseEntity.badRequest().body(errResp);
    } catch (BipException e) {
      log.error("failed to get claim details, {}", id, e);
      BipClaimResponse bipErrResp = BipClaimResponse.builder().message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(bipErrResp);
    }
  }

  @Override
  public ResponseEntity<BipClaimContentionsResponse> getContentions(@Valid String id) {
    long claimId = Long.parseLong(id);
    log.info("Retrieve contentions for claim ID {} from {}", claimId, id);
    try {
      List<ClaimContention> resp = service.getClaimContentions(claimId);
      BipClaimContentionsResponse response =
          BipClaimContentionsResponse.builder().contentions(resp).claimId(claimId).build();
      return ResponseEntity.ok(response);
    } catch (SecurityException | NullPointerException e) {
      return ResponseEntity.badRequest().build();
    } catch (BipException e) {
      log.error("failed to get contentions for {}", id, e);
      BipClaimContentionsResponse badResp =
          BipClaimContentionsResponse.builder().claimId(claimId).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipContentionUpdateResponse> updateContentions(
      @Valid BipUpdateClaimContentionPayload payload) {
    log.info("update a contention for claim ID {}", payload.getClaimId());

    try {
      UpdateContention contention = payload.getContention();
      List<UpdateContention> contentions = Collections.singletonList(contention);
      UpdateContentionReq req =
          UpdateContentionReq.builder().updateContentions(contentions).build();
      BipUpdateClaimResp resp = service.updateClaimContention(payload.getClaimId(), req);
      boolean isUpdated = resp.getStatus() == HttpStatus.OK;
      BipContentionUpdateResponse response =
          BipContentionUpdateResponse.builder()
              .updated(isUpdated)
              .contentionId(Optional.of(payload.getContention().getContentionId()).orElse(0L))
              .message(resp.getMessage())
              .build();
      return ResponseEntity.ok(response);
    } catch (BipException e) {
      BipContentionUpdateResponse badResp =
          BipContentionUpdateResponse.builder().updated(false).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipContentionCreationResponse> createContentions(
      @Valid BipCreateClaimContentionPayload payload) {
    log.info("Create a contention for claim ID {}", payload.getClaimId());

    try {
      List<CreateContention> contentions = Collections.singletonList(payload.getContention());
      CreateContentionReq req = new CreateContentionReq();
      req.setCreateContentions(contentions);
      BipUpdateClaimResp resp = service.addClaimContention(payload.getClaimId(), req);
      boolean isCreated = resp.getStatus() == HttpStatus.CREATED;
      BipContentionCreationResponse response =
          BipContentionCreationResponse.builder()
              .claimId(payload.getClaimId())
              .created(isCreated)
              .message(resp.getMessage())
              .build();
      return ResponseEntity.status(resp.getStatus()).body(response);
    } catch (BipException e) {
      log.error("failed to create contention for claim {}", payload.getClaimId(), e);
      BipContentionCreationResponse badResp =
          BipContentionCreationResponse.builder()
              .contentionId(payload.getClaimId())
              .created(false)
              .message(e.getMessage())
              .build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipFileUploadResponse> fileUpload(
      @Valid String fileId,
      @Valid String fileIdType,
      BipFileProviderData providerData,
      MultipartFile file)
      throws BipException {
    log.info("upload evidence file, fileID: {}, ID type: {}", fileId, fileIdType);
    try {
      FileIdType type = FileIdType.getIdType(fileIdType);
      if (type == null) {
        BipFileUploadResponse badResp =
            BipFileUploadResponse.builder().message("Invalid ID type: " + fileIdType).build();
        return ResponseEntity.badRequest().body(badResp);
      }
      BipFileUploadPayload payload =
          BipFileUploadPayload.builder()
              .contentName(file.getOriginalFilename())
              .providerData(providerData)
              .build();
      providerData.setDateVaReceivedDocument(LocalDate.now().toString());
      BipFileUploadResp resp = ceService.uploadEvidenceFile(type, fileId, payload, file.getBytes());
      BipFileUploadResponse result =
          BipFileUploadResponse.builder()
              .uploaded(resp.getStatus() == HttpStatus.OK)
              .message(resp.getMessage())
              .build();
      return ResponseEntity.status(
              resp.getStatus() != null ? resp.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR)
          .body(result);
    } catch (BipException | IOException e) {
      BipFileUploadResponse badResult =
          BipFileUploadResponse.builder().uploaded(false).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResult);
    }
  }
}
