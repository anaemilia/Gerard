package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

/**
 * Escolha de operação (soma/subtração) nos seletores web. O mesmo critério
 * do desktop (CriterioOperacaoModelagem, via
 * AvaliacaoEscolhaOperacaoRelacao.registrarEscolha) constitui a ação e decide
 * a correção; o registro segue pela tentativa até a persistência. Sem
 * operação curada válida não há critério: nada é constituído.
 */
final class EscolhaOperacaoWeb {

    private EscolhaOperacaoWeb() {
    }

    static ResultadoEscolhaOperacaoModelagem registrar(TipoSituacaoAditiva categoria,
            TipoOperacaoSeletor seletor, OpcaoOperacaoCuradoria escolha,
            OpcaoOperacaoCuradoria esperada, EscopoTentativaWeb escopo) {
        if (escolha == null || esperada == null
                || !escolha.isEscolhaValida() || !esperada.isEscolhaValida()) {
            return null;
        }
        ResultadoEscolhaOperacaoModelagem registro =
                AvaliacaoEscolhaOperacaoRelacao.registrarEscolha(
                        categoria, seletor, escolha, esperada);
        escopo.persistir(registro);
        return registro;
    }
}
