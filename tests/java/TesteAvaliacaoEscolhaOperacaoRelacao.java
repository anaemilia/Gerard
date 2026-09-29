import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.representacao.texto.RealizadorTextoExplicacaoOperacaoRelacao;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.i18n.ServicoLocalizacao;
import java.util.List;

/**
 * Fase 1 da retomada da versão web: cobre
 * {@link AvaliacaoEscolhaOperacaoRelacao}, extraída de
 * {@code SeletorOperacaoRelacaoAluno} (Swing) em 2026-09-03. Usa situações
 * curadas reais das 3 categorias que usam este protocolo — mesmo padrão de
 * {@code TesteServicoAtividadeWebTransformacaoMedidas}.
 */
public final class TesteAvaliacaoEscolhaOperacaoRelacao {

    public static void main(String[] args) {
        List<SituacaoProblemaAditiva> validadas =
                new RepositorioSituacoesAditivas().listarValidadas();

        SituacaoProblemaAditiva transformacaoRelacao = localizar(
                validadas, "PO_TRANSFORMACAO_RELACAO_bonecas_620955739");
        SituacaoProblemaAditiva composicaoRelacoes = localizar(
                validadas, "PO_COMPOSICAO_RELACOES_idades_642701604");
        SituacaoProblemaAditiva composicaoTransformacoes = localizar(
                validadas, "PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012");
        SituacaoProblemaAditiva composicaoMedidas = primeiraDoTipo(
                validadas, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);

        // --- aplicavel ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO),
                "Transformação de Relação deveria ser aplicável.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.COMPOSICAO_RELACOES),
                "Composição de Relações deveria ser aplicável.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES),
                "Composição de Transformações deveria ser aplicável.");
        exigir(!AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS),
                "Composição de Medidas não deveria ser aplicável.");
        exigir(!AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS),
                "Transformação de Medidas não deveria ser aplicável.");
        exigir(!AvaliacaoEscolhaOperacaoRelacao.aplicavel(TipoSituacaoAditiva.COMPARACAO_MEDIDAS),
                "Comparação de Medidas não deveria ser aplicável.");

        // --- determinarOperacaoCorreta: ENTRE_TRANSFORMACOES lê operacao_relacao ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, transformacaoRelacao,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) == OpcaoOperacaoCuradoria.SUBTRACAO,
                "Transformação de Relação (bonecas) deveria ter operação curada SUBTRACAO.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_RELACOES, composicaoRelacoes,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) == OpcaoOperacaoCuradoria.SOMA,
                "Composição de Relações (idades) deveria ter operação curada SOMA.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, composicaoTransformacoes,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) == OpcaoOperacaoCuradoria.SOMA,
                "Composição de Transformações (bolas), primeira operação, deveria ser SOMA.");

        // --- ENTRE_ESTADO_E_TRANSFORMACAO só existe em Composição de Transformações ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, composicaoTransformacoes,
                        TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO) == OpcaoOperacaoCuradoria.SOMA,
                "Composição de Transformações (bolas), segunda operação, deveria ser SOMA "
                        + "(lida de operacao_estado_transformacao, não de operacao_relacao).");
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, transformacaoRelacao,
                        TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO)
                        == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                "ENTRE_ESTADO_E_TRANSFORMACAO fora de Composição de Transformações deveria ser no-op.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_RELACOES, composicaoRelacoes,
                        TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO)
                        == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                "ENTRE_ESTADO_E_TRANSFORMACAO fora de Composição de Transformações deveria ser no-op (2).");

        // --- categoria não aplicável ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, composicaoMedidas,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                "Categoria não aplicável deveria devolver NAO_SELECIONADO.");

        // --- argumentos nulos não lançam exceção ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, null,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                "situação nula deveria devolver NAO_SELECIONADO, não lançar exceção.");
        exigir(AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, composicaoTransformacoes, null)
                        == OpcaoOperacaoCuradoria.SOMA,
                "papel nulo deveria equivaler a ENTRE_TRANSFORMACOES.");

        // --- respondeuCorretamente ---
        exigir(AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(
                OpcaoOperacaoCuradoria.SOMA, OpcaoOperacaoCuradoria.SOMA),
                "Escolha igual à correta deveria ser aceita.");
        exigir(!AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(
                OpcaoOperacaoCuradoria.SOMA, OpcaoOperacaoCuradoria.SUBTRACAO),
                "Escolha diferente da correta deveria ser rejeitada.");
        exigir(!AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(
                OpcaoOperacaoCuradoria.NAO_SELECIONADO, OpcaoOperacaoCuradoria.NAO_SELECIONADO),
                "Duas escolhas NAO_SELECIONADO não deveriam contar como resposta correta.");

        // A avaliação mantém somente a decisão semântica. A realização da
        // explicação e a substituição dos personagens pertencem ao componente
        // textual público, sem reabrir seus detalhes internos neste teste.
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        String explicacaoTransformacaoRelacao = RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, OpcaoOperacaoCuradoria.SUBTRACAO,
                transformacaoRelacao, localizacao);
        String explicacaoComposicaoRelacoes = RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, OpcaoOperacaoCuradoria.SOMA,
                composicaoRelacoes, localizacao);
        String explicacaoPrimeiraTransformacao = RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, OpcaoOperacaoCuradoria.SOMA,
                composicaoTransformacoes, localizacao);
        String explicacaoSegundaTransformacao = RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO, OpcaoOperacaoCuradoria.SOMA,
                composicaoTransformacoes, localizacao);
        for (String explicacao : new String[] {explicacaoTransformacaoRelacao,
                explicacaoComposicaoRelacoes, explicacaoPrimeiraTransformacao,
                explicacaoSegundaTransformacao}) {
            exigir(explicacao != null && !explicacao.trim().isEmpty(),
                    "A explicação aplicável deveria ser realizada.");
            exigir(!explicacao.contains("{Personagem_1}")
                            && !explicacao.contains("{Personagem_2}")
                            && !explicacao.contains("{Personagem_3}"),
                    "Marcadores de personagem deveriam ter sido substituídos.");
        }
        exigir(RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                        TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, OpcaoOperacaoCuradoria.SOMA,
                        composicaoMedidas, localizacao) == null,
                "Categoria sem explicação definida deveria devolver null.");

        System.out.println("APROVADO: AvaliacaoEscolhaOperacaoRelacao cobre as 3 categorias "
                + "(Transformação de Relação, Composição de Relações, Composição de Transformações) "
                + "com dados curados reais.");
    }

    private static SituacaoProblemaAditiva localizar(
            List<SituacaoProblemaAditiva> situacoes, String id) {
        for (SituacaoProblemaAditiva situacao : situacoes) {
            if (id.equals(situacao.getId())) {
                return situacao;
            }
        }
        throw new AssertionError("Situação curada não encontrada: " + id);
    }

    private static SituacaoProblemaAditiva primeiraDoTipo(
            List<SituacaoProblemaAditiva> situacoes, TipoSituacaoAditiva tipo) {
        for (SituacaoProblemaAditiva situacao : situacoes) {
            if (situacao.getTipo() == tipo) {
                return situacao;
            }
        }
        throw new AssertionError("Nenhuma situação curada do tipo " + tipo);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
