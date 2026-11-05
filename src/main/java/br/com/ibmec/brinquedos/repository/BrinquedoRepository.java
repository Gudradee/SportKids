package br.com.ibmec.brinquedos.repository;

import br.com.ibmec.brinquedos.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrinquedoRepository extends JpaRepository<Brinquedo, Long>, BrinquedoRepositoryCustom {
    List<Brinquedo> findByTipoIgnoreCase(String tipo);

    List<Brinquedo> findByClassificacaoLessThanEqual(Integer idade);

    List<Brinquedo> findByNomeContainingIgnoreCase(String nome);
}
