package gov.va.vro.consolegroovy


import org.apache.groovy.groovysh.Command
import org.apache.groovy.groovysh.Groovysh
import org.apache.groovy.groovysh.util.PackageHelper
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.util.Preferences
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

import javax.annotation.Resource

@Service
@groovy.transform.TupleConstructor
class VroConsoleShell {
  @Resource
  Closure<List<Command>> vroConsoleCommandsFactory

  @Autowired
  final DatabaseConnection db

  @Autowired
  final CamelConnection camel

  @Autowired
  final RedisConnection redis

  @EventListener(ApplicationReadyEvent)
  int startShell() {
    def userDir = System.getProperty("user.dir")
    println("Working Directory = " + userDir)

    def shell = setupVroShell()

    def returnCode = shell.run("")
    println 'Exiting'
    returnCode
  }

  Groovysh setupVroShell(){
    def shell = createGroovysh(getBinding())
    vroConsoleCommandsFactory(shell, this).each { Command cmd ->
      shell.register(cmd)
    }
    shell
  }

  def getBinding() {
    new Binding([
      redis: redis.redisCommands,
      redisT: redis.redisTemplate,
      claimsT: db.claimRepository,
      vetT   : db.veteranRepository,
      camel  : camel.camelContext,
      pt     : camel.producerTemplate
    ])
  }

  def createGroovysh(Binding binding = null) {
    IO io = new IO(System.in, System.out, System.err)
    // io.setVerbosity(IO.Verbosity.DEBUG)

    // workaround so that `java -jar ...` works
    Preferences.put(PackageHelper.IMPORT_COMPLETION_PREFERENCE_KEY, "true")

    new Groovysh(binding, io)
  }
}
