import gerard.aplicacao.portabilidade.ServicoAtividadeWebComposicaoTransformacoes;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.representacao.texto.RealizadorTextoExplicacaoOperacaoRelacao;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.i18n.ServicoLocalizacao;
import java.util.List;
import java.util.Map;

/**
 * Fase 2 da retomada da versão web: cobre
 * {@link ServicoAtividadeWebComposicaoTransformacoes}, o serviço de
 * aplicação que expõe Composição de Transformações na web com o paradigma
 * fiel ao desktop — os papéis posicionados antes da escolha, aluno escolhe a
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
        String alvoIncognita = new gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada()
                .resolver(situacao).getChaveEfetiva();
        Map<String, Object> estadoInicial = servico.estadoAtual();
        exigir("COMPOSICAO_TRANSFORMACOES".equals(estadoInicial.get("categoria")),
                "categoria deveria ser COMPOSICAO_TRANSFORMACOES.");
        for (String chave : new String[] {"transformacao_1", "transformacao_2",
                "transformacao_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estadoInicial.get(chave);
            exigir(Boolean.FALSE.equals(papel.get("conhecido")) && papel.get("valor") == null,
                    "papel " + chave + " aguarda posicionamento");
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvoIncognita)) {
                continue;        // a incógnita não é pré-posicionada: engata e recebe o valor adiante
            }
            servico.posicionarValorConhecido(id, id);
            // Toda transformação precisa de representação de sinal
            // (auditoria de acoplamento Main/web, 2026-09-19) — posicionar
            // só revela a magnitude; o sinal exige escolha explícita, mesmo
            // protocolo já usado por ServicoAtividadeWebComparacaoMedidas.
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> posicionado = (Map<String, Object>) servico.estadoAtual().get(chave);
            exigir(Boolean.TRUE.equals(posicionado.get("conhecido")) && posicionado.get("valor") != null,
                    "posicionamento revela o valor curado");
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

        // --- a incógnita curada: engatar e propor o valor certo (conclusão só depois dela) ---
        servico.engatarIncognita(alvoIncognita, alvoIncognita);
        int valorIncognita = SemanticaCuradaSituacao.buscar(situacao, null, alvoIncognita).getValorInteiro();
        Map<String, Object> aceitaIncognita = servico.proporValor(alvoIncognita, valorIncognita);
        exigir(Boolean.TRUE.equals(aceitaIncognita.get("aceita")),
                "a proposta curada deveria preencher a incógnita.");

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
        exigir(RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                        OpcaoOperacaoCuradoria.SOMA, situacao,
                        ServicoLocalizacao.getInstancia())
                        .equals(resultadoErrado.get("chave_mensagem")),
                "explicação errada para a primeira etapa incorreta.");
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

        incognitaSemOperacaoCurada(validadas, "PO_COMPOSICAO_TRANSFORMACOES_figurinhas_2105853102");
        incognitaSemOperacaoCurada(validadas, "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114");

        System.out.println("APROVADO: ServicoAtividadeWebComposicaoTransformacoes cobre "
                + "sequenciamento, acerto/erro nas duas etapas, a incógnita sem operação curada e reinício.");
    }

    /**
     * Sem operação curada, a atividade NÃO abre concluída: existe a incógnita, que precisa de
     * tentativas do participante; três valores errados seguidos levam ao limite (historinha).
     */
    private static void incognitaSemOperacaoCurada(List<SituacaoProblemaAditiva> validadas, String id) {
        SituacaoProblemaAditiva situacao = localizar(validadas, id);
        ServicoAtividadeWebComposicaoTransformacoes servico =
                new ServicoAtividadeWebComposicaoTransformacoes("tentativa.teste." + id, situacao);
        String alvo = new gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada()
                .resolver(situacao).getChaveEfetiva();
        exigir(Boolean.FALSE.equals(servico.estadoAtual().get("concluida")),
                id + ": não abre concluída (há uma incógnita a preencher).");
        @SuppressWarnings("unchecked")
        Map<String, Object> inicial = servico.estadoAtual();
        for (Object acaoObj : (List<?>) inicial.get("acoes_disponiveis")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> acao = (Map<String, Object>) acaoObj;
            if ("POSICIONAR_CONHECIDO".equals(acao.get("id"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> corpo = (Map<String, Object>) acao.get("corpo");
                String papel = String.valueOf(corpo.get("papel_id"));
                exigir(!papel.equals(alvo), id + ": a incógnita não é oferecida como dado conhecido.");
            }
        }
        for (Object acaoObj : (List<?>) inicial.get("acoes_disponiveis")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> acao = (Map<String, Object>) acaoObj;
            if ("POSICIONAR_CONHECIDO".equals(acao.get("id"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> corpo = (Map<String, Object>) acao.get("corpo");
                String papel = String.valueOf(corpo.get("papel_id"));
                servico.posicionarValorConhecido(papel, papel);
                if (papel.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                    int curado = SemanticaCuradaSituacao.buscar(situacao, null, papel).getValorInteiro();
                    servico.escolherSinalNumeroRelativo(papel, curado < 0 ? "-" : "+");
                }
            }
        }
        servico.engatarIncognita(alvo, alvo);
        Map<String, Object> ultima = null;
        for (int i = 0; i < 3; i++) {
            ultima = servico.proporValor(alvo, 900 + i);
            exigir(Boolean.FALSE.equals(ultima.get("aceita")), id + ": valor errado é rejeitado.");
            if (i < 2) {
                servico.responderConfirmacaoValor(alvo, true, 900 + i);
            }
        }
        exigir(Boolean.TRUE.equals(ultima.get("limite_atingido")), id + ": a 3ª rejeição atinge o limite.");
        exigir(ultima.get("chave_mensagem") == null, id + ": no limite não há aviso.");
        @SuppressWarnings("unchecked")
        Map<String, Object> estado = (Map<String, Object>) ultima.get("estado");
        exigir(Boolean.FALSE.equals(estado.get("concluida")), id + ": valor errado não conclui.");
        exigir(Boolean.TRUE.equals(estado.get("escalada_no_limite")), id + ": escalada no limite.");
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
