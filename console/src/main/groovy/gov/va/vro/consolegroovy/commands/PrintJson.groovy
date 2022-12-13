package gov.va.vro.consolegroovy.commands

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import gov.va.vro.persistence.model.AssessmentResultEntity
import gov.va.vro.persistence.model.ClaimEntity
import gov.va.vro.persistence.model.ContentionEntity
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity
import groovy.transform.CompileStatic
import org.apache.groovy.groovysh.CommandSupport
import org.apache.groovy.groovysh.Groovysh

@CompileStatic
class PrintJson extends CommandSupport {

  private final ObjectMapper objectMapper

  PrintJson(final Groovysh shell, ObjectMapper objectMapper) {
    super(shell, 'printJson', 'pj')
    // TODO: add Command description, usage, etc.

    this.objectMapper = objectMapper
  }

  Object execute(final List<String> args) {
    log.debug "args: ${args}"
    args.collect({ String it ->
      Object varObj = variables.get(it)
      // in case `it` is executable
      if (varObj == null) varObj = shell.execute(it)

      if (varObj == null) return "null"

      ObjectWriter writer = writerForObjectMemoized(varObj.class)
      writer.writeValueAsString(varObj)
    }).join('\n')
  }

  private Closure<ObjectMapper> mapperForObject = { Object clazz ->
    switch (clazz) {
      case ClaimEntity: return configureClaimJsonMapper(objectMapper.copy())
      case ContentionEntity: return configureContentionJsonMapper(objectMapper.copy())
    }
    configureDefaultJsonMapper(objectMapper.copy())
  }
  private Closure<ObjectWriter> writerForObjectMemoized = { Object clazz ->
    log.debug "Creating writer of object: ${clazz}"
    return mapperForObject(clazz).writerWithDefaultPrettyPrinter()
  }.memoize()

  ObjectMapper configureDefaultJsonMapper(ObjectMapper mapper) {
    // ignore certain fields to prevent recursion
    mapper.addMixIn(AssessmentResultEntity, IgnoreFieldContentionMixin)
    mapper.addMixIn(EvidenceSummaryDocumentEntity, IgnoreFieldContentionMixin)
    mapper
  }

  ObjectMapper configureClaimJsonMapper(ObjectMapper mapper) {
    // ignore certain fields to prevent recursion
    mapper.addMixIn(ContentionEntity, IgnoreFieldClaimMixin)
    configureDefaultJsonMapper(mapper)
  }

  ObjectMapper configureContentionJsonMapper(ObjectMapper mapper) {
    // ignore certain fields to prevent recursion
    mapper.addMixIn(ClaimEntity, IgnoreFieldContentionsMixin)
    configureDefaultJsonMapper(mapper)
  }

  @JsonIgnoreProperties(['claim'])
  static class IgnoreFieldClaimMixin {}

  @JsonIgnoreProperties(['contention'])
  static class IgnoreFieldContentionMixin {}

  @JsonIgnoreProperties(['contentions'])
  static class IgnoreFieldContentionsMixin {}
}
