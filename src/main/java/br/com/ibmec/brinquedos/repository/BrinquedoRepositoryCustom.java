package br.com.ibmec.brinquedos.repository;

import br.com.ibmec.brinquedos.model.Brinquedo;

import java.math.BigDecimal;
import java.util.List;

public interface BrinquedoRepositoryCustom {
    List<Brinquedo> buscarPorFaixaDePreco(BigDecimal min, BigDecimal max);
}

/*
 * RESUMO: Esta interface declara as consultas "customizadas" do repositorio de brinquedos, ou seja,
 * consultas que NAO sao geradas automaticamente pelo Spring Data e que escrevemos a mao usando o
 * EntityManager do JPA. Ela segue o padrao de fragmentos do Spring Data: o BrinquedoRepository estende
 * esta interface junto com JpaRepository, e o Spring procura sozinho uma classe chamada
 * BrinquedoRepositoryImpl (nome da interface principal + sufixo "Impl") para fornecer a implementacao.
 * Assim, quem usa o repositorio enxerga em um so lugar tanto os metodos prontos do JpaRepository quanto
 * este metodo escrito manualmente.
 */
