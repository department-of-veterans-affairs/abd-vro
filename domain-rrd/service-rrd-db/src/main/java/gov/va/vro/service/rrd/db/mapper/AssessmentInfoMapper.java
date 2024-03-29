package gov.va.vro.service.rrd.db.mapper;

import gov.va.vro.model.rrd.claimmetrics.AssessmentInfo;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssessmentInfoMapper {
  @Mapping(target = "evidenceInfo", source = "evidenceCountSummary")
  AssessmentInfo toAssessmentInfo(AssessmentResultEntity assessmentResultEntity);
}
