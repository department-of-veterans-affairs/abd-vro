package gov.va.vro.controller.demo.mapper;

import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.vro.api.demo.requests.AssessHealthDataRequest;
import gov.va.vro.api.demo.responses.AssessHealthDataResponse;
import gov.va.vro.service.spi.demo.AssessHealthData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AssessHealthDataRequestMapper {

  @Mapping(target = "bpReadingsJson", ignore = true)
  AssessHealthData toModel(AssessHealthDataRequest request);

  AssessHealthDataResponse toAssessHealthDataResponse(AssessHealthData src);

  default AssessHealthDataResponse toAssessHealthDataResponse(Optional<AssessHealthData> src) {
    return toAssessHealthDataResponse(src.orElse(null));
  }

  /**
   * convert to PagedResponse<>.
   *
   * @param src Page<> object
   * @return PagedResponse<>
   */
  default PagedResponse<AssessHealthDataResponse> toAssessHealthDataResponsePage(
      Page<AssessHealthData> src) {
    return new PagedResponse<>(
        toAssessHealthDataResponseList(src.getContent()),
        src.getTotalPages(),
        src.getTotalElements(),
        src.getNumber(),
        src.getNumberOfElements());
  }

  List<AssessHealthDataResponse> toAssessHealthDataResponseList(List<AssessHealthData> src);
}
