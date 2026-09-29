package gerard.campoaditivo.representacao.texto;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

/**
 * Realiza como texto localizado a explicacao de uma operacao ja avaliada.
 * Nao determina a operacao esperada nem reavalia a escolha do participante.
 */
public final class RealizadorTextoExplicacaoOperacaoRelacao {

    private RealizadorTextoExplicacaoOperacaoRelacao() {
    }

    public static String realizar(
            TipoSituacaoAditiva categoria,
            TipoOperacaoSeletor tipo,
            OpcaoOperacaoCuradoria operacaoEsperada,
            SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao) {
        String chave = chaveExplicacao(categoria, tipo, operacaoEsperada);
        if (chave == null || situacao == null) {
            return null;
        }
        ServicoLocalizacao loc = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        return preencherPersonagensCurados(loc.texto(chave), situacao);
    }

    private static String chaveExplicacao(
            TipoSituacaoAditiva categoria,
            TipoOperacaoSeletor tipo,
            OpcaoOperacaoCuradoria operacao) {
        boolean soma = operacao == OpcaoOperacaoCuradoria.SOMA;
        if (tipo == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO) {
            return soma
                    ? "operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.soma"
                    : "operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.subtracao";
        }
        if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return soma ? "operacao.explicacao.transformacaoRelacao.soma"
                    : "operacao.explicacao.transformacaoRelacao.subtracao";
        }
        if (categoria == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return soma ? "operacao.explicacao.composicaoRelacoes.soma"
                    : "operacao.explicacao.composicaoRelacoes.subtracao";
        }
        if (categoria == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return soma ? "operacao.explicacao.composicaoTransformacoes.soma"
                    : "operacao.explicacao.composicaoTransformacoes.subtracao";
        }
        return null;
    }

    private static String preencherPersonagensCurados(
            String modelo, SituacaoProblemaAditiva situacao) {
        return textoOu(modelo)
                .replace("{Personagem_1}", textoOu(situacao.getPersonagem1()))
                .replace("{Personagem_2}", textoOu(situacao.getPersonagem2()))
                .replace("{Personagem_3}", textoOu(situacao.getPersonagem3()));
    }

    private static String textoOu(String valor) {
        return valor == null ? "" : valor;
    }
}
