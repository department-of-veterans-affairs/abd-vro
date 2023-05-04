package gov.va.vro.service.rrd.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimMapper {

  @Mapping(target = "contentions", ignore = true)
  @Mapping(target = "vbmsId", source = "benefitClaimId")
  ClaimEntity toClaimEntity(Claim claim);

  @Mapping(target = "recordId", source = "id")
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  @Mapping(target = "benefitClaimId", source = "vbmsId")
  Claim toClaim(ClaimEntity claimEntity);

  /***
   * <p>Maps list of contention entities to a set of strings.</p>
   *
   * @param contentionEntities contention entities
   * @return return set of strings
   */
  default Set<String> toContentionSet(List<ContentionEntity> contentionEntities) {
    return contentionEntities.stream()
        .map(ContentionEntity::getDiagnosticCode)
        .collect(Collectors.toSet());
  }
}
