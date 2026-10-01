import gerard.aplicacao.portabilidade.ServicoAtividadeWebTransformacaoMedidas;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

/**
 * A escalada da incógnita no limite (3ª tentativa rejeitada consecutiva) é um fato do
 * domínio exposto pelo servidor em {@code escalada_no_limite}: falso no início, verdadeiro
 * quando o limite é atingido, persistente enquanto o papel continuar bloqueado e falso
 * de novo depois de reiniciar. A historinha passiva do cliente depende só desse fato —
 * nunca de memória própria (decisão de 2026-10-01).
 */
public final class TesteEscaladaNoLimiteWeb {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        SituacaoProblemaAditiva situacao = null;
        for (SituacaoProblemaAditiva candidata
                : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (candidata.getTipo() == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
                situacao = candidata;
                break;
            }
        }
        exigir(situacao != null, "situação curada de transformação ausente");
        ServicoAtividadeWebTransformacaoMedidas servico =
                new ServicoAtividadeWebTransformacaoMedidas(
                        "tentativa.web.teste.escalada", situacao);

        Map<String, Object> estado = servico.estadoAtual();
        exigir(Boolean.FALSE.equals(estado.get("escalada_no_limite")),
                "começa fora do limite da escalada");
        String alvo = String.valueOf(estado.get("papel_desconhecido_original"));
        preparar(servico, situacao, alvo);
        exigir(Boolean.FALSE.equals(servico.estadoAtual().get("escalada_no_limite")),
                "posicionar os papéis conhecidos não aciona a escalada");

        boolean limite = false;
        for (int tentativa = 1; tentativa <= 8 && !limite; tentativa++) {
            Map<String, Object> resultado = servico.proporValor(alvo, 7770 + tentativa);
            exigir(Boolean.FALSE.equals(resultado.get("aceita")), "valor errado é rejeitado");
            limite = Boolean.TRUE.equals(resultado.get("limite_atingido"));
            Map<String, Object> aposProposta = (Map<String, Object>) resultado.get("estado");
            exigir(Boolean.valueOf(limite).equals(aposProposta.get("escalada_no_limite")),
                    "o estado devolvido junto com a proposta já reflete o limite (tentativa "
                            + tentativa + ")");
            if (!limite) {
                Map<String, Object> resposta =
                        servico.responderConfirmacaoValor(alvo, false, 7770 + tentativa);
                limite = Boolean.TRUE.equals(resposta.get("limite_atingido"));
                Map<String, Object> aposResposta = (Map<String, Object>) resposta.get("estado");
                exigir(Boolean.valueOf(limite).equals(aposResposta.get("escalada_no_limite")),
                        "o estado devolvido junto com a resposta já reflete o limite");
            }
        }
        exigir(limite, "o limite de tentativas deve ser atingido por rejeições consecutivas");
        exigir(Boolean.TRUE.equals(servico.estadoAtual().get("escalada_no_limite")),
                "a escalada permanece verdadeira enquanto o papel está bloqueado");

        Map<String, Object> reiniciado = (Map<String, Object>) servico.reiniciar().get("estado");
        exigir(reiniciado == null || Boolean.FALSE.equals(reiniciado.get("escalada_no_limite")),
                "reiniciar devolve a escalada ao estado inicial");
        exigir(Boolean.FALSE.equals(servico.estadoAtual().get("escalada_no_limite")),
                "depois de reiniciar a historinha volta a esperar a terceira tentativa");
        System.out.println("APROVADO: escalada_no_limite sobe no limite, persiste e zera ao reiniciar.");
    }

    @SuppressWarnings("unchecked")
    private static void preparar(ServicoAtividadeWebTransformacaoMedidas servico,
            SituacaoProblemaAditiva situacao, String alvo) {
        for (Object item : (List<Object>) servico.estadoAtual().get("papeis")) {
            String id = String.valueOf(((Map<String, Object>) item).get("id"));
            if (id.equals(alvo)) continue;
            servico.posicionarValorConhecido(id, id);
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
            }
        }
        servico.engatarIncognita(alvo, alvo);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
