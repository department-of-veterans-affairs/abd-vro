package gov.va.vro.controller.mapper;

import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface GenerateDataRequestMapper {

  @Mapping(target = "pdfDocumentJson", ignore = true)
  GeneratePdfPayload toModel(GeneratePdfRequest request);

  GeneratePdfResponse toGeneratePdfResponse(GeneratePdfPayload src);

  default GeneratePdfResponse toGeneratePdfResponse(Optional<GeneratePdfPayload> src) {
    return toGeneratePdfResponse(src.orElse(null));
  }

  /**
   * convert to PagedResponse<>.
   *
   * @param src Page<> object
   * @return PagedResponse<>
   */
  default PagedResponse<GeneratePdfResponse> toGeneratePdfResponsePage(
      Page<GeneratePdfPayload> src) {
    return new PagedResponse<>(
        toGeneratePdfResponseList(src.getContent()),
        src.getTotalPages(),
        src.getTotalElements(),
        src.getNumber(),
        src.getNumberOfElements());
  }

  List<GeneratePdfResponse> toGeneratePdfResponseList(List<GeneratePdfPayload> src);
}
