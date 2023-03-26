package gov.va.vro.service.db.mapper;

import gov.va.vro.model.rrd.claimmetrics.ContentionInfo;
import gov.va.vro.persistence.model.ContentionEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {AssessmentInfoMapper.class, DocumentInfoMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContentionInfoMapper {
  @Mapping(target = "assessments", source = "assessmentResults")
  @Mapping(target = "documents", source = "evidenceSummaryDocuments")
  ContentionInfo toContentionInfo(ContentionEntity contentionEntity);
}
