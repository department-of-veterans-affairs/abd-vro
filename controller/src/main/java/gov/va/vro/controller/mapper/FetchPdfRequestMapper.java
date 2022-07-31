package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.FetchPdfRequest;
import gov.va.vro.api.responses.FetchPdfResponse;
import gov.va.vro.service.spi.model.FetchPdfPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface FetchPdfRequestMapper {

  @Mapping(target = "pdfDocumentJson", ignore = true)
  FetchPdfPayload toModel(FetchPdfRequest request);

  FetchPdfResponse toFetchPdfResponse(FetchPdfPayload src);

  default FetchPdfResponse toFetchPdfResponse(Optional<FetchPdfPayload> src) {
    return toFetchPdfResponse(src.orElse(null));
  }
}
