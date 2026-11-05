package br.com.ibmec.brinquedos.controller;

import br.com.ibmec.brinquedos.dto.BrinquedoLoteRequest;
import br.com.ibmec.brinquedos.dto.BrinquedoRequest;
import br.com.ibmec.brinquedos.dto.BrinquedoResponse;
import br.com.ibmec.brinquedos.service.BrinquedoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/brinquedos")
public class BrinquedoController {
    private final BrinquedoService service;

    public BrinquedoController(BrinquedoService service) {
        this.service = service;
    }

    @GetMapping
    public List<BrinquedoResponse> listar(@RequestParam(required = false) String tipo,
                                          @RequestParam(required = false) Integer idade,
                                          @RequestParam(required = false) String nome) {
        return service.listar(tipo, idade, nome);
    }

    @GetMapping("/faixa-preco")
    public List<BrinquedoResponse> buscarPorFaixaDePreco(@RequestParam BigDecimal min,
                                                         @RequestParam BigDecimal max) {
        return service.buscarPorFaixaDePreco(min, max);
    }

    @GetMapping("/{id}")
    public BrinquedoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<BrinquedoResponse> criar(@Valid @RequestBody BrinquedoRequest dados) {
        BrinquedoResponse salvo = service.criar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.id()).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @PostMapping("/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BrinquedoResponse> criarEmLote(@Valid @RequestBody BrinquedoLoteRequest lote) {
        return service.criarEmLote(lote.brinquedos());
    }

    @PutMapping("/{id}")
    public BrinquedoResponse atualizar(@PathVariable Long id, @Valid @RequestBody BrinquedoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

/*
 * RESUMO: Esta classe e o Controller REST (@RestController) que expoe o CRUD de brinquedos em JSON no
 * endpoint /brinquedos, para ser testado pelo Postman ou Insomnia em http://localhost:8080. Cada metodo e
 * ligado a um verbo HTTP: GET lista todos (com filtros opcionais por tipo, idade ou nome) ou busca por id;
 * GET /brinquedos/faixa-preco filtra por preco usando uma consulta feita com o EntityManager;
 * POST cria um brinquedo (ou varios, em /brinquedos/lote) e devolve 201 Created com o header Location;
 * PUT atualiza pelo id; DELETE exclui pelo id e devolve 204 No Content. A anotacao @Valid dispara o Bean
 * Validation sobre o JSON recebido antes de qualquer gravacao. O controller nao contem regra de negocio
 * nem acessa o banco diretamente: ele apenas traduz HTTP <-> chamadas ao BrinquedoService. Erros (404,
 * 400) sao convertidos em JSON pelo GlobalExceptionHandler, e as permissoes (USER le, ADMIN altera) sao
 * aplicadas antes, pelo Spring Security.
 */
