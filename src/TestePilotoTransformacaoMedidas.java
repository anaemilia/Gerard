import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.InvarianteOperatorioTransformacao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do segundo piloto arquitetural — Transformação de
 * Medidas (EstadoInicial, Transformacao, EstadoFinal), mesmo espírito de
 * TestePilotoPapelQuantitativo e TesteBaseConhecimento: falha alto
 * (AssertionError) se o piloto quebrar, não é framework de testes.
 *
 * Não toca em Main.java nem em nenhum caminho de produção. Reaproveita
 * PapelQuantitativo sem alterá-lo (classe congelada no baseline
 * baseline-piloto-papel-quantitativo).
 */
public class TestePilotoTransformacaoMedidas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        InvarianteOperatorioTransformacao invariante = InvarianteOperatorioTransformacao.transformacaoDeMedidas();
        System.out.println("invariante: " + invariante.explicar());

        System.out.println();
        System.out.println("=== Testes 4-6: papéis começam como incógnita ===");
        checar("EstadoInicial começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoMedidas.estadoInicial(publicador).ehIncognita()), "true");
        checar("Transformacao começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoMedidas.transformacao(publicador).ehIncognita()), "true");
        checar("EstadoFinal começa como incógnita",
                String.valueOf(FabricaPapeisTransformacaoMedidas.estadoFinal(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Testes 1-3: Transformacao aceita positiva, negativa e nula (domínio INTEIROS) ===");
        PapelQuantitativo t1 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        checar("transformação positiva (+5) é aceita", String.valueOf(t1.posicionar(new NumeroInteiro(5)).isPresent()), "false");

        PapelQuantitativo t2 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        checar("transformação negativa (-3) é aceita", String.valueOf(t2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");

        PapelQuantitativo t3 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        checar("transformação nula (0) é aceita", String.valueOf(t3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== Teste 10: invariante completo e correto (10 + (-3) = 7) ===");
        PapelQuantitativo ei = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei.posicionar(new NumeroNatural(10));
        tr.posicionar(new NumeroInteiro(-3));
        ef.posicionar(new NumeroNatural(7));
        checar("invariante EstadoFinal=EstadoInicial+Transformacao satisfeito",
                String.valueOf(invariante.verificar(ei, tr, ef)), "true");

        System.out.println();
        System.out.println("=== Teste 13: cálculo com EstadoFinal desconhecido (10 + (-3) = ?) ===");
        PapelQuantitativo ei2 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr2 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef2 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei2.posicionar(new NumeroNatural(10));
        tr2.posicionar(new NumeroInteiro(-3));
        Optional<DiagnosticoErroPapel> resE = invariante.resolverIncognita(ei2, tr2, ef2);
        checar("resolver EstadoFinal desconhecido é aceito", String.valueOf(resE.isPresent()), "false");
        checar("EstadoFinal resolvido corretamente (7)", String.valueOf(ef2.valorAtual().valorOuNull()), "7");

        System.out.println();
        System.out.println("=== Teste 11: cálculo com EstadoInicial desconhecido (? + (-3) = 7) ===");
        PapelQuantitativo ei3 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr3 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef3 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        tr3.posicionar(new NumeroInteiro(-3));
        ef3.posicionar(new NumeroNatural(7));
        Optional<DiagnosticoErroPapel> resF = invariante.resolverIncognita(ei3, tr3, ef3);
        checar("resolver EstadoInicial desconhecido é aceito", String.valueOf(resF.isPresent()), "false");
        checar("EstadoInicial resolvido corretamente (10)", String.valueOf(ei3.valorAtual().valorOuNull()), "10");

        System.out.println();
        System.out.println("=== Teste 12: cálculo com Transformacao desconhecida (10 + ? = 7) ===");
        PapelQuantitativo ei4 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr4 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef4 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei4.posicionar(new NumeroNatural(10));
        ef4.posicionar(new NumeroNatural(7));
        Optional<DiagnosticoErroPapel> resG = invariante.resolverIncognita(ei4, tr4, ef4);
        checar("resolver Transformacao desconhecida é aceito", String.valueOf(resG.isPresent()), "false");
        checar("Transformacao resolvida corretamente (-3)", String.valueOf(tr4.valorAtual().valorOuNull()), "-3");

        System.out.println();
        System.out.println("=== Teste 7: rejeição de EstadoInicial negativo ===");
        PapelQuantitativo ei5 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        Optional<DiagnosticoErroPapel> resH = ei5.posicionar(new NumeroInteiro(-2));
        checar("EstadoInicial (NATURAIS) rejeita valor negativo", String.valueOf(resH.isPresent()), "true");
        checar("EstadoInicial permanece incógnita após rejeição", String.valueOf(ei5.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Teste 8: rejeição de EstadoFinal negativo (posicionamento direto) ===");
        PapelQuantitativo ef5 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        Optional<DiagnosticoErroPapel> resI = ef5.posicionar(new NumeroInteiro(-2));
        checar("EstadoFinal (NATURAIS) rejeita valor negativo", String.valueOf(resI.isPresent()), "true");

        System.out.println();
        System.out.println("=== Teste 9: rejeição de operação que produziria EstadoFinal negativo (3 + (-10) = -7) ===");
        PapelQuantitativo ei6 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr6 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef6 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei6.posicionar(new NumeroNatural(3));
        tr6.posicionar(new NumeroInteiro(-10));
        Optional<DiagnosticoErroPapel> resJ = invariante.resolverIncognita(ei6, tr6, ef6);
        checar("resolver EstadoFinal com resultado negativo é rejeitado", String.valueOf(resJ.isPresent()), "true");
        checar("EstadoFinal permanece incógnita (não aceita o resultado negativo)",
                String.valueOf(ef6.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Testes 19-22: diagnóstico pedagógico da rejeição acima ===");
        DiagnosticoErroPapel diagnostico = resJ.get();
        checar("diagnóstico presente (teste 19)", String.valueOf(diagnostico != null), "true");
        checar("chave de mensagem (teste 20)", diagnostico.getChaveMensagem(), "erro.papel.valorForaDoDominio");
        checar("chave de feedback pedagógico (teste 21)", diagnostico.getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("chave de sugestão de correção (teste 22)", diagnostico.getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");

        System.out.println();
        System.out.println("=== Testes 14-16: eventos semânticos ===");
        long aceitos = eventos.stream().filter(e -> "VALOR_POSICIONADO".equals(e.getTipo())).count();
        long rejeitados = eventos.stream().filter(e -> "VALOR_REJEITADO".equals(e.getTipo())).count();
        System.out.println("total de eventos: " + eventos.size() + " (aceitos=" + aceitos + ", rejeitados=" + rejeitados + ")");
        checar("existe ao menos um evento de valor aceito (teste 14)", String.valueOf(aceitos > 0), "true");
        checar("existe ao menos um evento de valor rejeitado (teste 15)", String.valueOf(rejeitados > 0), "true");
        checar("quantidade total de eventos é exatamente a esperada (teste 16)", String.valueOf(eventos.size()), "20");

        System.out.println();
        System.out.println("=== Teste 17: funciona sem publicador de eventos ===");
        PapelQuantitativo semPublicador = FabricaPapeisTransformacaoMedidas.transformacao(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("Transformacao sem publicador ainda funciona", String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");

        System.out.println();
        System.out.println("=== Teste 18: serialização por paraMapa() ===");
        System.out.println("tr2.paraMapa() = " + tr2.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(tr2.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (-3)", String.valueOf(tr2.paraMapa().get("valor_atual")), "-3");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE TRANSFORMAÇÃO DE MEDIDAS PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
