package com.teenread;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifies the Spring application context loads without errors.
 * If any bean is misconfigured, this test fails and the CI pipeline is blocked.
 */
@SpringBootTest

public class TeenReadApplicationTests {
    /**
     * If this test method passes it means:
     * - All @Component / @Service / @Repository / @RestController beans were found
     * - Constructor injections resolved without circular dependencies
     * - application.properties was parsed correctly
     */
    @Test
    void contextLoads() {
        // No assertions needed – the test fails automatically if context startup throws
    }
}
