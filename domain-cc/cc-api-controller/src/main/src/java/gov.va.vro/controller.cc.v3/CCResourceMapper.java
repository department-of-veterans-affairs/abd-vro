package gov.va.vro.controller.cc.v3;

import gov.va.vro.api.cc.v3.CCRequest;
import gov.va.vro.api.cc.v3.CCResponse;
//import gov.va.vro.model.cc.SomeDtoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

 //  @Mapping(target = "status", ignore = true)
 //  @Mapping(target = "statusCode", ignore = true)
 //  @Mapping(target = "statusMessage", ignore = true)
 //  SomeDtoModel toModel(CCRequest request);

  CCResponse toResourceResponse(String someStr);

//   default CCResponse toResourceResponse(Optional<SomeDtoModel> sharedModel) {
//     return toResourceResponse(sharedModel.orElse(null));
//   }
}
