package gov.va.vro;

import com.google.common.collect.Sets;
import com.rabbitmq.client.ConnectionFactory;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.model.Payload;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.converter.CoreTypeConverterRegistry;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.InputStream;
import java.util.Set;

@Configuration
@EnableJpaRepositories({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
@EntityScan({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
public class AppConfig {
  @Value("${vro.context_path}")
  public String contextPath;

  public static String servletName = "UniqCamelServletName";

  @Autowired private CamelContext camelContext;

  @Bean
  CamelContextConfiguration contextConfiguration() {
    return new CamelContextConfiguration() {
      @Override
      public void beforeApplicationStart(CamelContext context) {
        SedaComponent sedaComponent = context.getComponent("seda", SedaComponent.class);
        sedaComponent.setDefaultBlockWhenFull(true);
      }

      @Override
      public void afterApplicationStart(CamelContext camelContext) {
        System.err.println("=====================================");
        registerTypeConverter(camelContext);
      }
    };
  }

  public static Set<Class> DTO_CLASSES = Sets.newHashSet(ClaimSubmission.class, Payload.class);

  // TODO: replace with Auto-configured TypeConverter
  // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-Auto-configuredTypeConverter
  private void registerTypeConverter(CamelContext camelContext) {
    // Define the behaviour if the TypeConverter already exists
    DtoConverter.registerWith(camelContext, DTO_CLASSES);

    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    ((CoreTypeConverterRegistry) registry)
        .getTypeMappings()
        .forEach(
            (fromClass, toClass, converter) -> {
              System.err.println(
                  fromClass.getName() + " -> " + toClass.getName() + " : " + converter.getClass());
            });
    System.err.println("\n+++++++ " + registry.lookup(ClaimSubmission.class, byte[].class));
    System.err.println("\n+++++++ " + registry.lookup(byte[].class, ClaimSubmission.class));
    System.err.println("\n+++++++ " + registry.lookup(ClaimSubmission.class, InputStream.class));
  }

  @Autowired public MessageQueueProperties messageQueueProps;

  @Bean
  ConnectionFactory rabbitConnectionFactory() {
    System.err.println(
        "===================================== "
            + messageQueueProps.getHost()
            + ":"
            + messageQueueProps.getPort());
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUser());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }

  // https://opensource.com/article/18/9/camel-rest-dsl
  // https://stackoverflow.com/questions/55127006/multiple-servlets-with-camel-servlet-possible
  @Bean
  ServletRegistrationBean servletRegistrationBean() {
    ServletRegistrationBean servlet =
        new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath + "/*");
    servlet.setName(servletName);
    System.out.println(servlet);
    return servlet;
  }

  // See
  // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-Auto-configuredconsumerandproducertemplates
  @Autowired private ProducerTemplate producerTemplate;

  //  @Bean
  //  ProducerTemplate producerTemplate() {
  //    return camelContext.createProducerTemplate();
  //  }

  // @Bean
  // ConsumerTemplate consumerTemplate() {
  //   return camelContext.createConsumerTemplate();
  // }

  //  @Bean
  //  ObjectMapper objectMapper() {
  //    System.out.println("+++++ ObjectMapper");
  //    ObjectMapper objectMapper = new ObjectMapper();
  //    objectMapper.registerModule(new JavaTimeModule());
  //    return objectMapper;
  //  }

  // https://stackoverflow.com/questions/33397359/how-to-configure-jackson-objectmapper-for-camel-in-spring-boot
  //  @Bean(name = "json-jackson")
  //  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  //  public JacksonDataFormat jacksonDataFormat(ObjectMapper objectMapper) {
  //    System.out.println("+++++ JacksonDataFormat");
  //    return new JacksonDataFormat(objectMapper, HashMap.class);
  //  }
}
