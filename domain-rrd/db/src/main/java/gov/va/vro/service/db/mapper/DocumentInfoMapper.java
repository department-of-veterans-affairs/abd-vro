package gov.va.vro.service.db.mapper;

import gov.va.vro.model.claimmetrics.DocumentInfo;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentInfoMapper {
  @Mapping(target = "evidenceInfo", source = "evidenceCount")
  DocumentInfo toAssessmentInfo(EvidenceSummaryDocumentEntity entity);
}
