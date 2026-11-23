package br.edu.fatecpg.projetospring.repository;

import br.edu.fatecpg.projetospring.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNome(String nome);

    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.transacoes WHERE c.id = :id")
    Optional<Conta> findByIdWithTransacoes(@Param("id") Long id);

    boolean existsByNome(String nome);
}