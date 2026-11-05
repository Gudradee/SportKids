package br.com.ibmec.brinquedos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String mensagem,
        String caminho,
        Map<String, String> erros) {
    public static ErroResponse of(int status, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status, mensagem, caminho, Map.of());
    }

    public static ErroResponse of(int status, String mensagem, String caminho, Map<String, String> erros) {
        return new ErroResponse(LocalDateTime.now(), status, mensagem, caminho, erros);
    }
}

/*
 * RESUMO: Este record define o formato PADRAO de todas as respostas de erro da API REST, em JSON. Ele
 * contem o momento do erro (timestamp), o codigo HTTP (status), uma mensagem amigavel em portugues, o
 * caminho da requisicao que falhou e, quando for erro de validacao, um mapa "campo -> mensagem" com cada
 * regra violada (por exemplo, "classificacao": "A empresa atende criancas de ate 15 anos"). A anotacao
 * @JsonInclude(NON_EMPTY) omite o mapa de erros quando ele estiver vazio, deixando o JSON mais limpo. Ter um
 * formato unico facilita o consumo da API e evita vazar mensagens internas do servidor.
 */
