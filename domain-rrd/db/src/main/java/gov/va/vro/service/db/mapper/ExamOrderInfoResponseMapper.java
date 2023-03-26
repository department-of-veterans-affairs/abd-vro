package gov.va.vro.service.db.mapper;

import gov.va.vro.model.rrd.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.persistence.model.ExamOrderEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = ContentionInfoMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ExamOrderInfoResponseMapper {

  @Mapping(
      target = "hasAssociatedClaimSubmission",
      expression = "java(examOrderEntity.getClaimSubmission() != null)")
  @Mapping(
      target = "orderedAt",
      expression = "java(convertOrderedAt(examOrderEntity.getOrderedAt()))")
  ExamOrderInfoResponse toExamOrderInfoResponse(ExamOrderEntity examOrderEntity);

  List<ExamOrderInfoResponse> toExamOrderInfoResponses(Iterable<ExamOrderEntity> examOrderEntities);

  default LocalDateTime convertOrderedAt(OffsetDateTime offsetOrdered) {
    if (offsetOrdered != null) {
      return offsetOrdered.toLocalDateTime();
    }
    return null;
  }
}
