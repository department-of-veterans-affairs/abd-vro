package gov.va.vro.services.bie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class BieKafkaApplication {
    @Bean
    public CountDownLatch shutdownLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String[] args) throws InterruptedException {
        var ctx = SpringApplication.run(BieKafkaApplication.class, args);

        // Keep this application running
        final CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(() -> closeLatch.countDown()));
        closeLatch.await();
    }

}
