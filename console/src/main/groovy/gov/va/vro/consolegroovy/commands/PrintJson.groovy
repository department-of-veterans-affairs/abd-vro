package gov.va.vro.consolegroovy.commands

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.ser.FilterProvider
import com.fasterxml.jackson.databind.ser.PropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import gov.va.vro.persistence.model.ClaimEntity
import gov.va.vro.persistence.model.ContentionEntity
import org.apache.groovy.groovysh.CommandSupport
import org.apache.groovy.groovysh.Groovysh

//@CompileStatic
class PrintJson extends CommandSupport {

    private final ObjectWriter defaultWriter
    private final Map<Class<?>, ObjectWriter> writers = [:]

    protected PrintJson(final Groovysh shell, ObjectMapper objectMapper) {
        super(shell, 'print-json', 'pj')
        // TODO: add description, usage, etc.
        this.defaultWriter=objectMapper.writer()

        // TODO: configure when needed
        writers[ClaimEntity] = configureClaimJsonWriter(objectMapper.copy())
    }

    Object execute(List args) {
        println "args: ${args}"
        args.collect({
            def varObj = variables.get(it)
            if (varObj==null) return "null"

            if(true) {
                println writers.keySet() + " --- "+ varObj.class
                ObjectWriter writer = writers[varObj.class]// ?: defaultWriter
                writer.writeValueAsString(varObj)
            } else {
                def generator = claimJsonGenerator()
                generator.toJson(varObj)
            }
        }).join('\n')
    }

    // Doesn't work
    def claimJsonGenerator(){
        new groovy.json.JsonGenerator.Options().excludeNulls()
            .dateFormat('dd-MM-yyyy').excludeFieldsByName( 'contentions').build()
    }

    def configureClaimJsonWriter(ObjectMapper mapper){
        mapper.addMixIn(ContentionEntity, IgnoreFieldClaimMixin);

        return mapper.writer()

//        def filters = claimJsonFilter()
//        mapper.writer(filters)
    }

    // Not needed
    def claimJsonFilter(){
        PropertyFilter propFilter = SimpleBeanPropertyFilter.serializeAllExcept( 'contentions')
        FilterProvider filters = new SimpleFilterProvider().addFilter("claimJsonFilter", propFilter as PropertyFilter)
        filters
    }
    @JsonFilter("claimJsonFilter")
    class ClaimJsonMixin { }

    @JsonIgnoreProperties(['claim'])
    static class IgnoreFieldClaimMixin { }

    @JsonIgnoreProperties(['contention'])
    static class IgnoreFieldContentionMixin { }
}
