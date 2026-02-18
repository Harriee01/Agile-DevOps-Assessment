package com.teenread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the TeenRead Hub application.
 *
 * @SpringBootApplication enables:
 *   - @Configuration  (this class is a config source)
 *   - @EnableAutoConfiguration (Spring Boot auto-wires web, JSON, etc.)
 *   - @ComponentScan  (scans com.teenread.* for beans)
 */
@SpringBootApplication

public class TeenReadApplication {

    public static void main(String[] args) {
        // Bootstraps the Spring context and starts the embedded Tomcat server
        SpringApplication.run(TeenReadApplication.class, args);
    }


}
