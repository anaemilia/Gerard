import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Transformação de Relação — categoria
 * "Relações" de Vergnaud, não "Medidas": os três papéis (RelacaoInicial,
 * Transformacao, RelacaoFinal) são todos INTEIROS, sem restrição de sinal —
 * por isso, diferente dos harnesses de Composição/Transformação/Comparação
 * de Medidas, não há cenário de rejeição por domínio aqui. Mesmo padrão dos
 * outros harnesses do piloto quanto ao resto.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoTransformacaoDeRelacao {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalTransformacaoDeRelacao relacao = RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-5", "usuario-local-1", "tentativa-5",
                "situacao-transformacao-relacao-menos4-mais9-5", "diagrama-vergnaud-5");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("=== Testes: papéis começam como incógnita ===");
        checar("RelacaoInicial começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador).ehIncognita()), "true");
        checar("Transformacao começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.transformacao(publicador).ehIncognita()), "true");
        checar("RelacaoFinal começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Os três aceitam positivo, negativo e nulo (domínio INTEIROS nos três) ===");
        PapelQuantitativo t1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        checar("positivo (+5) é aceito", String.valueOf(t1.posicionar(new NumeroInteiro(5)).isPresent()), "false");
        PapelQuantitativo t2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        checar("negativo (-3) é aceito", String.valueOf(t2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");
        PapelQuantitativo t3 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        checar("nulo (0) é aceito", String.valueOf(t3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== relação estrutural CONSISTENTE (-4 + 9 = 5) ===");
        PapelQuantitativo a1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo a2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo af = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        a1.posicionar(new NumeroInteiro(-4));
        a2.posicionar(new NumeroInteiro(9));
        af.posicionar(new NumeroInteiro(5));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(a1, a2, af).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (-4 + 9 != 999) ===");
        PapelQuantitativo b1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo b2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo bf = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        b1.posicionar(new NumeroInteiro(-4));
        b2.posicionar(new NumeroInteiro(9));
        bf.posicionar(new NumeroInteiro(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(b1, b2, bf).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== calcularValorAusente com RelacaoFinal desconhecida (-4 + 9 = ?) ===");
        PapelQuantitativo c1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo c2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo cf = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        c1.posicionar(new NumeroInteiro(-4));
        c2.posicionar(new NumeroInteiro(9));
        ResultadoCalculo resFinal = relacao.calcularValorAusente(c1, c2, cf);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(cf.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resFinal.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resFinal.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicFinal = relacao.aplicar(resFinal, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicFinal.isPresent()), "false");
        checar("RelacaoFinal resolvida corretamente (5) após aplicar", String.valueOf(cf.valorAtual().valorOuNull()), "5");

        System.out.println();
        System.out.println("=== calcularValorAusente com RelacaoInicial desconhecida (? + 9 = 5) ===");
        PapelQuantitativo d1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo d2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo df = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        d2.posicionar(new NumeroInteiro(9));
        df.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resRI = relacao.calcularValorAusente(d1, d2, df);
        relacao.aplicar(resRI, contexto);
        checar("RelacaoInicial resolvida corretamente (-4)", String.valueOf(d1.valorAtual().valorOuNull()), "-4");

        System.out.println();
        System.out.println("=== calcularValorAusente com Transformacao desconhecida (-4 + ? = 5) ===");
        PapelQuantitativo e1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo e2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo ef = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        e1.posicionar(new NumeroInteiro(-4));
        ef.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resTr = relacao.calcularValorAusente(e1, e2, ef);
        relacao.aplicar(resTr, contexto);
        checar("Transformacao resolvida corretamente (9)", String.valueOf(e2.valorAtual().valorOuNull()), "9");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção ===");
        PapelQuantitativo f1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo f2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo ff = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
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

        PapelQuantitativo g1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo g2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo gf = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        g1.posicionar(new NumeroInteiro(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(g1, g2, gf).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo h1 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo h2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo hf = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
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
        PapelQuantitativo semPublicador = FabricaPapeisTransformacaoDeRelacao.transformacao(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("Transformacao com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisTransformacaoDeRelacao.transformacao(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("Transformacao com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Serialização por paraMapa() ===");
        System.out.println("d1.paraMapa() = " + d1.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(d1.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (-4)", String.valueOf(d1.paraMapa().get("valor_atual")), "-4");

        System.out.println();
        System.out.println("=== diagnosticarValorProposto (2026-08-06): avalia o que foi proposto, não só o que falta ===");
        PapelQuantitativo riE = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trE = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfE = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riE.posicionar(new NumeroInteiro(10));
        trE.posicionar(new NumeroInteiro(-3));
        checar("proposta correta (7) -> Optional.empty()",
                String.valueOf(relacao.diagnosticarValorProposto(riE, trE, rfE, rfE, new NumeroInteiro(7)).isPresent()),
                "false");
        checar("proposta com operação invertida (10 - (-3) = 13, devia somar) -> OPERACAO_INVERTIDA",
                relacao.diagnosticarValorProposto(riE, trE, rfE, rfE, new NumeroInteiro(13)).get().getTipo().name(),
                "OPERACAO_INVERTIDA");
        checar("proposta sem padrão reconhecido (999) -> VALOR_INCORRETO",
                relacao.diagnosticarValorProposto(riE, trE, rfE, rfE, new NumeroInteiro(999)).get().getTipo().name(),
                "VALOR_INCORRETO");

        System.out.println();
        System.out.println("=== recalcularParaConsistencia (2026-08-06): recalcula um papel já conhecido quando outro muda ===");
        PapelQuantitativo riR = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trR = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfR = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riR.posicionar(new NumeroInteiro(10));
        trR.posicionar(new NumeroInteiro(-3));
        rfR.posicionar(new NumeroInteiro(7));
        checar("RelacaoInicial alterada, RelacaoInicial e Transformacao conhecidas -> recalcula RelacaoFinal (10+(-3)=7)",
                String.valueOf(relacao.recalcularParaConsistencia(riR, trR, rfR, riR).getValorCalculado().valorOuNull()),
                "7");
        checar("papel recalculado é RelacaoFinal",
                String.valueOf(relacao.recalcularParaConsistencia(riR, trR, rfR, riR).getPapelCalculado() == rfR),
                "true");

        PapelQuantitativo riR2 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trR2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfR2 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riR2.posicionar(new NumeroInteiro(10));
        rfR2.posicionar(new NumeroInteiro(7));
        checar("RelacaoFinal alterada, RelacaoInicial e RelacaoFinal conhecidas (Transformacao ainda incógnita) "
                        + "-> recalcula Transformacao (7-10=-3)",
                String.valueOf(relacao.recalcularParaConsistencia(riR2, trR2, rfR2, rfR2).getValorCalculado().valorOuNull()),
                "-3");

        PapelQuantitativo riR3 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trR3 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfR3 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        trR3.posicionar(new NumeroInteiro(-3));
        rfR3.posicionar(new NumeroInteiro(7));
        checar("Transformacao alterada, só Transformacao e RelacaoFinal conhecidas (RelacaoInicial incógnita) "
                        + "-> recalcula RelacaoInicial (7-(-3)=10)",
                String.valueOf(relacao.recalcularParaConsistencia(riR3, trR3, rfR3, trR3).getValorCalculado().valorOuNull()),
                "10");

        PapelQuantitativo riR4 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trR4 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfR4 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riR4.posicionar(new NumeroInteiro(10));
        checar("RelacaoInicial alterada, só ela conhecida (Transformacao e RelacaoFinal incógnitas) "
                        + "-> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.recalcularParaConsistencia(riR4, trR4, rfR4, riR4).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("=== guarda de estouro de int (2026-08-06): não devolve número errado como CONSISTENTE ===");
        PapelQuantitativo riO = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trO = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfO = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riO.posicionar(new NumeroInteiro(2000000000));
        trO.posicionar(new NumeroInteiro(2000000000));
        ResultadoCalculo resO = relacao.calcularValorAusente(riO, trO, rfO);
        checar("2e9 + 2e9 não é representável -> NAO_RESOLVIVEL_NESTE_ESTADO (antes dava -294967296/CONSISTENTE)",
                resO.getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("não há valor calculado quando estoura", String.valueOf(resO.temValorCalculavel()), "false");
        checar("recalcularParaConsistencia também protege",
                relacao.recalcularParaConsistencia(riO, trO, rfO, riO).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo riO2 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador);
        PapelQuantitativo trO2 = FabricaPapeisTransformacaoDeRelacao.transformacao(publicador);
        PapelQuantitativo rfO2 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador);
        riO2.posicionar(new NumeroInteiro(2000000000));
        trO2.posicionar(new NumeroInteiro(2000000000));
        rfO2.posicionar(new NumeroInteiro(1));
        checar("verificarConsistencia com soma não representável -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.verificarConsistencia(riO2, trO2, rfO2).name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("=== necessitaRepresentacaoDeSinal() (2026-08-18): objeto rico, sem olhar forma/desenho ===");
        checar("RelacaoInicial (INTEIROS) necessita representação de sinal",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.relacaoInicial(publicador).necessitaRepresentacaoDeSinal()),
                "true");
        checar("Transformacao (INTEIROS) necessita representação de sinal",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.transformacao(publicador).necessitaRepresentacaoDeSinal()),
                "true");
        checar("RelacaoFinal (INTEIROS) necessita representação de sinal",
                String.valueOf(FabricaPapeisTransformacaoDeRelacao.relacaoFinal(publicador).necessitaRepresentacaoDeSinal()),
                "true");
        checar("Parte1 (NATURAIS, outro esquema) NÃO necessita representação de sinal",
                String.valueOf(PapelQuantitativo.parte1(publicador).necessitaRepresentacaoDeSinal()),
                "false");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE TRANSFORMAÇÃO DE RELAÇÃO PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
