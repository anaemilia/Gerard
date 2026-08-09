import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Comparação de Medidas — mesmo padrão de
 * TestePilotoComposicaoMedidas (Composição) e TestePilotoTransformacaoMedidas
 * (Transformação): calcularValorAusente() NUNCA modifica o papel calculado —
 * devolve um ResultadoCalculo; aplicar(...) é o passo explícito, separado,
 * que decide posicionar o valor. Todo valor aplicado por cálculo do sistema
 * carrega origem_da_acao = ORIGEM_SISTEMA no evento, nunca ORIGEM_USUARIO.
 * Zero, duas ou três incógnitas não lançam exceção — resultam em
 * NAO_RESOLVIVEL_NESTE_ESTADO, um estado explícito, não um erro.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoComparacaoMedidas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalComparacao relacao = RelacaoEstruturalComparacao.comparacaoDeMedidas();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-3", "usuario-local-1", "tentativa-3",
                "situacao-comparacao-6-8-14", "diagrama-vergnaud-3");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("=== Testes: papéis começam como incógnita ===");
        checar("Referido começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.referido(publicador).ehIncognita()), "true");
        checar("ValorRelativo começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.valorRelativo(publicador).ehIncognita()), "true");
        checar("Referendo começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.referendo(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== ValorRelativo aceita positiva, negativa e nula (domínio INTEIROS) ===");
        PapelQuantitativo v1 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo positivo (+5) é aceito", String.valueOf(v1.posicionar(new NumeroInteiro(5)).isPresent()), "false");
        PapelQuantitativo v2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo negativo (-3) é aceito", String.valueOf(v2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");
        PapelQuantitativo v3 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo nulo (0) é aceito", String.valueOf(v3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== relação estrutural CONSISTENTE (6 + 8 = 14) ===");
        PapelQuantitativo rd = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd.posicionar(new NumeroNatural(6));
        vr.posicionar(new NumeroInteiro(8));
        rn.posicionar(new NumeroNatural(14));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(rd, vr, rn).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (6 + 8 != 999) ===");
        PapelQuantitativo rdInc = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrInc = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnInc = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdInc.posicionar(new NumeroNatural(6));
        vrInc.posicionar(new NumeroInteiro(8));
        rnInc.posicionar(new NumeroNatural(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(rdInc, vrInc, rnInc).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== calcularValorAusente com Referendo desconhecido (6 + 8 = ?) ===");
        PapelQuantitativo rd2 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn2 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd2.posicionar(new NumeroNatural(6));
        vr2.posicionar(new NumeroInteiro(8));
        ResultadoCalculo resReferendo = relacao.calcularValorAusente(rd2, vr2, rn2);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(rn2.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resReferendo.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resReferendo.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicReferendo = relacao.aplicar(resReferendo, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicReferendo.isPresent()), "false");
        checar("Referendo resolvido corretamente (14) após aplicar", String.valueOf(rn2.valorAtual().valorOuNull()), "14");

        System.out.println();
        System.out.println("=== calcularValorAusente com Referido desconhecido (? + 8 = 14) ===");
        PapelQuantitativo rd3 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr3 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn3 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        vr3.posicionar(new NumeroInteiro(8));
        rn3.posicionar(new NumeroNatural(14));
        ResultadoCalculo resReferido = relacao.calcularValorAusente(rd3, vr3, rn3);
        relacao.aplicar(resReferido, contexto);
        checar("Referido resolvido corretamente (6)", String.valueOf(rd3.valorAtual().valorOuNull()), "6");

        System.out.println();
        System.out.println("=== calcularValorAusente com ValorRelativo desconhecido (6 + ? = 14) ===");
        PapelQuantitativo rd4 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr4 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn4 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd4.posicionar(new NumeroNatural(6));
        rn4.posicionar(new NumeroNatural(14));
        ResultadoCalculo resValorRelativo = relacao.calcularValorAusente(rd4, vr4, rn4);
        relacao.aplicar(resValorRelativo, contexto);
        checar("ValorRelativo resolvido corretamente (8)", String.valueOf(vr4.valorAtual().valorOuNull()), "8");

        System.out.println();
        System.out.println("=== rejeição de Referido negativo (ação do usuário) ===");
        PapelQuantitativo rd5 = FabricaPapeisComparacaoMedidas.referido(publicador);
        Optional<DiagnosticoErroPapel> resRejReferido = rd5.posicionar(new NumeroInteiro(-2));
        checar("Referido (NATURAIS) rejeita valor negativo", String.valueOf(resRejReferido.isPresent()), "true");
        checar("Referido permanece incógnita após rejeição", String.valueOf(rd5.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== rejeição de Referendo negativo (posicionamento direto) ===");
        PapelQuantitativo rn5 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        Optional<DiagnosticoErroPapel> resRejReferendo = rn5.posicionar(new NumeroInteiro(-2));
        checar("Referendo (NATURAIS) rejeita valor negativo", String.valueOf(resRejReferendo.isPresent()), "true");

        System.out.println();
        System.out.println("=== rejeição de cálculo do SISTEMA que produziria Referendo negativo (3 + (-10) = -7) ===");
        PapelQuantitativo rd6 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr6 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn6 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd6.posicionar(new NumeroNatural(3));
        vr6.posicionar(new NumeroInteiro(-10));
        ResultadoCalculo resRej = relacao.calcularValorAusente(rd6, vr6, rn6);
        checar("cálculo em si é CONSISTENTE (o valor -7 foi calculável, mesmo que inválido para o papel)",
                resRej.getEstadoConsistencia().name(), "CONSISTENTE");
        Optional<DiagnosticoErroPapel> aplicRej = relacao.aplicar(resRej, contexto);
        checar("aplicar o cálculo que produz Referendo negativo é rejeitado pelo próprio papel",
                String.valueOf(aplicRej.isPresent()), "true");
        checar("Referendo permanece incógnita (o sistema não força um valor inválido)",
                String.valueOf(rn6.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Origem da rejeição acima é do SISTEMA, nunca do usuário (integridade do dado de pesquisa) ===");
        EventoDominio ultimoEvento = eventos.get(eventos.size() - 1);
        checar("evento da rejeição tem origem_da_acao = ORIGEM_SISTEMA",
                String.valueOf(ultimoEvento.paraMapa().get("origem_da_acao")), "ORIGEM_SISTEMA");
        checar("evento da rejeição tem resultado = REJEITADO",
                String.valueOf(ultimoEvento.paraMapa().get("resultado")), "REJEITADO");
        checar("evento carrega id_situacao_problema do contexto",
                String.valueOf(ultimoEvento.paraMapa().get("id_situacao_problema")), "situacao-comparacao-6-8-14");

        System.out.println();
        System.out.println("=== diagnóstico pedagógico da rejeição do sistema ===");
        DiagnosticoErroPapel diagnostico = aplicRej.get();
        checar("diagnóstico presente", String.valueOf(diagnostico != null), "true");
        checar("chave de mensagem", diagnostico.getChaveMensagem(), "erro.papel.valorForaDoDominio");
        checar("chave de feedback pedagógico", diagnostico.getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("chave de sugestão de correção", diagnostico.getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção ===");
        PapelQuantitativo rd7 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr7 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn7 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd7.posicionar(new NumeroNatural(1));
        vr7.posicionar(new NumeroInteiro(1));
        rn7.posicionar(new NumeroNatural(2));
        ResultadoCalculo resZeroIncognitas = relacao.calcularValorAusente(rd7, vr7, rn7);
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

        PapelQuantitativo rd8 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr8 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn8 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd8.posicionar(new NumeroNatural(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(rd8, vr8, rn8).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo rd9 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr9 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn9 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        checar("três incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(rd9, vr9, rn9).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("representação totalmente vazia -> REPRESENTACAO_INCOMPLETA na verificação",
                relacao.verificarConsistencia(rd9, vr9, rn9).name(), "REPRESENTACAO_INCOMPLETA");

        System.out.println();
        System.out.println("=== Eventos semânticos ===");
        long aceitos = eventos.stream().filter(e -> "ACEITO".equals(e.paraMapa().get("resultado"))).count();
        long rejeitados = eventos.stream().filter(e -> "REJEITADO".equals(e.paraMapa().get("resultado"))).count();
        System.out.println("total de eventos: " + eventos.size() + " (aceitos=" + aceitos + ", rejeitados=" + rejeitados + ")");
        checar("existe ao menos um evento de valor aceito", String.valueOf(aceitos > 0), "true");
        checar("existe ao menos um evento de valor rejeitado", String.valueOf(rejeitados > 0), "true");

        System.out.println();
        System.out.println("=== Null Object do publicador ===");
        PapelQuantitativo semPublicador = FabricaPapeisComparacaoMedidas.valorRelativo(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("ValorRelativo com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisComparacaoMedidas.valorRelativo(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("ValorRelativo com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Serialização por paraMapa() ===");
        System.out.println("vr2.paraMapa() = " + vr2.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(vr2.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (8)", String.valueOf(vr2.paraMapa().get("valor_atual")), "8");

        System.out.println();
        System.out.println("=== diagnosticarValorProposto (2026-08-06): avalia o que foi proposto, não só o que falta ===");
        PapelQuantitativo rdE = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrE = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnE = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdE.posicionar(new NumeroNatural(10));
        vrE.posicionar(new NumeroInteiro(-3));
        checar("proposta correta (7) -> Optional.empty()",
                String.valueOf(relacao.diagnosticarValorProposto(rdE, vrE, rnE, rnE, new NumeroNatural(7)).isPresent()),
                "false");
        checar("proposta com operação invertida (10 - (-3) = 13, devia somar) -> OPERACAO_INVERTIDA",
                relacao.diagnosticarValorProposto(rdE, vrE, rnE, rnE, new NumeroNatural(13)).get().getTipo().name(),
                "OPERACAO_INVERTIDA");
        checar("proposta sem padrão reconhecido (999) -> VALOR_INCORRETO",
                relacao.diagnosticarValorProposto(rdE, vrE, rnE, rnE, new NumeroNatural(999)).get().getTipo().name(),
                "VALOR_INCORRETO");
        checar("proposta fora do domínio (Referendo natural, -1) -> VALOR_FORA_DO_DOMINIO",
                relacao.diagnosticarValorProposto(rdE, vrE, rnE, rnE, new NumeroInteiro(-1)).get().getTipo().name(),
                "VALOR_FORA_DO_DOMINIO");

        System.out.println();
        System.out.println("=== recalcularParaConsistencia (2026-08-06): recalcula um papel já conhecido quando outro muda ===");
        PapelQuantitativo rdR = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrR = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnR = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdR.posicionar(new NumeroNatural(10));
        vrR.posicionar(new NumeroInteiro(-3));
        rnR.posicionar(new NumeroNatural(7));
        checar("Referido alterado, Referido e ValorRelativo conhecidos -> recalcula Referendo (10+(-3)=7)",
                String.valueOf(relacao.recalcularParaConsistencia(rdR, vrR, rnR, rdR).getValorCalculado().valorOuNull()),
                "7");
        checar("papel recalculado é Referendo",
                String.valueOf(relacao.recalcularParaConsistencia(rdR, vrR, rnR, rdR).getPapelCalculado() == rnR),
                "true");

        PapelQuantitativo rdR2 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrR2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnR2 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdR2.posicionar(new NumeroNatural(10));
        rnR2.posicionar(new NumeroNatural(7));
        checar("Referendo alterado, Referido e Referendo conhecidos (ValorRelativo ainda incógnito) "
                        + "-> recalcula ValorRelativo (7-10=-3)",
                String.valueOf(relacao.recalcularParaConsistencia(rdR2, vrR2, rnR2, rnR2).getValorCalculado().valorOuNull()),
                "-3");

        PapelQuantitativo rdR3 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrR3 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnR3 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        vrR3.posicionar(new NumeroInteiro(-3));
        rnR3.posicionar(new NumeroNatural(7));
        checar("ValorRelativo alterado, só ValorRelativo e Referendo conhecidos (Referido incógnito) "
                        + "-> recalcula Referido (7-(-3)=10)",
                String.valueOf(relacao.recalcularParaConsistencia(rdR3, vrR3, rnR3, vrR3).getValorCalculado().valorOuNull()),
                "10");

        PapelQuantitativo rdR4 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrR4 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnR4 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdR4.posicionar(new NumeroNatural(10));
        checar("Referido alterado, só ele conhecido (ValorRelativo e Referendo incógnitos) -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.recalcularParaConsistencia(rdR4, vrR4, rnR4, rdR4).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("=== guarda de estouro de int (2026-08-06): não devolve número errado como CONSISTENTE ===");
        PapelQuantitativo rdO = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrO = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnO = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdO.posicionar(new NumeroNatural(2000000000));
        vrO.posicionar(new NumeroInteiro(2000000000));
        ResultadoCalculo resO = relacao.calcularValorAusente(rdO, vrO, rnO);
        checar("2e9 + 2e9 não é representável -> NAO_RESOLVIVEL_NESTE_ESTADO (antes dava -294967296/CONSISTENTE)",
                resO.getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("não há valor calculado quando estoura", String.valueOf(resO.temValorCalculavel()), "false");
        checar("recalcularParaConsistencia também protege",
                relacao.recalcularParaConsistencia(rdO, vrO, rnO, rdO).getEstadoConsistencia().name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo rdO2 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrO2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnO2 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdO2.posicionar(new NumeroNatural(2000000000));
        vrO2.posicionar(new NumeroInteiro(2000000000));
        rnO2.posicionar(new NumeroNatural(1));
        checar("verificarConsistencia com soma não representável -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.verificarConsistencia(rdO2, vrO2, rnO2).name(),
                "NAO_RESOLVIVEL_NESTE_ESTADO");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE COMPARAÇÃO DE MEDIDAS PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
