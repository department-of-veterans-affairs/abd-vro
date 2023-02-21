package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.groovy.groovysh.Command
import org.apache.groovy.groovysh.Groovysh
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
class VroConsoleShellPrintJsonTests {

  @Mock
  DatabaseConnection db

  @Mock
  CamelConnection camel

  @Mock
  RedisConnection redis

  Closure<List<Command>> vroConsoleCommandsFactory = new VroConsoleConfig().vroConsoleCommandsFactory(new ObjectMapper())

  VroConsoleShell consoleShell
  Groovysh shell

  @BeforeEach
  void setup(){
    consoleShell = new VroConsoleShell(vroConsoleCommandsFactory, db, camel, redis)
    shell = consoleShell.setupVroShell()
  }

  @Test
  void shellRunsWithoutError() {
    def returnCode = consoleShell.startShell()
    assertEquals returnCode, 0
  }

  @Test
  void printJsonPojosTest() {
    def nullString = shell.execute('printJson null')
    assertEquals nullString, "null"

    shell.execute('jsonObj = [name: "John Doe", age: 43]')
    def jsonString = shell.execute('printJson jsonObj')
    assertTrue jsonString.contains('"name" : "John Doe"')
    assertTrue jsonString.contains('"age" : 43')
  }

  @Test
  void printJsonEntitiesTest() {
    shell.execute("import gov.va.vro.persistence.model.*")
    def createClaim="""
      claim = new ClaimEntity()
      claim.setVbmsId('id-123456')
      """.stripMargin()
    shell.execute(createClaim)
    String claimJsonString = shell.execute('printJson claim')
    assertTrue claimJsonString.contains('"vbmsId" : "id-123456"')
    assertTrue claimJsonString.contains('"veteran" : null')
    assertTrue claimJsonString.contains('"contentions" : [ ]')

    def setVeteran="""
      veteran = new VeteranEntity()
      veteran.icn = '987654321'
      veteran.participantId = '333-4444-55555'
      claim.setVeteran(veteran)
      """.stripMargin()
    shell.execute(setVeteran)
    String claimVetJsonString = shell.execute('printJson claim')
    assertTrue claimVetJsonString.contains('"veteran" : {')
    assertTrue claimVetJsonString.contains('"icn" : "987654321"')
    assertTrue claimVetJsonString.contains('"participantId" : "333-4444-55555"')

    def setContention="""
      contention = new ContentionEntity()
      contention.diagnosticCode = '7101'
      claim.addContention(contention)
      """.stripMargin()
    shell.execute(setContention)
    String claimContentionJsonString = shell.execute('printJson claim')
    assertTrue claimContentionJsonString.contains('"contentions" : [ {')
    assertTrue claimContentionJsonString.contains('"diagnosticCode" : "7101"')

    String contentionJsonString = shell.execute('printJson claim.contentions[0]')
    assertTrue contentionJsonString.contains('"diagnosticCode" : "7101"')
    assertTrue contentionJsonString.contains('"claim" : {')
    assertTrue contentionJsonString.contains('"vbmsId" : "id-123456"')
  }
}
