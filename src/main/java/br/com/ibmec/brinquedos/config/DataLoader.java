package br.com.ibmec.brinquedos.config;

import br.com.ibmec.brinquedos.dto.BrinquedoRequest;
import br.com.ibmec.brinquedos.service.BrinquedoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final BrinquedoService service;

    public DataLoader(BrinquedoService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (service.contar() > 0) {
            return;
        }
        List<BrinquedoRequest> iniciais = List.of(
                new BrinquedoRequest("Bola de Futebol Infantil", "Bola", 5, "M", new BigDecimal("59.90")),
                new BrinquedoRequest("Raquete de Tênis Júnior", "Raquete", 8, "P", new BigDecimal("129.90")),
                new BrinquedoRequest("Patins Ajustável", "Patins", 6, "G", new BigDecimal("249.00")),
                new BrinquedoRequest("Bicicleta Aro 20", "Bicicleta", 7, "UNICO", new BigDecimal("899.00")));
        service.criarEmLote(iniciais);
        log.info("DataLoader: {} brinquedos iniciais cadastrados na tabela TDS_TB_Brinquedos", iniciais.size());
    }
}

/*
 * RESUMO: Esta classe e um componente que roda automaticamente uma unica vez, logo apos a aplicacao
 * terminar de subir, porque implementa a interface ApplicationRunner do Spring Boot. Como o H2 esta
 * configurado em memoria e comeca vazio a cada execucao, ela popula a tabela TDS_TB_Brinquedos com quatro
 * brinquedos de exemplo (bola, raquete, patins e bicicleta), para que o GET /brinquedos ja retorne dados nos
 * testes do Postman e nas paginas web. O cadastro passa pelo BrinquedoService (reaproveitando a mesma
 * regra usada pela API) e so acontece se a tabela estiver vazia, evitando registros duplicados.
 */
