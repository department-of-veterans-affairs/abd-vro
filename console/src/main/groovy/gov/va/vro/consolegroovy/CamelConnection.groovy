package gov.va.vro.consolegroovy


import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@groovy.transform.TupleConstructor
class CamelConnection {
  @Autowired
  CamelContext camelContext

  @Autowired
  ProducerTemplate producerTemplate
}
