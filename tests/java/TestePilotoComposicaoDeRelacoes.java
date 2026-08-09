import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Composição de Relações — categoria
 * "Relações" de Vergnaud, não "Medidas": os três papéis (Relacao1,
 * Relacao2, RelacaoFinal) são todos INTEIROS, sem restrição de sinal — por
 * isso, diferente dos harnesses de Composição/Transformação/Comparação de
 * Medidas, não há cenário de rejeição por domínio aqui. Mesmo padrão dos
 * outros harnesses do piloto quanto ao resto.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoComposicaoDeRelacoes {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalComposicaoDeRelacoes relacao = RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-6", "usuario-local-1", "tentativa-6",
                "situacao-composicao-relacoes-menos6-mais11-5", "diagrama-vergnaud-6");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("=== Testes: papéis começam como incógnita ===");
        checar("Relacao1 começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeRelacoes.relacao1(publicador).ehIncognita()), "true");
        checar("Relacao2 começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeRelacoes.relacao2(publicador).ehIncognita()), "true");
        checar("RelacaoFinal começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Os três aceitam positivo, negativo e nulo (domínio INTEIROS nos três) ===");
        PapelQuantitativo t1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        checar("positivo (+5) é aceito", String.valueOf(t1.posicionar(new NumeroInteiro(5)).isPresent()), "false");
        PapelQuantitativo t2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        checar("negativo (-3) é aceito", String.valueOf(t2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");
        PapelQuantitativo t3 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        checar("nulo (0) é aceito", String.valueOf(t3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== relação estrutural CONSISTENTE (-6 + 11 = 5) ===");
        PapelQuantitativo a1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo a2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo af = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        a1.posicionar(new NumeroInteiro(-6));
        a2.posicionar(new NumeroInteiro(11));
        af.posicionar(new NumeroInteiro(5));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(a1, a2, af).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (-6 + 11 != 999) ===");
        PapelQuantitativo b1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo b2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo bf = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        b1.posicionar(new NumeroInteiro(-6));
        b2.posicionar(new NumeroInteiro(11));
        bf.posicionar(new NumeroInteiro(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(b1, b2, bf).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== calcularValorAusente com RelacaoFinal desconhecida (-6 + 11 = ?) ===");
        PapelQuantitativo c1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo c2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo cf = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        c1.posicionar(new NumeroInteiro(-6));
        c2.posicionar(new NumeroInteiro(11));
        ResultadoCalculo resFinal = relacao.calcularValorAusente(c1, c2, cf);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(cf.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resFinal.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resFinal.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicFinal = relacao.aplicar(resFinal, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicFinal.isPresent()), "false");
        checar("RelacaoFinal resolvida corretamente (5) após aplicar", String.valueOf(cf.valorAtual().valorOuNull()), "5");

        System.out.println();
        System.out.println("=== calcularValorAusente com Relacao1 desconhecida (? + 11 = 5) ===");
        PapelQuantitativo d1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo d2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo df = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        d2.posicionar(new NumeroInteiro(11));
        df.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resR1 = relacao.calcularValorAusente(d1, d2, df);
        relacao.aplicar(resR1, contexto);
        checar("Relacao1 resolvida corretamente (-6)", String.valueOf(d1.valorAtual().valorOuNull()), "-6");

        System.out.println();
        System.out.println("=== calcularValorAusente com Relacao2 desconhecida (-6 + ? = 5) ===");
        PapelQuantitativo e1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo e2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo ef = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        e1.posicionar(new NumeroInteiro(-6));
        ef.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resR2 = relacao.calcularValorAusente(e1, e2, ef);
        relacao.aplicar(resR2, contexto);
        checar("Relacao2 resolvida corretamente (11)", String.valueOf(e2.valorAtual().valorOuNull()), "11");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção ===");
        PapelQuantitativo f1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo f2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo ff = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        f1.posicionar(new NumeroInteiro(1));
        f2.posicionar(new NumeroInteiro(1));
        ff.posicionar(new NumeroInteiro(2));
        ResultadoCalculo resZeroIncognitas = relacao.calcularValorAusente(f1, f2, ff);
        checar("zero incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO (não é exceção)",
                resZeroIncognitas.getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("resultado sem valor calculável não tem valor calculável", String.valueOf(resZeroIncognitas.temValorCalculavel()), "false");

        boolean lancouExcecaoAoAplicarSemValor;
        try {
            relacao.aplicar(resZeroIncognitas, contexto);
            lancouExcecaoAoAplicarSemValor = false;
        } catch (IllegalStateException esperada) {
            lancouExcecaoAoAplicarSemValor = true;
        }
        checar("aplicar(...) sem valor calculável lança IllegalStateException (violação de contrato do chamador, não erro pedagógico)",
                String.valueOf(lancouExcecaoAoAplicarSemValor), "true");

        PapelQuantitativo g1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo g2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo gf = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        g1.posicionar(new NumeroInteiro(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(g1, g2, gf).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo h1 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo h2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo hf = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        checar("três incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(h1, h2, hf).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("representação totalmente vazia -> REPRESENTACAO_INCOMPLETA na verificação",
                relacao.verificarConsistencia(h1, h2, hf).name(), "REPRESENTACAO_INCOMPLETA");

        System.out.println();
        System.out.println("=== Eventos semânticos (só ACEITO — INTEIROS nos três nunca rejeita) ===");
        long aceitos = eventos.stream().filter(e -> "ACEITO".equals(e.paraMapa().get("resultado"))).count();
        long rejeitados = eventos.stream().filter(e -> "REJEITADO".equals(e.paraMapa().get("resultado"))).count();
        System.out.println("total de eventos: " + eventos.size() + " (aceitos=" + aceitos + ", rejeitados=" + rejeitados + ")");
        checar("existe ao menos um evento de valor aceito", String.valueOf(aceitos > 0), "true");
        checar("nenhum evento de rejeição (domínio INTEIROS nos três nunca rejeita)", String.valueOf(rejeitados), "0");

        System.out.println();
        System.out.println("=== Null Object do publicador ===");
        PapelQuantitativo semPublicador = FabricaPapeisComposicaoDeRelacoes.relacao1(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("Relacao1 com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisComposicaoDeRelacoes.relacao1(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("Relacao1 com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Serialização por paraMapa() ===");
        System.out.println("d1.paraMapa() = " + d1.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(d1.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (-6)", String.valueOf(d1.paraMapa().get("valor_atual")), "-6");

        System.out.println();
        System.out.println("=== diagnosticarValorProposto (2026-08-06): avalia o que foi proposto, não só o que falta ===");
        PapelQuantitativo r1E = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2E = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfE = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1E.posicionar(new NumeroInteiro(10));
        r2E.posicionar(new NumeroInteiro(-3));
        checar("proposta correta (7) -> Optional.empty()",
                String.valueOf(relacao.diagnosticarValorProposto(r1E, r2E, rfE, rfE, new NumeroInteiro(7)).isPresent()),
                "false");
        checar("proposta com operação invertida (10 - (-3) = 13, devia somar) -> OPERACAO_INVERTIDA",
                relacao.diagnosticarValorProposto(r1E, r2E, rfE, rfE, new NumeroInteiro(13)).get().getTipo().name(),
                "OPERACAO_INVERTIDA");
        checar("proposta sem padrão reconhecido (999) -> VALOR_INCORRETO",
                relacao.diagnosticarValorProposto(r1E, r2E, rfE, rfE, new NumeroInteiro(999)).get().getTipo().name(),
                "VALOR_INCORRETO");

        System.out.println();
        System.out.println("=== recalcularParaConsistencia (2026-08-06): recalcula um papel já conhecido quando outro muda ===");
        PapelQuantitativo r1R = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2R = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfR = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1R.posicionar(new NumeroInteiro(10));
        r2R.posicionar(new NumeroInteiro(-3));
        rfR.posicionar(new NumeroInteiro(7));
        checar("Relacao1 alterada, Relacao1 e Relacao2 conhecidas -> recalcula RelacaoFinal (10+(-3)=7)",
                String.valueOf(relacao.recalcularParaConsistencia(r1R, r2R, rfR, r1R).getValorCalculado().valorOuNull()),
                "7");
        checar("papel recalculado é RelacaoFinal",
                String.valueOf(relacao.recalcularParaConsistencia(r1R, r2R, rfR, r1R).getPapelCalculado() == rfR),
                "true");

        PapelQuantitativo r1R2 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2R2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfR2 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1R2.posicionar(new NumeroInteiro(10));
        rfR2.posicionar(new NumeroInteiro(7));
        checar("RelacaoFinal alterada, Relacao1 e RelacaoFinal conhecidas (Relacao2 ainda incógnita) "
                        + "-> recalcula Relacao2 (7-10=-3)",
                String.valueOf(relacao.recalcularParaConsistencia(r1R2, r2R2, rfR2, rfR2).getValorCalculado().valorOuNull()),
                "-3");

        PapelQuantitativo r1R3 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2R3 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfR3 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r2R3.posicionar(new NumeroInteiro(-3));
        rfR3.posicionar(new NumeroInteiro(7));
        checar("Relacao2 alterada, só Relacao2 e RelacaoFinal conhecidas (Relacao1 incógnita) "
                        + "-> recalcula Relacao1 (7-(-3)=10)",
                String.valueOf(relacao.recalcularParaConsistencia(r1R3, r2R3, rfR3, r2R3).getValorCalculado().valorOuNull()),
                "10");

        PapelQuantitativo r1R4 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2R4 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfR4 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1R4.posicionar(new NumeroInteiro(10));
        checar("Relacao1 alterada, só ela conhecida (Relacao2 e RelacaoFinal incógnitas) -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.recalcularParaConsistencia(r1R4, r2R4, rfR4, r1R4).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("=== guarda de estouro de int (2026-08-06): não devolve número errado como CONSISTENTE ===");
        PapelQuantitativo r1O = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2O = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfO = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1O.posicionar(new NumeroInteiro(2000000000));
        r2O.posicionar(new NumeroInteiro(2000000000));
        ResultadoCalculo resO = relacao.calcularValorAusente(r1O, r2O, rfO);
        checar("2e9 + 2e9 não é representável -> NAO_RESOLVIVEL_NESTE_ESTADO (antes dava -294967296/CONSISTENTE)",
                resO.getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("não há valor calculado quando estoura", String.valueOf(resO.temValorCalculavel()), "false");
        checar("recalcularParaConsistencia também protege",
                relacao.recalcularParaConsistencia(r1O, r2O, rfO, r1O).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo r1O2 = FabricaPapeisComposicaoDeRelacoes.relacao1(publicador);
        PapelQuantitativo r2O2 = FabricaPapeisComposicaoDeRelacoes.relacao2(publicador);
        PapelQuantitativo rfO2 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(publicador);
        r1O2.posicionar(new NumeroInteiro(2000000000));
        r2O2.posicionar(new NumeroInteiro(2000000000));
        rfO2.posicionar(new NumeroInteiro(1));
        checar("verificarConsistencia com soma não representável -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.verificarConsistencia(r1O2, r2O2, rfO2).name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE COMPOSIÇÃO DE RELAÇÕES PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
