package br.com.ibmec.brinquedos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BrinquedosApplicationTests {
	@Test
	void contextLoads() {
	}

}

/*
 * RESUMO: Este e o teste "smoke" gerado pelo Spring Initializr. O metodo contextLoads nao tem corpo de
 * proposito: a anotacao @SpringBootTest tenta subir todo o contexto da aplicacao (beans, banco H2, JPA,
 * seguranca, templates) e, se qualquer configuracao estiver quebrada, o teste falha. E a verificacao mais
 * basica de que o projeto inicializa corretamente.
 */
