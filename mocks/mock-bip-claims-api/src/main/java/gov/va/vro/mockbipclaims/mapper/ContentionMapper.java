package gov.va.vro.mockbipclaims.mapper;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.ExistingContention;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContentionMapper {
  ExistingContention toExistingContention(ContentionSummary summary);

  ContentionSummary toContentionSummary(ExistingContention existing);
}
