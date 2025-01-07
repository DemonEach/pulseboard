package org.goblintelligence.pulseboard;

import org.springframework.boot.SpringApplication;

public class TestPulseBoardApplication {

	public static void main(String[] args) {
		SpringApplication.from(PulseBoardApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
