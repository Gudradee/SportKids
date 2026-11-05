package br.com.ibmec.brinquedos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BrinquedoLoteRequest(
        @NotEmpty(message = "Informe ao menos um brinquedo")
        @Size(max = 50, message = "Envie no máximo 50 brinquedos por lote")
        List<@Valid BrinquedoRequest> brinquedos) {
}

/*
 * RESUMO: Este record e o DTO de entrada do cadastro em lote (POST /brinquedos/lote). Ele recebe do
 * Postman um JSON no formato {"brinquedos": [ {...}, {...} ]}, ou seja, uma LISTA de brinquedos, seguindo a
 * sugestao do enunciado de juntar os dados em uma lista no programa antes de envia-los ao banco. As
 * validacoes garantem que a lista nao esteja vazia, que tenha no maximo 50 itens (limite que protege o
 * servidor contra requisicoes abusivamente grandes) e, gracas ao @Valid dentro do tipo generico, que CADA
 * item da lista passe pelas mesmas regras de Bean Validation do BrinquedoRequest.
 */
