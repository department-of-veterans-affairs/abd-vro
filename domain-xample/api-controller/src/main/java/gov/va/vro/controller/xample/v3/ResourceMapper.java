package gov.va.vro.controller.xample.v3;

import gov.va.vro.api.xample.v3.ResourceRequest;
import gov.va.vro.api.xample.v3.ResourceResponse;
import gov.va.vro.model.xample.SomeDtoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

  @Mapping(target = "status", ignore = true)
  @Mapping(target = "reason", ignore = true)
  SomeDtoModel toModel(ResourceRequest request);

  //  @Mapping(target = "xResourceId", source = "xResourceId")
  //  @Mapping(target = "status")
  //  @Mapping(target = "reason")
  ResourceResponse toResourceResponse(SomeDtoModel someDtoModel);

  default ResourceResponse toResourceResponse(Optional<SomeDtoModel> sharedModel) {
    return toResourceResponse(sharedModel.orElse(null));
  }
}
