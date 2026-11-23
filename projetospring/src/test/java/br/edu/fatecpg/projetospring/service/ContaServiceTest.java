package br.edu.fatecpg.projetospring.service;

import br.edu.fatecpg.projetospring.dto.request.ContaRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.ContaResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.SaldoResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.ResumoFinanceiroResponseDTO;
import br.edu.fatecpg.projetospring.exception.ContaNaoEncontradaException;
import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.model.projection.ResumoFinanceiro;
import br.edu.fatecpg.projetospring.repository.ContaRepository;
import br.edu.fatecpg.projetospring.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;
    private ContaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        conta = new Conta("Conta Teste", new BigDecimal("1000.00"));
        // Set ID via reflection since it's generated
        try {
            java.lang.reflect.Field idField = Conta.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(conta, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        requestDTO = new ContaRequestDTO("Nova Conta", new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Deve listar todas as contas")
    void deveListarTodasContas() {
        when(contaRepository.findAll()).thenReturn(List.of(conta));

        List<ContaResponseDTO> result = contaService.listarTodas();

        assertEquals(1, result.size());
        assertEquals("Conta Teste", result.get(0).getNome());
        verify(contaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar conta por ID")
    void deveBuscarContaPorId() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponseDTO result = contaService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals("Conta Teste", result.getNome());
        assertEquals(new BigDecimal("1000.00"), result.getSaldoInicial());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar conta inexistente")
    void deveLancarExcecaoAoBuscarContaInexistente() {
        when(contaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> contaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve criar nova conta")
    void deveCriarNovaConta() {
        when(contaRepository.existsByNome("Nova Conta")).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta c = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = Conta.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(c, 2L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return c;
        });

        ContaResponseDTO result = contaService.criar(requestDTO);

        assertNotNull(result);
        assertEquals("Nova Conta", result.getNome());
        assertEquals(new BigDecimal("500.00"), result.getSaldoInicial());
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar conta com nome duplicado")
    void deveLancarExcecaoAoCriarContaComNomeDuplicado() {
        when(contaRepository.existsByNome("Nova Conta")).thenReturn(true);

        assertThrows(br.edu.fatecpg.projetospring.exception.BusinessException.class,
                () -> contaService.criar(requestDTO));
    }

    @Test
    @DisplayName("Deve calcular saldo da conta")
    void deveCalcularSaldoDaConta() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        SaldoResponseDTO result = contaService.calcularSaldo(1L);

        assertNotNull(result);
        assertEquals(1L, result.getContaId());
        assertEquals("Conta Teste", result.getContaNome());
        assertEquals(new BigDecimal("1000.00"), result.getSaldoInicial());
    }

    @Test
    @DisplayName("Deve obter resumo financeiro via query nativa")
    void deveObterResumoFinanceiro() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ResumoFinanceiro resumoMock = mock(ResumoFinanceiro.class);
        when(resumoMock.getReceitas()).thenReturn(new BigDecimal("6200.00"));
        when(resumoMock.getDespesas()).thenReturn(new BigDecimal("2300.00"));
        when(transacaoRepository.somarReceitasEDespesas(1L)).thenReturn(resumoMock);

        ResumoFinanceiroResponseDTO result = contaService.obterResumo(1L);

        assertNotNull(result);
        assertEquals(1L, result.getContaId());
        assertEquals("Conta Teste", result.getContaNome());
        assertEquals(new BigDecimal("6200.00"), result.getReceitas());
        assertEquals(new BigDecimal("2300.00"), result.getDespesas());
    }
}