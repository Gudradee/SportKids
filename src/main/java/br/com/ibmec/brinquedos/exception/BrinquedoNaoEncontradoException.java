package br.com.ibmec.brinquedos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BrinquedoNaoEncontradoException extends RuntimeException {
    public BrinquedoNaoEncontradoException(Long id) {
        super("Brinquedo com id " + id + " não encontrado");
    }
}

/*
 * RESUMO: Esta e uma excecao personalizada de dominio (unchecked, pois estende RuntimeException) lancada
 * pela camada de servico sempre que se tenta consultar, atualizar ou excluir um brinquedo cujo id nao
 * existe no banco. Ter uma excecao com nome de negocio deixa o codigo mais legivel do que retornar null ou
 * Optional vazio em todo lugar. Na API REST ela e convertida pelo GlobalExceptionHandler em uma resposta
 * JSON 404; nas paginas web, a anotacao @ResponseStatus(NOT_FOUND) faz o Spring Boot exibir a pagina de
 * erro templates/error/404.html.
 */
