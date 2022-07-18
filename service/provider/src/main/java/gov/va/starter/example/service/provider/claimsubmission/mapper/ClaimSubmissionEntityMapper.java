package gov.va.starter.example.service.provider.claimsubmission.mapper;

import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ClaimSubmissionEntityMapper {

  ClaimSubmissionEntity toEntity(ClaimSubmission src);

  default Optional<ClaimSubmissionEntity> toEntity(Optional<ClaimSubmission> src) {
    return Optional.ofNullable(toEntity(src.orElse(null)));
  }

  List<ClaimSubmissionEntity> toEntityList(List<ClaimSubmission> src);

  ClaimSubmission toModel(ClaimSubmissionEntity src);

  default Optional<ClaimSubmission> toModel(Optional<ClaimSubmissionEntity> src) {
    return Optional.ofNullable(toModel(src.orElse(null)));
  }

  default Page<ClaimSubmission> toModelPage(Page<ClaimSubmissionEntity> src) {
    return src.map(this::toModel);
  }

  List<ClaimSubmission> toModelList(Iterable<ClaimSubmissionEntity> src);

  @Mapping(target = "id", ignore = true)
  ClaimSubmissionEntity updateMetadata(
      ClaimSubmission src, @MappingTarget ClaimSubmissionEntity dst);

  @Mapping(target = "pii", constant = "FIXME")
  @Mapping(target = "claimSubmissionId", ignore = true)
  SubClaimSubmissionEntity toSubClaimSubmissionEntity(SubClaimSubmission src);

  default Optional<SubClaimSubmissionEntity> toSubClaimSubmissionEntity(
      Optional<SubClaimSubmission> src) {
    return Optional.ofNullable(toSubClaimSubmissionEntity(src.orElse(null)));
  }

  List<SubClaimSubmissionEntity> toSubClaimSubmissionEntityList(List<SubClaimSubmission> src);

  SubClaimSubmission toSubClaimSubmissionModel(SubClaimSubmissionEntity src);

  default Optional<SubClaimSubmission> toSubClaimSubmissionModel(
      Optional<SubClaimSubmissionEntity> src) {
    return Optional.ofNullable(toSubClaimSubmissionModel(src.orElse(null)));
  }

  default Page<SubClaimSubmission> toSubClaimSubmissionModelPage(
      Page<SubClaimSubmissionEntity> src) {
    return src.map(this::toSubClaimSubmissionModel);
  }

  List<SubClaimSubmission> toSubClaimSubmissionModelList(Iterable<SubClaimSubmissionEntity> src);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "pii", ignore = true)
  @Mapping(target = "claimSubmissionId", ignore = true)
  SubClaimSubmissionEntity updateSubClaimSubmissionMetadata(
      SubClaimSubmission src, @MappingTarget SubClaimSubmissionEntity dst);
}
