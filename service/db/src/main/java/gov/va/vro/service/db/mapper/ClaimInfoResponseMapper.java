package gov.va.vro.service.db.mapper;

import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = ContentionInfoMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClaimInfoResponseMapper {

  // Mapper for v1 claims.
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  @Mapping(
      target = "claimSubmissionId",
      expression =
          "java(claimSubmissionsEntitySetToClaimSubmissionId(claimEntity.getClaimSubmissions()))")
  @Mapping(
      target = "idType",
      expression = "java(claimSubmissionsEntitySetToIdType(claimEntity.getClaimSubmissions()))")
  ClaimInfoResponse toClaimInfoResponseV1(ClaimEntity claimEntity);

  // Mapper for v2 claims.
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  @Mapping(
      target = "claimSubmissionId",
      expression =
          "java(claimSubmissionsEntitySetToBenefitClaimId(claimEntity.getClaimSubmissions()))")
  @Mapping(
      target = "idType",
      expression = "java(claimSubmissionsEntitySetToIdType(claimEntity.getClaimSubmissions()))")
  @Mapping(
      target = "collectionId",
      expression =
          "java(claimSubmissionsEntitySetToCollectionId(claimEntity.getClaimSubmissions()))")
  ClaimInfoResponse toClaimInfoResponseV2(ClaimEntity claimEntity);

  //  List<ClaimInfoResponse> toClaimInfoResponses(Iterable<ClaimEntity> claimEntities);

  // Custom mapper to handle new claim_submission table. The reference_id in claim submission is
  // equal to claimSubmissionId to external systems.
  default String claimSubmissionsEntitySetToClaimSubmissionId(
      Iterable<ClaimSubmissionEntity> claimSubmissionEntities) {
    return claimSubmissionEntities.iterator().next().getReferenceId();
  }

  // Sets the claimInfoResponses claimSubmissionId to VBMS ID(aka benefit claim ID) for v2 claims.
  default String claimSubmissionsEntitySetToBenefitClaimId(
      Iterable<ClaimSubmissionEntity> claimSubmissionEntities) {
    return claimSubmissionEntities.iterator().next().getClaim().getVbmsId();
  }

  default String claimSubmissionsEntitySetToCollectionId(
      Iterable<ClaimSubmissionEntity> claimSubmissionEntities) {
    return claimSubmissionEntities.iterator().next().getReferenceId();
  }

  default String claimSubmissionsEntitySetToIdType(
      Iterable<ClaimSubmissionEntity> claimSubmissionEntities) {
    return claimSubmissionEntities.iterator().next().getIdType();
  }
}
