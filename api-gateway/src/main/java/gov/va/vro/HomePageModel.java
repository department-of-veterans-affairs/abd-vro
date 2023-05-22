package gov.va.vro;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
class HomePageModel {
    @Value("${vro.openapi.info.version}")
    String version;
}
