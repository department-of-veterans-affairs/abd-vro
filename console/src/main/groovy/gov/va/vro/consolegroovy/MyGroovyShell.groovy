package gov.va.vro.consolegroovy

import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.groovy.groovysh.Groovysh
import org.apache.groovy.groovysh.util.PackageHelper
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.util.Preferences
import org.springframework.context.event.EventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.stereotype.Service

@Service
class MyGroovyShell {
	@Autowired
	CamelContext camelContext;

	@Autowired
	ProducerTemplate producerTemplate;

	String submitSeda() {
		return producerTemplate.requestBody("seda:foo", "Hello", String.class);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
//		def shell = new MyGroovyShell();
//		shell.camelContext = camelContext
//		shell.producerTemplate = producerTemplate
		startShell()
	}

    void startShell(){
		def userDir = System.getProperty("user.dir")
		System.out.println("Working Directory = " + userDir);
//		createFS()

//		println "args: $args"
//
//		def ccl=Thread.currentThread().contextClassLoader
//		ccl.getDefinedPackages().each {
//			println "p ${it}"
//		}
//		new PackageHelperImpl(ccl)

		def block=1
		if(block==1) {

			Binding binding=new Binding([
				name : "Albert",
				camel: camelContext,
				pt: producerTemplate,
				me: this
			])
			def shell=createGroovysh(binding)
			shell.run("");
			println "============= DONE"
		} else if(block==2){
			runGroovyshThread()
		}

//		Thread.sleep(100000)
		println 'Exiting'
	}

	static void runGroovyshThread(){
		def shell=createFS()
		Runnable runnable = () -> {
//			  try {
//				SshTerminal.registerEnvironment(env);
			shell.run("");
//				callback.onExit(0);
//			  } catch (RuntimeException | Error e) {
//				callback.onExit(-1, e.getMessage());
//			  }
			println "============= DONE thread"
		};
		def threadName="GRROOOOOVY"
		def wrapper = new Thread(runnable, threadName);
		wrapper.start();
	}

	static createGroovysh(Binding binding = null){
		IO io = new IO(System.in, System.out, System.err);
//		io.setVerbosity(IO.Verbosity.DEBUG);

		// workaround so that `java -jar build/libs/console-groovy-0.0.1-SNAPSHOT.jar` works
        Preferences.put(PackageHelper.IMPORT_COMPLETION_PREFERENCE_KEY, "true")

		def shell = new Groovysh(binding, io)
	}

//	@Override
	void run(String... args) throws Exception {
		def shell=createFS()
		shell.run("");
		println "============= DONE"
	}
}
