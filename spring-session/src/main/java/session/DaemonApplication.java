package session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @author sh */
@SpringBootApplication
public class DaemonApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DaemonApplication.class);
        application.run(args);
    }
}
