package gov.va.vro.mockbipclaims.configuration;

import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClaimStoreItem {
  String description;
  ClaimDetail claimDetail;
  List<ContentionSummary> contentions;
}
