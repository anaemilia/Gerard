import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Composição de Transformações — categoria
 * "Relações" de Vergnaud, não "Medidas": os três papéis (Transformacao1,
 * Transformacao2, TransformacaoFinal) são todos INTEIROS, sem restrição de
 * sinal — por isso, diferente dos harnesses de Composição/Transformação/
 * Comparação de Medidas, não há cenário de rejeição por domínio aqui: todo
 * valor inteiro é aceito nos três papéis. Mesmo padrão dos outros harnesses
 * do piloto quanto ao resto: calcularValorAusente() nunca modifica o papel
 * calculado, aplicar(...) é o passo explícito separado, origem_da_acao do
 * cálculo do sistema é sempre ORIGEM_SISTEMA.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoComposicaoDeTransformacoes {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalComposicaoDeTransformacoes relacao = RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-4", "usuario-local-1", "tentativa-4",
                "situacao-composicao-transformacoes-8-menos3-5", "diagrama-vergnaud-4");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("=== Testes: papéis começam como incógnita ===");
        checar("Transformacao1 começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador).ehIncognita()), "true");
        checar("Transformacao2 começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador).ehIncognita()), "true");
        checar("TransformacaoFinal começa como incógnita",
                String.valueOf(FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Os três aceitam positivo, negativo e nulo (domínio INTEIROS nos três) ===");
        PapelQuantitativo t1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        checar("positivo (+5) é aceito", String.valueOf(t1.posicionar(new NumeroInteiro(5)).isPresent()), "false");
        PapelQuantitativo t2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        checar("negativo (-3) é aceito", String.valueOf(t2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");
        PapelQuantitativo t3 = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        checar("nulo (0) é aceito", String.valueOf(t3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== relação estrutural CONSISTENTE (8 + (-3) = 5) ===");
        PapelQuantitativo a1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo a2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo af = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        a1.posicionar(new NumeroInteiro(8));
        a2.posicionar(new NumeroInteiro(-3));
        af.posicionar(new NumeroInteiro(5));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(a1, a2, af).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (8 + (-3) != 999) ===");
        PapelQuantitativo b1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo b2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo bf = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        b1.posicionar(new NumeroInteiro(8));
        b2.posicionar(new NumeroInteiro(-3));
        bf.posicionar(new NumeroInteiro(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(b1, b2, bf).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== calcularValorAusente com TransformacaoFinal desconhecida (8 + (-3) = ?) ===");
        PapelQuantitativo c1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo c2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo cf = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        c1.posicionar(new NumeroInteiro(8));
        c2.posicionar(new NumeroInteiro(-3));
        ResultadoCalculo resFinal = relacao.calcularValorAusente(c1, c2, cf);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(cf.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resFinal.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resFinal.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicFinal = relacao.aplicar(resFinal, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicFinal.isPresent()), "false");
        checar("TransformacaoFinal resolvida corretamente (5) após aplicar", String.valueOf(cf.valorAtual().valorOuNull()), "5");

        System.out.println();
        System.out.println("=== calcularValorAusente com Transformacao1 desconhecida (? + (-3) = 5) ===");
        PapelQuantitativo d1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo d2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo df = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        d2.posicionar(new NumeroInteiro(-3));
        df.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resT1 = relacao.calcularValorAusente(d1, d2, df);
        relacao.aplicar(resT1, contexto);
        checar("Transformacao1 resolvida corretamente (8)", String.valueOf(d1.valorAtual().valorOuNull()), "8");

        System.out.println();
        System.out.println("=== calcularValorAusente com Transformacao2 desconhecida (8 + ? = 5) ===");
        PapelQuantitativo e1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo e2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo ef = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        e1.posicionar(new NumeroInteiro(8));
        ef.posicionar(new NumeroInteiro(5));
        ResultadoCalculo resT2 = relacao.calcularValorAusente(e1, e2, ef);
        relacao.aplicar(resT2, contexto);
        checar("Transformacao2 resolvida corretamente (-3)", String.valueOf(e2.valorAtual().valorOuNull()), "-3");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção ===");
        PapelQuantitativo f1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo f2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo ff = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
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

        PapelQuantitativo g1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo g2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo gf = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
        g1.posicionar(new NumeroInteiro(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(g1, g2, gf).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo h1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(publicador);
        PapelQuantitativo h2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(publicador);
        PapelQuantitativo hf = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(publicador);
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
        PapelQuantitativo semPublicador = FabricaPapeisComposicaoDeTransformacoes.transformacao1(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("Transformacao1 com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisComposicaoDeTransformacoes.transformacao1(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("Transformacao1 com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Serialização por paraMapa() ===");
        System.out.println("d1.paraMapa() = " + d1.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(d1.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (8)", String.valueOf(d1.paraMapa().get("valor_atual")), "8");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE COMPOSIÇÃO DE TRANSFORMAÇÕES PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
