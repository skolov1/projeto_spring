package br.edu.fatecpg.projetospring.repository;

import br.edu.fatecpg.projetospring.model.entity.Conta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ContaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Deve salvar e buscar conta por ID")
    void deveSalvarEBucarContaPorId() {
        Conta conta = new Conta("Conta Teste", new BigDecimal("1000.00"));
        Conta salva = entityManager.persistAndFlush(conta);

        Optional<Conta> encontrada = contaRepository.findById(salva.getId());

        assertTrue(encontrada.isPresent());
        assertEquals("Conta Teste", encontrada.get().getNome());
        assertEquals(new BigDecimal("1000.00"), encontrada.get().getSaldoInicial());
    }

    @Test
    @DisplayName("Deve buscar conta por nome")
    void deveBuscarContaPorNome() {
        Conta conta = new Conta("Minha Conta", new BigDecimal("500.00"));
        entityManager.persistAndFlush(conta);

        Optional<Conta> encontrada = contaRepository.findByNome("Minha Conta");

        assertTrue(encontrada.isPresent());
        assertEquals("Minha Conta", encontrada.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar empty ao buscar nome inexistente")
    void deveRetornarEmptyAoBuscarNomeInexistente() {
        Optional<Conta> encontrada = contaRepository.findByNome("Inexistente");

        assertTrue(encontrada.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar existência por nome")
    void deveVerificarExistenciaPorNome() {
        Conta conta = new Conta("Conta Existente", new BigDecimal("100.00"));
        entityManager.persistAndFlush(conta);

        boolean existe = contaRepository.existsByNome("Conta Existente");
        boolean naoExiste = contaRepository.existsByNome("Inexistente");

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve buscar conta com transações (fetch join)")
    void deveBuscarContaComTransacoes() {
        Conta conta = new Conta("Conta com Transações", new BigDecimal("1000.00"));
        entityManager.persistAndFlush(conta);

        // Buscar com fetch join
        Optional<Conta> encontrada = contaRepository.findByIdWithTransacoes(conta.getId());

        assertTrue(encontrada.isPresent());
        // O fetch join deve carregar a coleção
        assertNotNull(encontrada.get().getTransacoes());
    }

    @Test
    @DisplayName("Deve listar todas as contas")
    void deveListarTodasContas() {
        entityManager.persistAndFlush(new Conta("Conta 1", new BigDecimal("100")));
        entityManager.persistAndFlush(new Conta("Conta 2", new BigDecimal("200")));
        entityManager.persistAndFlush(new Conta("Conta 3", new BigDecimal("300")));

        var contas = contaRepository.findAll();

        assertEquals(3, contas.size());
    }
}