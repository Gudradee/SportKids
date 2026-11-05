package br.com.ibmec.brinquedos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BrinquedoRequest {
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O tipo é obrigatório")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres")
    private String tipo;

    @NotNull(message = "A classificação é obrigatória")
    @Min(value = 0, message = "A classificação mínima é 0 anos")
    @Max(value = 15, message = "A empresa atende crianças de até 15 anos")
    private Integer classificacao;

    @NotBlank(message = "O tamanho é obrigatório")
    @Pattern(regexp = "P|M|G|UNICO", message = "O tamanho deve ser P, M, G ou UNICO")
    private String tamanho;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 8 dígitos inteiros e 2 casas decimais")
    private BigDecimal preco;

    public BrinquedoRequest() {
    }

    public BrinquedoRequest(String nome, String tipo, Integer classificacao, String tamanho, BigDecimal preco) {
        this.nome = nome;
        this.tipo = tipo;
        this.classificacao = classificacao;
        this.tamanho = tamanho;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Integer classificacao) {
        this.classificacao = classificacao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}

/*
 * RESUMO: Esta classe e um DTO (Data Transfer Object) de ENTRADA: representa os dados que o cliente envia
 * para criar ou atualizar um brinquedo, seja como JSON no corpo de um POST/PUT feito pelo Postman, seja
 * pelo formulario HTML do Thymeleaf. Ela carrega as anotacoes de Bean Validation que sao verificadas
 * automaticamente quando o controller usa @Valid, gerando erro 400 com mensagens claras em caso de dados
 * invalidos. Repare que ela NAO possui o campo id: assim o cliente nunca consegue escolher ou trocar a
 * chave primaria de um registro (protecao contra "mass assignment"), e a entidade JPA fica desacoplada
 * do formato exposto na API. Os getters e setters existem porque o Jackson (JSON) e o Thymeleaf
 * (formulario) preenchem o objeto atraves deles.
 */
