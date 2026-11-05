package br.com.ibmec.brinquedos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "TDS_TB_Brinquedos")
public class Brinquedo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O tipo é obrigatório")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres")
    @Column(name = "Tipo", nullable = false, length = 50)
    private String tipo;

    @NotNull(message = "A classificação é obrigatória")
    @Min(value = 0, message = "A classificação mínima é 0 anos")
    @Max(value = 15, message = "A empresa atende crianças de até 15 anos")
    @Column(name = "Classificacao", nullable = false)
    private Integer classificacao;

    @NotBlank(message = "O tamanho é obrigatório")
    @Pattern(regexp = "P|M|G|UNICO", message = "O tamanho deve ser P, M, G ou UNICO")
    @Column(name = "Tamanho", nullable = false, length = 10)
    private String tamanho;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 8 dígitos inteiros e 2 casas decimais")
    @Column(name = "Preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    protected Brinquedo() {
    }

    public Brinquedo(String nome, String tipo, Integer classificacao, String tamanho, BigDecimal preco) {
        this.nome = nome;
        this.tipo = tipo;
        this.classificacao = classificacao;
        this.tamanho = tamanho;
        this.preco = preco;
    }

    public void atualizarDados(String nome, String tipo, Integer classificacao, String tamanho, BigDecimal preco) {
        this.nome = nome;
        this.tipo = tipo;
        this.classificacao = classificacao;
        this.tamanho = tamanho;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}

/*
 * RESUMO: Esta classe e a Entidade JPA que representa um brinquedo esportivo e e mapeada para a tabela
 * TDS_TB_Brinquedos do banco H2, com as colunas obrigatorias Id, Nome, Tipo, Classificacao, Tamanho e
 * Preco. As anotacoes do pacote jakarta.persistence (@Entity, @Table, @Id, @Column...) dizem ao Hibernate
 * como transformar o objeto em linha da tabela, enquanto as anotacoes do Bean Validation (@NotBlank,
 * @Size, @Min, @Max, @Pattern, @DecimalMin, @Digits) definem as regras de negocio dos atributos — por
 * exemplo, a classificacao etaria limitada a 15 anos, que e o publico da empresa. Essas validacoes sao
 * verificadas novamente pelo Hibernate antes de gravar (defesa em profundidade). A classe nao tem
 * setters publicos: o id e gerado apenas pelo banco e os demais campos so mudam pelo construtor ou pelo
 * metodo atualizarDados, o que protege a integridade dos dados.
 */
