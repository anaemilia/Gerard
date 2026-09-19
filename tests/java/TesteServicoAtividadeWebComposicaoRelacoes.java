import gerard.aplicacao.portabilidade.ServicoAtividadeWebComposicaoRelacoes;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

/**
 * Continuação da Fase 2 da retomada da versão web: cobre
 * {@link ServicoAtividadeWebComposicaoRelacoes}, a versão mais simples (3
 * papéis, uma única etapa) do mesmo paradigma "escolher operação" de
 * {@link gerard.aplicacao.portabilidade.ServicoAtividadeWebComposicaoTransformacoes}.
 * Usa a mesma situação curada real já usada por
 * {@code TesteAvaliacaoEscolhaOperacaoRelacao} (Fase 1).
 */
public final class TesteServicoAtividadeWebComposicaoRelacoes {

    public static void main(String[] args) {
        List<SituacaoProblemaAditiva> validadas =
                new RepositorioSituacoesAditivas().listarValidadas();
        SituacaoProblemaAditiva situacao = localizar(
                validadas, "PO_COMPOSICAO_RELACOES_idades_642701604");

        ServicoAtividadeWebComposicaoRelacoes servico =
                new ServicoAtividadeWebComposicaoRelacoes(
                        "tentativa.teste.composicao_relacoes", situacao);

        Map<String, Object> estadoInicial = servico.estadoAtual();
        exigir("COMPOSICAO_RELACOES".equals(estadoInicial.get("categoria")),
                "categoria deveria ser COMPOSICAO_RELACOES.");
        for (String chave : new String[] {"relacao_1", "relacao_2", "relacao_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estadoInicial.get(chave);
            exigir(Boolean.FALSE.equals(papel.get("conhecido")) && papel.get("valor") == null,
                    "papel " + chave + " aguarda posicionamento");
            String id = String.valueOf(papel.get("id"));
            servico.posicionarValorConhecido(id, id);
            // Toda relação precisa de representação de sinal (auditoria de
            // acoplamento Main/web, 2026-09-19) — posicionar só revela a
            // magnitude; o sinal exige escolha explícita, mesmo protocolo já
            // usado por ServicoAtividadeWebComparacaoMedidas.
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> posicionado = (Map<String, Object>) servico.estadoAtual().get(chave);
            exigir(Boolean.TRUE.equals(posicionado.get("conhecido")) && posicionado.get("valor") != null,
                    "posicionamento revela o valor curado");
        }
        exigir(Boolean.FALSE.equals(estadoInicial.get("concluida")),
                "atividade não deveria estar concluída no estado inicial.");
        exigir(estadoInicial.get("correta") == null,
                "correta deveria ser null antes de responder.");

        // --- resposta errada ---
        Map<String, Object> resultadoErrado = servico.escolherOperacao(
                "ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir(Boolean.FALSE.equals(resultadoErrado.get("aceita")),
                "SUBTRACAO deveria ser rejeitada (correta é SOMA, conforme Fase 1).");
        exigir(gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao
                        .preencherPersonagensCurados(gerard.i18n.ServicoLocalizacao.getInstancia()
                                .texto("operacao.explicacao.composicaoRelacoes.soma"), situacao)
                        .equals(resultadoErrado.get("chave_mensagem")),
                "chave de explicação errada para a resposta incorreta.");
        @SuppressWarnings("unchecked")
        Map<String, Object> estadoAposErro =
                (Map<String, Object>) resultadoErrado.get("estado");
        exigir(Boolean.FALSE.equals(estadoAposErro.get("concluida")),
                "atividade não deveria estar concluída após resposta errada.");

        // --- resposta certa ---
        Map<String, Object> resultadoCerto = servico.escolherOperacao(
                "ENTRE_TRANSFORMACOES", "SOMA");
        exigir(Boolean.TRUE.equals(resultadoCerto.get("aceita")),
                "SOMA deveria ser aceita.");
        @SuppressWarnings("unchecked")
        Map<String, Object> estadoAposCerto =
                (Map<String, Object>) resultadoCerto.get("estado");
        exigir(Boolean.TRUE.equals(estadoAposCerto.get("concluida")),
                "atividade deveria estar concluída após resposta certa.");

        // --- reiniciar ---
        Map<String, Object> estadoReiniciado = servico.reiniciar();
        exigir(Boolean.FALSE.equals(estadoReiniciado.get("concluida")),
                "reiniciar deveria voltar concluida para false.");
        exigir(estadoReiniciado.get("escolha_operacao") == null,
                "reiniciar deveria zerar escolha_operacao.");

        System.out.println("APROVADO: ServicoAtividadeWebComposicaoRelacoes cobre "
                + "acerto/erro e reinício.");

        testarSituacaoSemOperacaoRelacaoCurada();
    }

    /**
     * "dinheiro" (Carlos/João/Pedro) não tem operacao_relacao curado (ver
     * TSV) -- mesma guarda de SeletorOperacaoRelacaoAluno.ativar() no
     * desktop: "situações antigas, sem esse campo preenchido, não mostram o
     * seletor (nada a avaliar)". Antes da correção (auditoria de
     * acoplamento Main/web, 2026-09-19), o web oferecia e avaliava a escolha
     * mesmo sem resposta curada nenhuma pra comparar.
     */
    @SuppressWarnings("unchecked")
    private static void testarSituacaoSemOperacaoRelacaoCurada() {
        SituacaoProblemaAditiva situacao = localizar(
                new RepositorioSituacoesAditivas().listarValidadas(),
                "PO_COMPOSICAO_RELACOES_dinheiro_161113042");
        ServicoAtividadeWebComposicaoRelacoes servico =
                new ServicoAtividadeWebComposicaoRelacoes(
                        "tentativa.teste.composicao_relacoes.sem_operacao", situacao);

        for (String chave : new String[] {"relacao_1", "relacao_2"}) {
            Map<String, Object> papel = (Map<String, Object>) servico.estadoAtual().get(chave);
            servico.posicionarValorConhecido(String.valueOf(papel.get("id")),
                    String.valueOf(papel.get("id")));
            String id = String.valueOf(papel.get("id"));
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                servico.escolherSinalNumeroRelativo(id, "+");
            }
        }

        Map<String, Object> estado = servico.estadoAtual();
        for (Object item : (List<Object>) estado.get("acoes_disponiveis")) {
            exigir(!"ESCOLHER_OPERACAO_RELACAO".equals(((Map<String, Object>) item).get("id")),
                    "seletor não deveria aparecer sem operacao_relacao curado");
        }
        exigir(estado.get("escolha_operacao") == null, "escolha_operacao deveria ficar null");
        exigir(estado.get("correta") == null, "correta deveria ficar null sem operação curada");
        exigir(Boolean.FALSE.equals(estado.get("concluida")),
                "não deveria completar sem operação curada pra avaliar");

        System.out.println("APROVADO: seletor de operação fica ausente quando a situação "
                + "não tem operacao_relacao curado, mesma guarda do desktop.");
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
