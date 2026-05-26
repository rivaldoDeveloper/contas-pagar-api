package com.sistema.contas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Garante que o application-test.yml (ou application.yml em test/resources) seja carregado
class ContasApplicationTests {

	@Test
	void contextLoads() {
	}

}
