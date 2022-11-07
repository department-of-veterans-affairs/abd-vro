package gov.va.vro.consolegroovy

class VroConsoleShellTest extends spock.lang.Specification {
  void setup() {
  }

  def "StartShell"() {
    given:
    def shell = new VroConsoleShell()

    when:
    def binding = new Binding()
    def groovyShell = shell.createGroovysh(binding)

    then:
    groovyShell.getProperties() != null
  }
}
