package org.goblintelligence.pulseboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class PulseBoardApplicationTests {

    @Test
    void contextLoads() {
    }
}
