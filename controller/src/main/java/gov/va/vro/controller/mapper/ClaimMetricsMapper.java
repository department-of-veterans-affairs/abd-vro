package gov.va.vro.controller.mapper;

import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMetricsMapper {
  @Mapping(target = "claimSubmissionId", ignore = false)
  @Mapping(target = "veteranIcn", ignore = false)
  ClaimInfo toClaimInfo(ClaimMetricsInfo metricsInfo);
}
