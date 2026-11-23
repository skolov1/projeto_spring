package br.edu.fatecpg.projetospring.repository;

import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.model.projection.DespesaPorCategoria;
import br.edu.fatecpg.projetospring.model.projection.ResumoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query(value = """
            SELECT
                COALESCE(SUM(CASE WHEN tipo = 'RECEITA' THEN valor END), 0) AS receitas,
                COALESCE(SUM(CASE WHEN tipo = 'DESPESA' THEN valor END), 0) AS despesas
            FROM transacao WHERE conta_id = :contaId
            """, nativeQuery = true)
    ResumoFinanceiro somarReceitasEDespesas(@Param("contaId") Long contaId);

    Optional<Transacao> findByIdAndContaId(Long id, Long contaId);

    List<Transacao> findByTipo(TipoTransacao tipo);

    List<Transacao> findByDescricaoContainingIgnoreCase(String descricao);

    List<Transacao> findByDataBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT t FROM Transacao t ORDER BY t.data DESC, t.id DESC")
    List<Transacao> listarOrdenadasPorData();

    // JPQL: total por categoria de um tipo (usado no gráfico de despesas por categoria)
    @Query("""
            SELECT COALESCE(t.categoria, 'Sem categoria') AS categoria, SUM(t.valor) AS total
            FROM Transacao t
            WHERE t.conta.id = :contaId AND t.tipo = :tipo
            GROUP BY COALESCE(t.categoria, 'Sem categoria')
            ORDER BY SUM(t.valor) DESC
            """)
    List<DespesaPorCategoria> somarPorCategoria(@Param("contaId") Long contaId, @Param("tipo") TipoTransacao tipo);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId ORDER BY t.data DESC, t.id DESC")
    List<Transacao> findByContaIdOrderByDataDesc(@Param("contaId") Long contaId);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId AND t.tipo = :tipo ORDER BY t.data DESC")
    List<Transacao> findByContaIdAndTipo(@Param("contaId") Long contaId, @Param("tipo") TipoTransacao tipo);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId AND t.descricao ILIKE %:descricao% ORDER BY t.data DESC")
    List<Transacao> findByContaIdAndDescricaoContaining(@Param("contaId") Long contaId, @Param("descricao") String descricao);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId AND t.data BETWEEN :inicio AND :fim ORDER BY t.data DESC")
    List<Transacao> findByContaIdAndDataBetween(@Param("contaId") Long contaId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId AND t.tipo = :tipo AND t.data BETWEEN :inicio AND :fim ORDER BY t.data DESC")
    List<Transacao> findByContaIdAndTipoAndDataBetween(@Param("contaId") Long contaId, @Param("tipo") TipoTransacao tipo, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}