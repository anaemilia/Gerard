import gerard.aplicacao.portabilidade.ServicoAtividadeWebTransformacaoMedidas;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

public final class TesteServicoAtividadeWebTransformacaoMedidas {
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
                        "tentativa.web.teste.transformacao", situacao);
        Map<String, Object> estado = servico.estadoAtual();
        String alvo = String.valueOf(estado.get("papel_desconhecido_original"));
        List<Object> acoes = (List<Object>) estado.get("acoes_disponiveis");
        exigir(acoes.stream().map(item -> (Map<String, Object>) item)
                .anyMatch(acao -> "PROPOR_VALOR_PAPEL".equals(acao.get("id"))
                        && alvo.equals(((Map<String, Object>) acao.get("corpo"))
                                .get("papel_id"))),
                "ação não anuncia a incógnita curada");

        Integer esperado = SemanticaCuradaSituacao.buscar(
                situacao, null, alvo).getValorInteiro();
        exigir(esperado != null, "valor normativo da incógnita ausente");
        int errado = esperado.intValue() == Integer.MAX_VALUE
                ? esperado.intValue() - 1 : esperado.intValue() + 1;
        if ("papel.transformacao".equals(alvo) && errado == 0) errado = 1;
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
        System.out.println("APROVADO: Transformação de Medidas funcional via serviço web.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
