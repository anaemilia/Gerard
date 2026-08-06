import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Transformação de Medidas — versão corrigida
 * (baseline v2). Principais diferenças em relação à v1:
 * - calcularValorAusente() (antes resolverIncognita()) NUNCA modifica o
 *   papel calculado — devolve um ResultadoCalculo; aplicar(...) é o passo
 *   explícito, separado, que decide posicionar o valor;
 * - todo valor aplicado por cálculo do sistema carrega origem_da_acao =
 *   ORIGEM_SISTEMA no evento, nunca ORIGEM_USUARIO;
 * - zero, duas ou três incógnitas não lançam mais exceção — resultam em
 *   NAO_RESOLVIVEL_NESTE_ESTADO, um estado explícito, não um erro.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoTransformacaoMedidas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalTransformacao relacao = RelacaoEstruturalTransformacao.transformacaoDeMedidas();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-2", "usuario-local-1", "tentativa-2",
                "situacao-transformacao-10-menos3-7", "diagrama-vergnaud-2");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

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
        System.out.println("=== Teste 10: relação estrutural CONSISTENTE (10 + (-3) = 7) ===");
        PapelQuantitativo ei = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei.posicionar(new NumeroNatural(10));
        tr.posicionar(new NumeroInteiro(-3));
        ef.posicionar(new NumeroNatural(7));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(ei, tr, ef).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (10 + (-3) != 999) ===");
        PapelQuantitativo eiInc = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trInc = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efInc = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        eiInc.posicionar(new NumeroNatural(10));
        trInc.posicionar(new NumeroInteiro(-3));
        efInc.posicionar(new NumeroNatural(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(eiInc, trInc, efInc).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== Teste 13: calcularValorAusente com EstadoFinal desconhecido (10 + (-3) = ?) ===");
        PapelQuantitativo ei2 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr2 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef2 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei2.posicionar(new NumeroNatural(10));
        tr2.posicionar(new NumeroInteiro(-3));
        ResultadoCalculo resE = relacao.calcularValorAusente(ei2, tr2, ef2);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(ef2.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resE.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resE.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicE = relacao.aplicar(resE, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicE.isPresent()), "false");
        checar("EstadoFinal resolvido corretamente (7) após aplicar", String.valueOf(ef2.valorAtual().valorOuNull()), "7");

        System.out.println();
        System.out.println("=== Teste 11: calcularValorAusente com EstadoInicial desconhecido (? + (-3) = 7) ===");
        PapelQuantitativo ei3 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr3 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef3 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        tr3.posicionar(new NumeroInteiro(-3));
        ef3.posicionar(new NumeroNatural(7));
        ResultadoCalculo resF = relacao.calcularValorAusente(ei3, tr3, ef3);
        relacao.aplicar(resF, contexto);
        checar("EstadoInicial resolvido corretamente (10)", String.valueOf(ei3.valorAtual().valorOuNull()), "10");

        System.out.println();
        System.out.println("=== Teste 12: calcularValorAusente com Transformacao desconhecida (10 + ? = 7) ===");
        PapelQuantitativo ei4 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr4 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef4 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei4.posicionar(new NumeroNatural(10));
        ef4.posicionar(new NumeroNatural(7));
        ResultadoCalculo resG = relacao.calcularValorAusente(ei4, tr4, ef4);
        relacao.aplicar(resG, contexto);
        checar("Transformacao resolvida corretamente (-3)", String.valueOf(tr4.valorAtual().valorOuNull()), "-3");

        System.out.println();
        System.out.println("=== Teste 7: rejeição de EstadoInicial negativo (ação do usuário) ===");
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
        System.out.println("=== Teste 9: rejeição de cálculo do SISTEMA que produziria EstadoFinal negativo (3 + (-10) = -7) ===");
        PapelQuantitativo ei6 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr6 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef6 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei6.posicionar(new NumeroNatural(3));
        tr6.posicionar(new NumeroInteiro(-10));
        ResultadoCalculo resJ = relacao.calcularValorAusente(ei6, tr6, ef6);
        checar("cálculo em si é CONSISTENTE (o valor -7 foi calculável, mesmo que inválido para o papel)",
                resJ.getEstadoConsistencia().name(), "CONSISTENTE");
        Optional<DiagnosticoErroPapel> aplicJ = relacao.aplicar(resJ, contexto);
        checar("aplicar o cálculo que produz EstadoFinal negativo é rejeitado pelo próprio papel",
                String.valueOf(aplicJ.isPresent()), "true");
        checar("EstadoFinal permanece incógnita (o sistema não força um valor inválido)",
                String.valueOf(ef6.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Origem da rejeição acima é do SISTEMA, nunca do usuário (integridade do dado de pesquisa) ===");
        EventoDominio ultimoEvento = eventos.get(eventos.size() - 1);
        checar("evento da rejeição tem origem_da_acao = ORIGEM_SISTEMA",
                String.valueOf(ultimoEvento.paraMapa().get("origem_da_acao")), "ORIGEM_SISTEMA");
        checar("evento da rejeição tem resultado = REJEITADO",
                String.valueOf(ultimoEvento.paraMapa().get("resultado")), "REJEITADO");
        checar("evento carrega id_situacao_problema do contexto",
                String.valueOf(ultimoEvento.paraMapa().get("id_situacao_problema")), "situacao-transformacao-10-menos3-7");

        System.out.println();
        System.out.println("=== Testes 19-22: diagnóstico pedagógico da rejeição do sistema ===");
        DiagnosticoErroPapel diagnostico = aplicJ.get();
        checar("diagnóstico presente (teste 19)", String.valueOf(diagnostico != null), "true");
        checar("chave de mensagem (teste 20)", diagnostico.getChaveMensagem(), "erro.papel.valorForaDoDominio");
        checar("chave de feedback pedagógico (teste 21)", diagnostico.getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("chave de sugestão de correção (teste 22)", diagnostico.getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção (teste do item 9 da correção) ===");
        PapelQuantitativo ei8 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr8 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef8 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei8.posicionar(new NumeroNatural(1));
        tr8.posicionar(new NumeroInteiro(1));
        ef8.posicionar(new NumeroNatural(2));
        ResultadoCalculo resZeroIncognitas = relacao.calcularValorAusente(ei8, tr8, ef8);
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

        PapelQuantitativo ei9 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr9 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef9 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        ei9.posicionar(new NumeroNatural(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(ei9, tr9, ef9).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo ei10 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo tr10 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo ef10 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        checar("três incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(ei10, tr10, ef10).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("representação totalmente vazia -> REPRESENTACAO_INCOMPLETA na verificação",
                relacao.verificarConsistencia(ei10, tr10, ef10).name(), "REPRESENTACAO_INCOMPLETA");

        System.out.println();
        System.out.println("=== Testes 14-16: eventos semânticos ===");
        long aceitos = eventos.stream().filter(e -> "ACEITO".equals(e.paraMapa().get("resultado"))).count();
        long rejeitados = eventos.stream().filter(e -> "REJEITADO".equals(e.paraMapa().get("resultado"))).count();
        System.out.println("total de eventos: " + eventos.size() + " (aceitos=" + aceitos + ", rejeitados=" + rejeitados + ")");
        checar("existe ao menos um evento de valor aceito (teste 14)", String.valueOf(aceitos > 0), "true");
        checar("existe ao menos um evento de valor rejeitado (teste 15)", String.valueOf(rejeitados > 0), "true");
        checar("quantidade total de eventos é exatamente a esperada (teste 16)", String.valueOf(eventos.size()), "27");

        System.out.println();
        System.out.println("=== Teste 17: Null Object do publicador ===");
        PapelQuantitativo semPublicador = FabricaPapeisTransformacaoMedidas.transformacao(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("Transformacao com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisTransformacaoMedidas.transformacao(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("Transformacao com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Teste 18: serialização por paraMapa() ===");
        System.out.println("tr2.paraMapa() = " + tr2.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(tr2.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (-3)", String.valueOf(tr2.paraMapa().get("valor_atual")), "-3");

        System.out.println();
        System.out.println("=== diagnosticarValorProposto (2026-08-06): avalia o que foi proposto, não só o que falta ===");
        PapelQuantitativo eiD = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trD = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efD = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        eiD.posicionar(new NumeroNatural(10));
        trD.posicionar(new NumeroInteiro(-3));
        checar("proposta correta (7) -> Optional.empty()",
                String.valueOf(relacao.diagnosticarValorProposto(eiD, trD, efD, efD, new NumeroNatural(7)).isPresent()),
                "false");
        checar("proposta com operação invertida (10 - (-3) = 13, devia somar) -> OPERACAO_INVERTIDA",
                relacao.diagnosticarValorProposto(eiD, trD, efD, efD, new NumeroNatural(13)).get().getTipo().name(),
                "OPERACAO_INVERTIDA");
        checar("proposta sem padrão reconhecido (999) -> VALOR_INCORRETO",
                relacao.diagnosticarValorProposto(eiD, trD, efD, efD, new NumeroNatural(999)).get().getTipo().name(),
                "VALOR_INCORRETO");
        checar("proposta fora do domínio (EstadoFinal natural, -1) -> VALOR_FORA_DO_DOMINIO",
                relacao.diagnosticarValorProposto(eiD, trD, efD, efD, new NumeroInteiro(-1)).get().getTipo().name(),
                "VALOR_FORA_DO_DOMINIO");

        PapelQuantitativo eiD2 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trD2 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efD2 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        efD2.posicionar(new NumeroNatural(7));
        checar("EstadoInicial proposto corretamente (10, EstadoFinal - Transformacao é a incógnita errada aqui) "
                        + "-> exceção de pré-condição (dois incógnitos, não um)",
                verificaLancaIllegalState(() ->
                        relacao.diagnosticarValorProposto(eiD2, trD2, efD2, eiD2, new NumeroNatural(10))),
                "true");

        System.out.println();
        System.out.println("=== recalcularParaConsistencia (2026-08-06): recalcula um papel já conhecido quando outro muda ===");
        PapelQuantitativo eiR = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trR = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efR = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        eiR.posicionar(new NumeroNatural(10));
        trR.posicionar(new NumeroInteiro(-3));
        efR.posicionar(new NumeroNatural(7));
        checar("EstadoInicial alterado, EstadoInicial e Transformacao conhecidos -> recalcula EstadoFinal (10+(-3)=7)",
                String.valueOf(relacao.recalcularParaConsistencia(eiR, trR, efR, eiR).getValorCalculado().valorOuNull()),
                "7");
        checar("papel recalculado é EstadoFinal",
                String.valueOf(relacao.recalcularParaConsistencia(eiR, trR, efR, eiR).getPapelCalculado() == efR),
                "true");

        PapelQuantitativo eiR2 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trR2 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efR2 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        eiR2.posicionar(new NumeroNatural(10));
        efR2.posicionar(new NumeroNatural(7));
        checar("EstadoFinal alterado, EstadoInicial e EstadoFinal conhecidos (Transformacao ainda incógnita) "
                        + "-> recalcula Transformacao (7-10=-3)",
                String.valueOf(relacao.recalcularParaConsistencia(eiR2, trR2, efR2, efR2).getValorCalculado().valorOuNull()),
                "-3");

        PapelQuantitativo eiR3 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trR3 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efR3 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        trR3.posicionar(new NumeroInteiro(-3));
        efR3.posicionar(new NumeroNatural(7));
        checar("Transformacao alterada, só Transformacao e EstadoFinal conhecidos (EstadoInicial incógnito) "
                        + "-> recalcula EstadoInicial (7-(-3)=10)",
                String.valueOf(relacao.recalcularParaConsistencia(eiR3, trR3, efR3, trR3).getValorCalculado().valorOuNull()),
                "10");

        PapelQuantitativo eiR4 = FabricaPapeisTransformacaoMedidas.estadoInicial(publicador);
        PapelQuantitativo trR4 = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        PapelQuantitativo efR4 = FabricaPapeisTransformacaoMedidas.estadoFinal(publicador);
        eiR4.posicionar(new NumeroNatural(10));
        checar("EstadoInicial alterado, só ele conhecido (Transformacao e EstadoFinal incógnitos) "
                        + "-> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.recalcularParaConsistencia(eiR4, trR4, efR4, eiR4).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo papelEstranho = FabricaPapeisTransformacaoMedidas.transformacao(publicador);
        checar("papelAlterado que não pertence à relação -> IllegalArgumentException",
                verificaLancaIllegalArgument(() ->
                        relacao.recalcularParaConsistencia(eiR4, trR4, efR4, papelEstranho)),
                "true");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE TRANSFORMAÇÃO DE MEDIDAS PASSARAM.");
    }

    private static String verificaLancaIllegalState(Runnable acao) {
        try {
            acao.run();
            return "false";
        } catch (IllegalStateException esperada) {
            return "true";
        }
    }

    private static String verificaLancaIllegalArgument(Runnable acao) {
        try {
            acao.run();
            return "false";
        } catch (IllegalArgumentException esperada) {
            return "true";
        }
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
