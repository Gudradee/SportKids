package br.com.ibmec.brinquedos.repository;

import br.com.ibmec.brinquedos.model.Brinquedo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

class BrinquedoRepositoryImpl implements BrinquedoRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Brinquedo> buscarPorFaixaDePreco(BigDecimal min, BigDecimal max) {
        String jpql = "SELECT b FROM Brinquedo b WHERE b.preco BETWEEN :min AND :max ORDER BY b.preco";
        TypedQuery<Brinquedo> query = entityManager.createQuery(jpql, Brinquedo.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }
}

