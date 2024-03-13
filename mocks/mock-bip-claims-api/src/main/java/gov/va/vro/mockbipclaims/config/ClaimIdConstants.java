package gov.va.vro.mockbipclaims.config;

public class ClaimIdConstants {

  public static final int CLAIM_ID_ALL_ENDPOINTS_YIELDS_500 = 500;

  // GET /claims/{claimId}
  public static final int CLAIM_ID_GET_CLAIM_DETAILS_YIELDS_500 = 5001;
  // PUT /claims/{claimId}/cancel
  public static final int CLAIM_ID_CANCEL_CLAIM_YIELDS_500 = 5002;
  // PUT /claims/{claimId}/temporary_station_of_jurisdiction
  public static final int CLAIM_ID_SET_TSOJ_YIELDS_500 = 5003;

  // GET /claims/{claimId}/contentions
  public static final int CLAIM_ID_GET_PENDING_EP_CONTENTIONS_YIELDS_500 = 5004;
  public static final int CLAIM_ID_GET_SUPP_EP_CONTENTIONS_YIELDS_500 = 50041;
  // PUT /claims/{claimId}/contentions
  public static final int CLAIM_ID_UPDATE_CONTENTIONS_YIELDS_500 = 5005;
  // POST /claims/{claimId}/contentions
  public static final int CLAIM_ID_CREATE_CONTENTIONS_YIELDS_500 = 5006;

  //  PUT /claims/{claimId}/lifecycle_status
  public static final int CLAIM_ID_UPDATE_LIFECYCLE_STATUS_YIELDS_500 = 5007;
}
