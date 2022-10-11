package gov.va.vro.consolegroovy

import org.apache.camel.CamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MyAppConfig {

  @Autowired
  CamelContext camelContext;

//  @Autowired
//  MyGroovyShell gShell

//  @Bean
//  MyGroovyShell myService() {
//    def shell = new MyGroovyShell();
//    shell.camelContext = camelContext
//    shell.startShell()
//    shell
//  }
}
