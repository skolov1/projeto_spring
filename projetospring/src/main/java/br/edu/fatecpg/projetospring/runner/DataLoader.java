package br.edu.fatecpg.projetospring.runner;

import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.repository.ContaRepository;
import br.edu.fatecpg.projetospring.repository.TransacaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public DataLoader(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public void run(String... args) {
        if (contaRepository.count() > 0) {
            return; // Já existem dados
        }

        // Conta 1: Conta Corrente
        Conta contaCorrente = new Conta("Conta Corrente", new BigDecimal("1000.00"));
        contaRepository.save(contaCorrente);

        // Transações da Conta Corrente
        Transacao t1 = new Transacao("Salário Janeiro", new BigDecimal("5000.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 5), "Salário");
        Transacao t2 = new Transacao("Aluguel Janeiro", new BigDecimal("1500.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 10), "Moradia");
        Transacao t3 = new Transacao("Supermercado", new BigDecimal("800.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 15), "Alimentação");
        Transacao t4 = new Transacao("Freelance", new BigDecimal("1200.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 20), "Trabalho Extra");
        Transacao t5 = new Transacao("Internet", new BigDecimal("120.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 25), "Serviços");
        Transacao t6 = new Transacao("Transporte", new BigDecimal("300.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 28), "Transporte");

        contaCorrente.adicionarTransacao(t1);
        contaCorrente.adicionarTransacao(t2);
        contaCorrente.adicionarTransacao(t3);
        contaCorrente.adicionarTransacao(t4);
        contaCorrente.adicionarTransacao(t5);
        contaCorrente.adicionarTransacao(t6);

        transacaoRepository.saveAll(java.util.List.of(t1, t2, t3, t4, t5, t6));

        // Conta 2: Poupança
        Conta poupanca = new Conta("Poupança", new BigDecimal("5000.00"));
        contaRepository.save(poupanca);

        Transacao t7 = new Transacao("Depósito Inicial", new BigDecimal("2000.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 1), "Investimento");
        Transacao t8 = new Transacao("Rendimento Janeiro", new BigDecimal("50.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 31), "Investimento");

        poupanca.adicionarTransacao(t7);
        poupanca.adicionarTransacao(t8);

        transacaoRepository.saveAll(java.util.List.of(t7, t8));

        // Conta 3: Cartão de Crédito
        Conta cartao = new Conta("Cartão de Crédito", BigDecimal.ZERO);
        contaRepository.save(cartao);

        Transacao t9 = new Transacao("Compra Amazon", new BigDecimal("350.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 12), "Compras");
        Transacao t10 = new Transacao("Uber", new BigDecimal("85.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 18), "Transporte");
        Transacao t11 = new Transacao("Pagamento Fatura", new BigDecimal("1000.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 28), "Pagamento");

        cartao.adicionarTransacao(t9);
        cartao.adicionarTransacao(t10);
        cartao.adicionarTransacao(t11);

        transacaoRepository.saveAll(java.util.List.of(t9, t10, t11));

        System.out.println("=== Dados de exemplo carregados ===");
        System.out.println("Contas criadas: " + contaRepository.count());
        System.out.println("Transações criadas: " + transacaoRepository.count());
    }
}