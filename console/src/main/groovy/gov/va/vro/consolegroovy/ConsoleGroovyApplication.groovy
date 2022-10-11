package gov.va.vro.consolegroovy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.ClassPathResource

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootApplication
class ConsoleGroovyApplication /*implements CommandLineRunner*/ {

	// https://github.com/spring-projects/spring-boot/issues/7161#issuecomment-253469772
    public static void createFS() throws IOException {
		def userDir = System.getProperty("user.dir")
		System.out.println("Working Directory = " + userDir);
        def resource = new ClassPathResource("file://"+userDir+"/file.groovy");
        URI uri = resource.getURI();
        System.out.println(uri);
		FileSystems.newFileSystem(uri, Collections.emptyMap());
		Path path = Paths.get(uri);
        System.out.println(path);
    }

	static void main(String[] args) {
		SpringApplication.run(ConsoleGroovyApplication, args)
        System.out.println("SprintApp started");
	}

}
