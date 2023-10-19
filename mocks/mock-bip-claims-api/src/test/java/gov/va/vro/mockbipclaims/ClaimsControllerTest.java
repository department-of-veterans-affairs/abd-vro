package gov.va.vro.mockbipclaims.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.bip.response.CloseClaimResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class ClaimsControllerTest {

  @InjectMocks private ClaimsController claimsController;

  @Mock private ClaimStore claimStore;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetClaimById_Success() {
    Long claimId = 1010L;
    ClaimDetail claimDetail = new ClaimDetail();
    ClaimStoreItem item = new ClaimStoreItem();
    item.setClaimDetail(claimDetail);
    when(claimStore.get(claimId)).thenReturn(item);

    ResponseEntity<ClaimDetailResponse> response = claimsController.getClaimById(claimId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(claimDetail, response.getBody().getClaim());
  }

  @Test
  public void testGetClaimById_NotFound() {
    Long claimId = 1010L;
    when(claimStore.get(claimId)).thenReturn(null);

    try {
      claimsController.getClaimById(claimId);
    } catch (ResponseStatusException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }
  }

  @Test
  public void testCancelClaimById_Success() {
    Long claimId = 1010L;
    ClaimStoreItem item = new ClaimStoreItem();
    when(claimStore.get(claimId)).thenReturn(item);

    ResponseEntity<CloseClaimResponse> response = claimsController.cancelClaimById(claimId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        "Successfully canceled the claim with id: " + claimId,
        response.getBody().getMessages().get(0).getText());
    verify(claimStore).cancel(claimId);
  }

  @Test
  public void testCancelClaimById_NotFound() {
    Long claimId = 1010L;
    when(claimStore.get(claimId)).thenReturn(null);

    try {
      claimsController.cancelClaimById(claimId);
    } catch (ResponseStatusException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }
  }
}
