package gerard.campoaditivo.curadoria.sinal;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Avalia a escolha de operação (soma/subtração) que o aluno faz entre dois
 * papéis já conhecidos, nas 3 categorias que usam esse protocolo:
 * Transformação de Relação, Composição de Relações e Composição de
 * Transformações (esta última com duas operações distintas — ver
 * {@link TipoOperacaoSeletor}).
 *
 * Extraído de {@code gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno} em
 * 2026-09-03: a derivação da operação correta a partir da situação curada,
 * a comparação com a escolha do aluno e a seleção da chave de explicação não
 * dependiam de Swing, geometria nem estado do widget — apenas não tinham,
 * até então, um proprietário fora da tela. O widget preserva a mesma API
 * pública e passa a delegar aqui; hit-testing, posicionamento e desenho
 * continuam no adaptador desktop.
 */
public final class AvaliacaoEscolhaOperacaoRelacao {

    /**
     * Qual das duas operações de Composição de Transformações está sendo
     * avaliada. Nas demais categorias, que só têm uma operação, use sempre
     * ENTRE_TRANSFORMACOES — ENTRE_ESTADO_E_TRANSFORMACAO nelas é no-op
     * (ver {@link #determinarOperacaoCorreta}).
     */
    public enum TipoOperacaoSeletor {
        ENTRE_TRANSFORMACOES,
        ENTRE_ESTADO_E_TRANSFORMACAO
    }

    private AvaliacaoEscolhaOperacaoRelacao() {
    }

    public static boolean aplicavel(TipoSituacaoAditiva tipo) {
        return tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                || tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES
                || tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
    }

    /**
     * Determina a operação correta para o par de papéis identificado por
     * {@code papel}, a partir dos campos curados da situação. Devolve
     * {@code NAO_SELECIONADO} quando: a categoria não é aplicável, o papel é
     * {@code ENTRE_ESTADO_E_TRANSFORMACAO} fora de
     * {@code COMPOSICAO_TRANSFORMACOES} (essa segunda operação só existe
     * ali), ou o campo curado correspondente está vazio ou não representa
     * soma/subtração (base antiga, sem esse campo preenchido — nada a
     * perguntar).
     */
    public static OpcaoOperacaoCuradoria determinarOperacaoCorreta(
            TipoSituacaoAditiva tipo, SituacaoProblemaAditiva situacao,
            TipoOperacaoSeletor papel) {
        if (!aplicavel(tipo) || situacao == null) {
            return OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        }
        TipoOperacaoSeletor papelEfetivo = papel == null
                ? TipoOperacaoSeletor.ENTRE_TRANSFORMACOES : papel;
        boolean papelEstadoTransformacao =
                papelEfetivo == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO;
        if (papelEstadoTransformacao && tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        }
        String operacaoCurada = papelEstadoTransformacao
                ? situacao.getOperacaoEstadoTransformacao()
                : situacao.getOperacaoRelacao();
        OpcaoOperacaoCuradoria escolhaCorreta = OpcaoOperacaoCuradoria.aPartirDoEstado(operacaoCurada);
        return escolhaCorreta.isEscolhaValida()
                ? escolhaCorreta : OpcaoOperacaoCuradoria.NAO_SELECIONADO;
    }

    public static boolean respondeuCorretamente(
            OpcaoOperacaoCuradoria escolhaAluno, OpcaoOperacaoCuradoria escolhaCorreta) {
        return escolhaAluno != OpcaoOperacaoCuradoria.NAO_SELECIONADO
                && escolhaAluno == escolhaCorreta;
    }

    /** Chave i18n da explicação exibida quando o aluno erra a operação — {@code null} se não houver. */
    public static String chaveExplicacao(TipoSituacaoAditiva tipo,
            TipoOperacaoSeletor papel, OpcaoOperacaoCuradoria operacao) {
        boolean soma = operacao == OpcaoOperacaoCuradoria.SOMA;
        if (papel == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO) {
            // Só existe para Composição de Transformações (ver determinarOperacaoCorreta()).
            return soma ? "operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.soma"
                    : "operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.subtracao";
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return soma ? "operacao.explicacao.transformacaoRelacao.soma"
                    : "operacao.explicacao.transformacaoRelacao.subtracao";
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return soma ? "operacao.explicacao.composicaoRelacoes.soma"
                    : "operacao.explicacao.composicaoRelacoes.subtracao";
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return soma ? "operacao.explicacao.composicaoTransformacoes.soma"
                    : "operacao.explicacao.composicaoTransformacoes.subtracao";
        }
        return null;
    }

    /** Substitui {@code {Personagem_1/2/3}} pelos campos curados homônimos — não associa por posição. */
    public static String preencherPersonagensCurados(String modelo, SituacaoProblemaAditiva situacao) {
        String personagem1 = textoOu(situacao.getPersonagem1());
        String personagem2 = textoOu(situacao.getPersonagem2());
        String personagem3 = textoOu(situacao.getPersonagem3());
        return textoOu(modelo)
                .replace("{Personagem_1}", personagem1)
                .replace("{Personagem_2}", personagem2)
                .replace("{Personagem_3}", personagem3);
    }

    private static String textoOu(String valor) {
        return valor == null ? "" : valor;
    }
}
