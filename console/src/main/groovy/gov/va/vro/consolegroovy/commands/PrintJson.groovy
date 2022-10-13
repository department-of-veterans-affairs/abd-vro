package gov.va.vro.consolegroovy.commands

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import gov.va.vro.persistence.model.AssessmentResultEntity
import gov.va.vro.persistence.model.ClaimEntity
import gov.va.vro.persistence.model.ContentionEntity
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity
import org.apache.groovy.groovysh.CommandSupport
import org.apache.groovy.groovysh.Groovysh

//@CompileStatic
class PrintJson extends CommandSupport {

    private final ObjectMapper objectMapper;

    protected PrintJson(final Groovysh shell, ObjectMapper objectMapper) {
        super(shell, 'printJson', 'pj')
        // TODO: add Command description, usage, etc.

        this.objectMapper = objectMapper
    }

    Object execute(List args) {
        log.debug "args: ${args}"
        args.collect({
            def varObj = variables.get(it)
            // in case `it` is executable
            if (varObj==null) varObj = shell.execute(it)

            if (varObj==null) return "null"

            ObjectWriter writer = writerForObjectMemoized(varObj)
            writer.writeValueAsString(varObj)
        }).join('\n')
    }

    def mapperForObject = {
        switch(it){
            case ClaimEntity: return configureClaimJsonMapper(objectMapper)
            case ContentionEntity: return configureContentionJsonMapper(objectMapper)
            default: return configureDefaultJsonMapper(objectMapper)
        }
    }

    def writerForObject = {
        log.debug "Creating writer of object: ${it.class}"
        mapperForObject(it).writerWithDefaultPrettyPrinter()
    }
    def writerForObjectMemoized = writerForObject.memoize()

    def configureDefaultJsonMapper(ObjectMapper mapper){
        // ignore certain fields to prevent recursion
        mapper.addMixIn(AssessmentResultEntity, IgnoreFieldContentionMixin);
        mapper.addMixIn(EvidenceSummaryDocumentEntity, IgnoreFieldContentionMixin);
        mapper
    }

    def configureClaimJsonMapper(ObjectMapper mapper){
        // ignore certain fields to prevent recursion
        mapper.addMixIn(ContentionEntity, IgnoreFieldClaimMixin);
        configureDefaultJsonMapper(mapper)
    }

    def configureContentionJsonMapper(ObjectMapper mapper){
        // ignore certain fields to prevent recursion
        mapper.addMixIn(ClaimEntity, IgnoreFieldContentionsMixin);
        configureDefaultJsonMapper(mapper)
    }

    @JsonIgnoreProperties(['claim'])
    static class IgnoreFieldClaimMixin { }

    @JsonIgnoreProperties(['contention'])
    static class IgnoreFieldContentionMixin { }

    @JsonIgnoreProperties(['contentions'])
    static class IgnoreFieldContentionsMixin { }
}
