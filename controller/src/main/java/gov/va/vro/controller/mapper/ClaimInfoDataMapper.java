package gov.va.vro.controller.mapper;

import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.service.spi.model.ClaimInfoData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimInfoDataMapper {
  @Mapping(target = "claimSubmissionId", ignore = false)
  @Mapping(target = "veteranIcn", ignore = false)
  ClaimInfo toClaimInfo(ClaimInfoData metricsInfo);
}
