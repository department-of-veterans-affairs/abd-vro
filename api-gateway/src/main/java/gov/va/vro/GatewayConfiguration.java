package gov.va.vro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {
  @Bean
  HomePageModel homePageModel() {
    HomePageModel homePageModel = new HomePageModel();
    return homePageModel;
  }
}
