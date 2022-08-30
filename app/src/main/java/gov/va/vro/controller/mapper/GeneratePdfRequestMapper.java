package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface GeneratePdfRequestMapper {

  @Mapping(target = "pdfDocumentJson", ignore = true)
  GeneratePdfPayload toModel(GeneratePdfRequest request);

  GeneratePdfResponse toGeneratePdfResponse(GeneratePdfPayload src);

  default GeneratePdfResponse toGeneratePdfResponse(Optional<GeneratePdfPayload> src) {
    return toGeneratePdfResponse(src.orElse(null));
  }
}
