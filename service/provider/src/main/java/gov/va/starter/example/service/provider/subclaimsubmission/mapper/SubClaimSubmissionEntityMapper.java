package gov.va.starter.example.service.provider.subclaimsubmission.mapper;

import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SubClaimSubmissionEntityMapper {

  @Mapping(target = "claimSubmissionId", ignore = true)
  SubClaimSubmissionEntity toEntity(SubClaimSubmission src);

  default Optional<SubClaimSubmissionEntity> toEntity(Optional<SubClaimSubmission> src) {
    return Optional.ofNullable(toEntity(src.orElse(null)));
  }

  List<SubClaimSubmissionEntity> toEntityList(List<SubClaimSubmission> src);

  SubClaimSubmission toModel(SubClaimSubmissionEntity src);

  default Optional<SubClaimSubmission> toModel(Optional<SubClaimSubmissionEntity> src) {
    return Optional.ofNullable(toModel(src.orElse(null)));
  }

  default Page<SubClaimSubmission> toModelPage(Page<SubClaimSubmissionEntity> src) {
    return src.map(this::toModel);
  }

  List<SubClaimSubmission> toModelList(Iterable<SubClaimSubmissionEntity> src);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimSubmissionId", ignore = true)
  SubClaimSubmissionEntity updateMetadata(
      SubClaimSubmission src, @MappingTarget SubClaimSubmissionEntity dst);
}
