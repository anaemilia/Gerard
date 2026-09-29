package gerard.campoaditivo.curadoria.sinal;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.campoaditivo.OperacaoAditiva;
import gerard.dominio.campoaditivo.situacao.CriterioOperacaoModelagem;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;
import java.util.Arrays;

/**
 * Avalia a escolha de operação (soma/subtração) que o aluno faz entre dois
 * papéis já conhecidos, nas 3 categorias que usam esse protocolo:
 * Transformação de Relação, Composição de Relações e Composição de
 * Transformações (esta última com duas operações distintas — ver
 * {@link TipoOperacaoSeletor}).
 *
 * Extraído de {@code gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno} em
 * 2026-09-03: a derivação da operação correta a partir da situação curada,
 * e a comparação com a escolha do aluno não
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

    public static ResultadoEscolhaOperacaoModelagem registrarEscolha(
            TipoSituacaoAditiva categoria,
            TipoOperacaoSeletor tipo,
            OpcaoOperacaoCuradoria escolha,
            OpcaoOperacaoCuradoria esperada) {
        boolean estadoTransformacao =
                tipo == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO;
        String proprietario = estadoTransformacao
                ? "relacao.operacao.estadoTransformacao"
                : "relacao.operacao.entrePapeis";
        ContextoAcaoInstrumental contexto = new ContextoAcaoInstrumental(
                estadoTransformacao
                        ? "Escolher a operação (soma/subtração) entre estado inicial e transformação"
                        : "Escolher a operação (soma/subtração) da situação de Relações",
                estadoTransformacao
                        ? "Seletor de operação à esquerda do círculo inferior do diagrama"
                        : "Seletor de operação perto da seta do diagrama",
                estadoTransformacao
                        ? "Operação entre estado inicial e transformação resultante"
                        : "Operação entre os papéis de Relações",
                escolha.name(),
                "OBJ4",
                estadoTransformacao
                        ? "OPERACAO_ESTADO_TRANSFORMACAO_ALUNO"
                        : "OPERACAO_RELACAO_ALUNO",
                "",
                "Escolha de operação registrada",
                Arrays.asList(proprietario, proprietario + ".escolha"));
        CriterioOperacaoModelagem criterio = new CriterioOperacaoModelagem(
                proprietario, converter(esperada),
                Arrays.asList(proprietario, proprietario + ".escolha"));
        return criterio.avaliar(converter(escolha), categoria, contexto);
    }

    private static OperacaoAditiva converter(OpcaoOperacaoCuradoria opcao) {
        if (opcao == OpcaoOperacaoCuradoria.SOMA) {
            return OperacaoAditiva.SOMA;
        }
        if (opcao == OpcaoOperacaoCuradoria.SUBTRACAO) {
            return OperacaoAditiva.SUBTRACAO;
        }
        throw new IllegalArgumentException("operação curada válida é obrigatória");
    }

}
