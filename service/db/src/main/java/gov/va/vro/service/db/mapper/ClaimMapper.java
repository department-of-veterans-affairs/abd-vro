package gov.va.vro.service.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimMapper {

  @Mapping(target = "contentions", ignore = true)
  ClaimEntity toClaimEntity(Claim claim);

  @Mapping(target = "recordId", source = "id")
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  Claim toClaim(ClaimEntity claimEntity);

  default Set<String> toContentionSet(List<ContentionEntity> contentionEntities) {
    return contentionEntities.stream()
        .map(ContentionEntity::getDiagnosticCode)
        .collect(Collectors.toSet());
  }
}
