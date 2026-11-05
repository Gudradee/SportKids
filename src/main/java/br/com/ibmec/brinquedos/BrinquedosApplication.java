package br.com.ibmec.brinquedos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrinquedosApplication {
	public static void main(String[] args) {
		SpringApplication.run(BrinquedosApplication.class, args);
	}

}

/*
 * RESUMO: Esta e a classe principal (ponto de entrada) da aplicacao Spring Boot. O metodo main chama
 * SpringApplication.run, que inicializa todo o framework: sobe o servidor Tomcat embutido na porta 8080,
 * cria a conexao com o banco H2, gera a tabela a partir das entidades JPA e registra todos os componentes
 * do projeto. A anotacao @SpringBootApplication combina tres outras: @Configuration (a classe pode declarar
 * beans), @EnableAutoConfiguration (o Spring configura sozinho o que encontrar no classpath, como JPA,
 * Security e Thymeleaf) e @ComponentScan (procura classes anotadas com @Controller, @Service, @Component
 * etc. neste pacote, br.com.ibmec.brinquedos, e em todos os subpacotes). Por isso esta classe deve ficar
 * no pacote raiz, acima de config, controller, dto, exception, model, repository e service.
 */
