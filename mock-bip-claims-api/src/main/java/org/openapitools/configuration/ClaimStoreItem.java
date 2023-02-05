package org.openapitools.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.model.ClaimDetail;
import org.openapitools.model.ContentionSummary;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClaimStoreItem {
  String description;
  ClaimDetail claimDetail;
  List<ContentionSummary> contentions;
}
