import gerard.aplicacao.portabilidade.ServicoAtividadeWebTransformacaoRelacao;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

/**
 * Cobre {@link ServicoAtividadeWebTransformacaoRelacao} (caminho básico, sem
 * narrativa rica), incluindo a escolha explícita de sinal recém-adicionada
 * (auditoria de acoplamento Main/web, 2026-09-19 — "toda relação/
 * transformação precisa de representação de sinal", mesmo protocolo já
 * usado por {@code ServicoAtividadeWebComparacaoMedidas}). Antes desta
 * correção, posicionar um papel conhecido aplicava o valor curado com sinal
 * direto, sem o combobox positivo/negativo que o desktop sempre exigiu.
 */
public final class TesteServicoAtividadeWebTransformacaoRelacao {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // "bonecas" (a outra situação original desta categoria) exige
        // orientação narrativa (quem recebeu o evento) para somar certo --
        // "3 + 5" ingênuo dá 8, mas o valor curado do alvo é "2", porque é a
        // mãe de MARIA quem compra, invertendo o benefício -- só a "ponte
        // rica" (removida, ver TesteConversorTransformacaoRelacaoRica)
        // resolve isso. "figurinhas" soma direto sem inversão (5 + 4 = 9,
        // igual ao curado) e é a mesma situação que o Javadoc da classe cita
        // como confirmada contra o desktop sem narrativa rica.
        SituacaoProblemaAditiva situacao = localizar(
                new RepositorioSituacoesAditivas().listarValidadas(),
                "PO_TRANSFORMACAO_RELACAO_figurinhas_1283157032");
        exigir(situacao.getTipo() == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                "situação escolhida não é Transformação de Relação");
        ServicoAtividadeWebTransformacaoRelacao servico =
                new ServicoAtividadeWebTransformacaoRelacao(
                        "tentativa.web.teste.transformacao_relacao", situacao);
        Map<String, Object> estado = servico.estadoAtual();
        exigir("TRANSFORMACAO_RELACAO".equals(estado.get("categoria")),
                "categoria deveria ser TRANSFORMACAO_RELACAO.");
        String alvo = String.valueOf(estado.get("papel_desconhecido_original"));
        exigir(Boolean.FALSE.equals(estado.get("concluida")), "começa incompleta");

