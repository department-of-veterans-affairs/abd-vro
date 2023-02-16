package gov.va.vro.service.db.mapper;

import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = ContentionInfoMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClaimInfoResponseMapper {
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  @Mapping(
      target = "claimSubmissionId",
      expression =
          "java(claimSubmissionsEntitySetToClaimSubmissionId(claimEntity.getClaimSubmissions()))")
  ClaimInfoResponse toClaimInfoResponse(ClaimEntity claimEntity);

  List<ClaimInfoResponse> toClaimInfoResponses(Iterable<ClaimEntity> claimEntities);

  // Custom mapper to handle new claim_submission table. The reference_id in claim submission is
  // equal to claimSubmissionId to external systems.
  default String claimSubmissionsEntitySetToClaimSubmissionId(
      Iterable<ClaimSubmissionEntity> claimSubmissionEntities) {
    return claimSubmissionEntities.iterator().next().getReferenceId();
  }
}
