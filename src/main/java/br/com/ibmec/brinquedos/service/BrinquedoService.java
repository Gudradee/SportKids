package br.com.ibmec.brinquedos.service;

import br.com.ibmec.brinquedos.dto.BrinquedoRequest;
import br.com.ibmec.brinquedos.dto.BrinquedoResponse;
import br.com.ibmec.brinquedos.exception.BrinquedoNaoEncontradoException;
import br.com.ibmec.brinquedos.model.Brinquedo;
import br.com.ibmec.brinquedos.repository.BrinquedoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BrinquedoService {
    private final BrinquedoRepository repository;

    public BrinquedoService(BrinquedoRepository repository) {
        this.repository = repository;
    }

    public List<BrinquedoResponse> listar(String tipo, Integer idade, String nome) {
        List<Brinquedo> encontrados;
        if (temTexto(tipo)) {
            encontrados = repository.findByTipoIgnoreCase(tipo.trim());
        } else if (idade != null) {
            encontrados = repository.findByClassificacaoLessThanEqual(idade);
        } else if (temTexto(nome)) {
            encontrados = repository.findByNomeContainingIgnoreCase(nome.trim());
        } else {
            encontrados = repository.findAll(Sort.by("id"));
        }
        return encontrados.stream().map(BrinquedoResponse::de).toList();
    }

    public List<BrinquedoResponse> buscarPorFaixaDePreco(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("O preço mínimo não pode ser maior que o preço máximo");
        }
        return repository.buscarPorFaixaDePreco(min, max).stream().map(BrinquedoResponse::de).toList();
    }

    public BrinquedoResponse buscarPorId(Long id) {
        return BrinquedoResponse.de(buscarEntidade(id));
    }

    public long contar() {
        return repository.count();
    }

    @Transactional
    public BrinquedoResponse criar(BrinquedoRequest dados) {
        Brinquedo novo = paraEntidade(dados);
        return BrinquedoResponse.de(repository.save(novo));
    }

    @Transactional
    public List<BrinquedoResponse> criarEmLote(List<BrinquedoRequest> lote) {
        List<Brinquedo> listaParaGravar = new ArrayList<>();
        for (BrinquedoRequest dados : lote) {
            listaParaGravar.add(paraEntidade(dados));
        }
        return repository.saveAll(listaParaGravar).stream().map(BrinquedoResponse::de).toList();
    }

    @Transactional
    public BrinquedoResponse atualizar(Long id, BrinquedoRequest dados) {
        Brinquedo existente = buscarEntidade(id);
        existente.atualizarDados(
                dados.getNome().trim(),
                dados.getTipo().trim(),
                dados.getClassificacao(),
                dados.getTamanho(),
                dados.getPreco());
        return BrinquedoResponse.de(repository.save(existente));
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new BrinquedoNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

    private Brinquedo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new BrinquedoNaoEncontradoException(id));
    }

    private static Brinquedo paraEntidade(BrinquedoRequest dados) {
        return new Brinquedo(
                dados.getNome().trim(),
                dados.getTipo().trim(),
                dados.getClassificacao(),
                dados.getTamanho(),
                dados.getPreco());
    }

    private static boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}

/*
 * RESUMO: Esta classe e a camada de Servico (@Service) da aplicacao: concentra as regras de negocio do
 * CRUD de brinquedos e fica entre os controllers (que lidam com HTTP) e o repository (que lida com o
 * banco). Ela converte os DTOs de entrada (BrinquedoRequest) em entidades, chama o BrinquedoRepository para
 * persistir, e converte o resultado em DTOs de saida (BrinquedoResponse). A anotacao @Transactional garante
 * que cada operacao de escrita (criar, criar em lote, atualizar, excluir) rode dentro de uma transacao com
 * commit automatico ao final ou rollback em caso de erro; as leituras usam readOnly = true, que e mais
 * eficiente. Quando um id nao existe, lanca BrinquedoNaoEncontradoException. Como a API REST e as paginas
 * Thymeleaf usam este mesmo servico, a regra de negocio fica escrita em um unico lugar.
 */
