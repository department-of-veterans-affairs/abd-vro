package gov.va.vro.consolegroovy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
scanBasePackages = ["gov.va.vro.consolegroovy", "gov.va.vro.camel"]
)
class ConsoleGroovyApplication {
  static void main(String[] args) {
    SpringApplication.run(ConsoleGroovyApplication, args)
  }
}
