package gov.va.vro.controller.rrd.mapper;

import gov.va.vro.api.rrd.requests.GeneratePdfRequest;
import gov.va.vro.api.rrd.responses.GeneratePdfResponse;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface GeneratePdfRequestMapper {

  @Mapping(target = "status", ignore = true)
  @Mapping(target = "reason", ignore = true)
  GeneratePdfPayload toModel(GeneratePdfRequest request);

  @Mapping(target = "status")
  @Mapping(target = "reason")
  GeneratePdfResponse toGeneratePdfResponse(GeneratePdfPayload src);

  default GeneratePdfResponse toGeneratePdfResponse(Optional<GeneratePdfPayload> src) {
    return toGeneratePdfResponse(src.orElse(null));
  }
}
