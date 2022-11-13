package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
import io.lettuce.core.api.sync.RedisCommands
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.groovy.groovysh.Groovysh
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.spy

@ExtendWith(MockitoExtension)
class VroConsoleShellMockedTests {

  @Mock
  CamelContext camelContext

  ObjectMapper objectMapper = new ObjectMapper()

  @Mock
  ProducerTemplate producerTemplate

  @Mock
  ClaimRepository claimRepository

  @Mock
  VeteranRepository veteranRepository

  @Mock
  RedisCommands redisCommands

  @Mock
  LettuceConnectionFactory lettuceConnectionFactory

  VroConsoleShell consoleShell
  Groovysh shell

  @BeforeEach
  void setup(){
    consoleShell = spy(new VroConsoleShell(camelContext, objectMapper, producerTemplate, claimRepository, veteranRepository, lettuceConnectionFactory))
    doReturn(redisCommands).when(consoleShell).redisConnection()

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
      claim.setClaimSubmissionId('id-123456')
      claim.setIdType('va.gov-Form526Submission')
      """.stripMargin()
    println createClaim
    shell.execute(createClaim)
    def claimJsonString = shell.execute('printJson claim')
    assertTrue claimJsonString.contains('"claimSubmissionId" : "id-123456"')
    assertTrue claimJsonString.contains('"idType" : "va.gov-Form526Submission"')
    assertTrue claimJsonString.contains('"veteran" : null')
    assertTrue claimJsonString.contains('"contentions" : [ ]')

    def setVeteran="""
      veteran = new VeteranEntity()
      veteran.icn = '987654321'
      veteran.participantId = '333-4444-55555'
      claim.setVeteran(veteran)
      """.stripMargin()
    shell.execute(setVeteran)
    def claimVetJsonString = shell.execute('printJson claim')
    assertTrue claimVetJsonString.contains('"veteran" : {')
    assertTrue claimVetJsonString.contains('"icn" : "987654321"')
    assertTrue claimVetJsonString.contains('"participantId" : "333-4444-55555"')

    def setContention="""
      contention = new ContentionEntity()
      contention.diagnosticCode = '7101'
      claim.addContention(contention)
      """.stripMargin()
    shell.execute(setContention)
    def claimContentionJsonString = shell.execute('printJson claim')
    assertTrue claimContentionJsonString.contains('"contentions" : [ {')
    assertTrue claimContentionJsonString.contains('"diagnosticCode" : "7101"')

    def contentionJsonString = shell.execute('printJson claim.contentions[0]')
    assertTrue contentionJsonString.contains('"diagnosticCode" : "7101"')
    assertTrue contentionJsonString.contains('"claim" : {')
    assertTrue contentionJsonString.contains('"claimSubmissionId" : "id-123456"')
  }
}