        for (Object item : (List<Object>) estado.get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) item;
            exigir(Boolean.FALSE.equals(papel.get("conhecido")) && papel.get("valor") == null,
                    "papéis começam sem valores posicionados");
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            servico.posicionarValorConhecido(id, id);
            // Toda relação/transformação precisa de representação de sinal —
            // posicionar só revela a magnitude; o sinal exige escolha
            // explícita via escolherSinalNumeroRelativo (nunca aplicado de
            // imediato).
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> papeisAposPosicionar =
                    (Map<String, Object>) servico.estadoAtual();
            List<Object> lista = (List<Object>) papeisAposPosicionar.get("papeis");
            boolean encontradoConhecido = false;
            for (Object outro : lista) {
                Map<String, Object> candidato = (Map<String, Object>) outro;
                if (id.equals(candidato.get("id"))) {
                    encontradoConhecido = Boolean.TRUE.equals(candidato.get("conhecido"))
                            && candidato.get("valor") != null;
                }
            }
            exigir(encontradoConhecido, "posicionamento revela o valor curado para " + id);
        }

        Integer esperado = SemanticaCuradaSituacao.buscar(
                situacao, null, alvo).getValorInteiro();
        exigir(esperado != null, "valor normativo da incógnita ausente");
        int errado = esperado.intValue() == Integer.MAX_VALUE
                ? esperado.intValue() - 1 : esperado.intValue() + 1;

        servico.engatarIncognita(alvo, alvo);
        Map<String, Object> rejeicao = servico.proporValor(alvo, errado);
        exigir(Boolean.FALSE.equals(rejeicao.get("aceita")),
                "proposta incorreta aceita");
        exigir(Boolean.FALSE.equals(((Map<String, Object>) rejeicao.get("estado"))
                .get("concluida")), "rejeição alterou o estado semântico");

        Map<String, Object> aceite = servico.proporValor(alvo, esperado.intValue());
        exigir(Boolean.TRUE.equals(aceite.get("aceita")),
                "valor curado correto rejeitado");
        exigir(Boolean.TRUE.equals(((Map<String, Object>) aceite.get("estado"))
                .get("concluida")), "relação não concluiu após aceite");

        System.out.println("APROVADO: Transformação de Relação (caminho básico) "
                + "funcional via serviço web, incluindo escolha explícita de sinal.");

        testarSegundoGateDeOperacao();
    }

    /**
     * "bonecas" tem operacao_relacao=subtracao curado (ver TSV) — ao
     * contrário de "figurinhas", o desktop ativa aqui o segundo gate
     * independente (SeletorOperacaoRelacaoAluno, ver Javadoc da classe
     * principal). Os dois gates não precisam concordar matematicamente: a
     * incógnita valida contra a relação estrutural simples (RelacaoFinal =
     * RelacaoInicial + Transformacao, sem orientação narrativa), enquanto a
     * escolha de operação valida contra o julgamento categórico curado
     * (operacao_relacao) -- por isso o valor estrutural correto é calculado
     * aqui a partir dos papéis já posicionados, não lido do campo
     * estado_final curado (que reflete a resposta orientada, fora do
     * alcance desta classe).
     */
    @SuppressWarnings("unchecked")
    private static void testarSegundoGateDeOperacao() {
        SituacaoProblemaAditiva situacao = localizar(
                new RepositorioSituacoesAditivas().listarValidadas(),
                "PO_TRANSFORMACAO_RELACAO_bonecas_620955739");
        ServicoAtividadeWebTransformacaoRelacao servico =
                new ServicoAtividadeWebTransformacaoRelacao(
                        "tentativa.web.teste.transformacao_relacao.bonecas", situacao);
        Map<String, Object> estado = servico.estadoAtual();
        String alvo = String.valueOf(estado.get("papel_desconhecido_original"));
        exigir("papel.relacaoFinal".equals(alvo),
                "incógnita curada de bonecas deveria ser relacaoFinal");
        exigir(!contemAcao(estado, "ESCOLHER_OPERACAO_RELACAO"),
                "seletor não deveria aparecer antes de nada posicionado");

        int somaEstrutural = 0;
        for (Object item : (List<Object>) estado.get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) item;
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            servico.posicionarValorConhecido(id, id);
            Map<String, Object> atual = servico.estadoAtual();
            if (id.equals(atual.get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
                atual = servico.estadoAtual();
            }
            for (Object outro : (List<Object>) atual.get("papeis")) {
                Map<String, Object> candidato = (Map<String, Object>) outro;
                if (id.equals(candidato.get("id"))) {
                    somaEstrutural += ((Number) candidato.get("valor")).intValue();
                }
            }
        }

        Map<String, Object> aposPosicionar = servico.estadoAtual();
        exigir(contemAcao(aposPosicionar, "ESCOLHER_OPERACAO_RELACAO"),
                "seletor deveria aparecer depois dos dois papéis conhecidos posicionados "
                        + "(situação tem operacao_relacao curado)");

        Map<String, Object> aceiteIncognita = servico.proporValor(alvo, somaEstrutural);
        exigir(Boolean.TRUE.equals(aceiteIncognita.get("aceita")),
                "valor estrutural (RelacaoInicial + Transformacao) deveria ser aceito para a incógnita");
        Map<String, Object> estadoAposIncognita =
                (Map<String, Object>) aceiteIncognita.get("estado");
        exigir(Boolean.FALSE.equals(estadoAposIncognita.get("concluida")),
                "não deveria concluir só com a incógnita certa -- falta o segundo gate (operação)");

        Map<String, Object> operacaoErrada = servico.escolherOperacao(null, "SOMA");
        exigir(Boolean.FALSE.equals(operacaoErrada.get("aceita")),
                "SOMA deveria ser rejeitada (curado é subtracao)");
        exigir(Boolean.FALSE.equals(((Map<String, Object>) operacaoErrada.get("estado"))
                .get("concluida")), "não deveria concluir com a operação errada");

        Map<String, Object> operacaoCerta = servico.escolherOperacao(null, "SUBTRACAO");
        exigir(Boolean.TRUE.equals(operacaoCerta.get("aceita")),
                "SUBTRACAO deveria ser aceita (curado é subtracao)");
        exigir(Boolean.TRUE.equals(((Map<String, Object>) operacaoCerta.get("estado"))
                .get("concluida")), "deveria concluir com os dois gates satisfeitos");

        System.out.println("APROVADO: segundo gate de operação (Transformação de Relação) "
                + "independente da incógnita, mesma regra do desktop.");
    }

    private static boolean contemAcao(Map<String, Object> estado, String id) {
        for (Object item : (List<Object>) estado.get("acoes_disponiveis")) {
            if (id.equals(((Map<String, Object>) item).get("id"))) {
                return true;
            }
        }
        return false;
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
