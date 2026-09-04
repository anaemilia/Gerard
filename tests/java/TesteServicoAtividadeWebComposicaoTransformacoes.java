import gerard.aplicacao.portabilidade.ServicoAtividadeWebComposicaoTransformacoes;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

/**
 * Fase 2 da retomada da versão web: cobre
 * {@link ServicoAtividadeWebComposicaoTransformacoes}, o serviço de
 * aplicação que expõe Composição de Transformações na web com o paradigma
 * fiel ao desktop — os 6 papéis revelados desde o início, aluno escolhe a
 * operação certa em duas etapas sequenciais. Usa a mesma situação curada real
 * já usada por {@code TesteAvaliacaoEscolhaOperacaoRelacao} (Fase 1) e por
 * {@code TesteVisualComposicaoTransformacoesP2_1}.
 */
public final class TesteServicoAtividadeWebComposicaoTransformacoes {

    public static void main(String[] args) {
        List<SituacaoProblemaAditiva> validadas =
                new RepositorioSituacoesAditivas().listarValidadas();
        SituacaoProblemaAditiva situacao = localizar(
                validadas, "PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012");

        // --- estado inicial: as 3 transformações conhecidas (curadas), os 3
        // estados não — situacoes_vergnaud.tsv não cura estado_inicial/
        // estado_intermediario/estado_final para COMPOSICAO_TRANSFORMACOES,
        // e AvaliacaoEscolhaOperacaoRelacao não precisa deles ---
        ServicoAtividadeWebComposicaoTransformacoes servico =
                new ServicoAtividadeWebComposicaoTransformacoes(
                        "tentativa.teste.composicao_transformacoes", situacao);
        Map<String, Object> estadoInicial = servico.estadoAtual();
        exigir("COMPOSICAO_TRANSFORMACOES".equals(estadoInicial.get("categoria")),
                "categoria deveria ser COMPOSICAO_TRANSFORMACOES.");
        for (String chave : new String[] {"transformacao_1", "transformacao_2",
                "transformacao_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estadoInicial.get(chave);
            exigir(Boolean.TRUE.equals(papel.get("conhecido")),
                    "papel " + chave + " deveria estar conhecido (curado na base).");
            exigir(papel.get("valor") != null,
                    "papel " + chave + " deveria ter valor não nulo.");
        }
        for (String chave : new String[] {"estado_inicial", "estado_intermediario",
                "estado_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estadoInicial.get(chave);
            exigir(Boolean.FALSE.equals(papel.get("conhecido")),
                    "papel " + chave + " não deveria estar conhecido "
                            + "(não curado para esta categoria em situacoes_vergnaud.tsv).");
        }
        exigir(Boolean.FALSE.equals(estadoInicial.get("segunda_etapa_habilitada")),
                "segunda etapa não deveria estar habilitada antes da primeira.");
        exigir(Boolean.FALSE.equals(estadoInicial.get("concluida")),
                "atividade não deveria estar concluída no estado inicial.");
        exigir(estadoInicial.get("correta_entre_transformacoes") == null,
                "correta_entre_transformacoes deveria ser null antes de responder.");

        // --- segunda etapa antes da primeira: rejeitada ---
        boolean lancou = false;
        try {
            servico.escolherOperacao("ENTRE_ESTADO_E_TRANSFORMACAO", "SOMA");
        } catch (IllegalStateException esperada) {
            lancou = true;
        }
        exigir(lancou, "escolher a segunda etapa antes da primeira deveria lançar IllegalStateException.");

        // --- primeira etapa errada ---
        Map<String, Object> resultadoErrado =
                servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir(Boolean.FALSE.equals(resultadoErrado.get("aceita")),
                "SUBTRACAO deveria ser rejeitada (correta é SOMA, conforme Fase 1).");
        exigir("operacao.explicacao.composicaoTransformacoes.soma"
                        .equals(resultadoErrado.get("chave_mensagem")),
                "chave de explicação errada para a primeira etapa incorreta.");
        @SuppressWarnings("unchecked")
        Map<String, Object> estadoAposErro =
                (Map<String, Object>) resultadoErrado.get("estado");
        exigir(Boolean.FALSE.equals(estadoAposErro.get("segunda_etapa_habilitada")),
                "segunda etapa continua desabilitada após a primeira errada.");

        // --- primeira etapa certa ---
        Map<String, Object> resultadoCerto =
                servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SOMA");
        exigir(Boolean.TRUE.equals(resultadoCerto.get("aceita")),
                "SOMA deveria ser aceita na primeira etapa.");
        @SuppressWarnings("unchecked")
        Map<String, Object> estadoAposCerto =
                (Map<String, Object>) resultadoCerto.get("estado");
        exigir(Boolean.TRUE.equals(estadoAposCerto.get("segunda_etapa_habilitada")),
                "segunda etapa deveria habilitar depois da primeira correta.");
        exigir(Boolean.FALSE.equals(estadoAposCerto.get("concluida")),
                "atividade não deveria estar concluída só com a primeira etapa.");

        // --- segunda etapa certa: conclui ---
        Map<String, Object> resultadoFinal =
                servico.escolherOperacao("ENTRE_ESTADO_E_TRANSFORMACAO", "SOMA");
        exigir(Boolean.TRUE.equals(resultadoFinal.get("aceita")),
                "SOMA deveria ser aceita na segunda etapa (conforme Fase 1).");
        @SuppressWarnings("unchecked")
        Map<String, Object> estadoFinal =
                (Map<String, Object>) resultadoFinal.get("estado");
        exigir(Boolean.TRUE.equals(estadoFinal.get("concluida")),
                "atividade deveria estar concluída com as duas etapas certas.");

        // --- reiniciar: zera as duas escolhas ---
        Map<String, Object> estadoReiniciado = servico.reiniciar();
        exigir(Boolean.FALSE.equals(estadoReiniciado.get("segunda_etapa_habilitada")),
                "reiniciar deveria voltar segunda_etapa_habilitada para false.");
        exigir(Boolean.FALSE.equals(estadoReiniciado.get("concluida")),
                "reiniciar deveria voltar concluida para false.");
        exigir(estadoReiniciado.get("escolha_entre_transformacoes") == null,
                "reiniciar deveria zerar escolha_entre_transformacoes.");
        exigir(estadoReiniciado.get("escolha_entre_estado_transformacao") == null,
                "reiniciar deveria zerar escolha_entre_estado_transformacao.");

        System.out.println("APROVADO: ServicoAtividadeWebComposicaoTransformacoes cobre "
                + "sequenciamento, acerto/erro nas duas etapas e reinício.");
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

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
