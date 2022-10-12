package gov.va.vro.consolegroovy.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import org.apache.groovy.groovysh.CommandSupport
import org.apache.groovy.groovysh.Groovysh

class PrintJson extends CommandSupport {
    private final ObjectWriter writer;

    protected PrintJson(final Groovysh shell, ObjectMapper objectMapper) {
        super(shell, 'print-json', 'pj')
        // TODO: add description, usage, etc.
        writer = objectMapper.writer()
    }

    Object execute(List args) {
        println "args: ${args}"
        args.collect({
            def varObj = variables.get(it)
            // TODO: handle recursion
            println "varObj: ${varObj.id}"
            new String(writer.writeValueAsBytes(varObj))
        }).join('\n')
    }
}
