package com.caderneta;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(
        locations = {"classpath:application-test.properties"}
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@WireMockTest(httpPort = 8080)
public abstract class AbstractTest {

    @LocalServerPort
    private int port;

    public AbstractTest() {}

    static {
        ClassLoader.getSystemClassLoader().setPackageAssertionStatus("org.postgresql.Driver", false);
    }

}
