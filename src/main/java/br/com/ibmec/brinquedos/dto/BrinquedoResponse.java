package br.com.ibmec.brinquedos.dto;

import br.com.ibmec.brinquedos.model.Brinquedo;

import java.math.BigDecimal;

public record BrinquedoResponse(
        Long id,
        String nome,
        String tipo,
        Integer classificacao,
        String tamanho,
        BigDecimal preco) {
    public static BrinquedoResponse de(Brinquedo brinquedo) {
        return new BrinquedoResponse(
                brinquedo.getId(),
                brinquedo.getNome(),
                brinquedo.getTipo(),
                brinquedo.getClassificacao(),
                brinquedo.getTamanho(),
                brinquedo.getPreco());
    }
}

/*
 * RESUMO: Este record e um DTO de SAIDA: define exatamente quais informacoes de um brinquedo sao devolvidas
 * ao cliente (Postman ou paginas web) — id, nome, tipo, classificacao, tamanho e preco. Um "record" do
 * Java e uma classe imutavel e enxuta: o compilador gera automaticamente construtor, metodos de acesso
 * (id(), nome()...), equals, hashCode e toString. O metodo estatico de(...) faz a conversao da entidade
 * Brinquedo para este formato. Usar um DTO de resposta, em vez de devolver a entidade diretamente, evita
 * expor detalhes internos de persistencia e permite mudar o banco sem quebrar o contrato da API.
 */
