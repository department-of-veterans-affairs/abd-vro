package gov.va.vro;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    HomePageModel homePageModel(){
        HomePageModel homePageModel = new HomePageModel();
        return homePageModel;
    }

//   @Autowired
//   RouteDefinitionLocator locator;
//
//   @Bean
//   public List<GroupedOpenApi> apis() {
//      List<GroupedOpenApi> groups = new ArrayList<>();
//      List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
//      definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service")).forEach(routeDefinition -> {
//         String name = routeDefinition.getId().replaceAll("-service", "");
//         GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").setGroup(name).build();
//      });
//      return groups;
//   }
}
