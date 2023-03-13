package gov.va.vro.controller.xample.v3;

import gov.va.vro.api.xample.v3.ResourceRequest;
import gov.va.vro.api.xample.v3.ResourceResponse;
import gov.va.vro.model.xample.SomeDtoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

  @Mapping(target = "status", ignore = true)
  @Mapping(target = "statusCode", ignore = true)
  @Mapping(target = "statusMessage", ignore = true)
  SomeDtoModel toModel(ResourceRequest request);

  ResourceResponse toResourceResponse(SomeDtoModel someDtoModel);

  // default ResourceResponse toResourceResponse(Optional<SomeDtoModel> sharedModel) {
  //   return toResourceResponse(sharedModel.orElse(null));
  // }
}
