import gerard.aplicacao.portabilidade.ServicoAtividadeWebComparacaoMedidas;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.List;
import java.util.Map;

public final class TesteServicoAtividadeWebComparacaoMedidas {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        SituacaoProblemaAditiva situacao = null;
        for (SituacaoProblemaAditiva candidata
                : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (candidata.getTipo() == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                situacao = candidata;
                break;
            }
        }
        exigir(situacao != null, "situação curada de comparação ausente");
        ServicoAtividadeWebComparacaoMedidas servico =
                new ServicoAtividadeWebComparacaoMedidas(
                        "tentativa.web.teste.comparacao", situacao);
        Map<String, Object> estado = servico.estadoAtual();
        String alvo = String.valueOf(estado.get("papel_desconhecido_original"));
        exigir(Boolean.FALSE.equals(estado.get("concluida")), "começa incompleta");
        exigir(((List<Object>) estado.get("acoes_disponiveis")).stream()
                .map(item -> (Map<String, Object>) item)
                .noneMatch(acao -> "PROPOR_VALOR_PAPEL".equals(acao.get("id"))),
                "não oferece digitação antes do engate e posicionamento");
        for (Object item : (List<Object>) estado.get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) item;
            exigir(Boolean.FALSE.equals(papel.get("conhecido")) && papel.get("valor") == null,
                    "papéis começam sem valores posicionados");
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            servico.posicionarValorConhecido(id, id);
            if (id.equals(servico.estadoAtual().get("papel_aguardando_sinal"))) {
                int valorCurado = SemanticaCuradaSituacao.buscar(situacao, null, id).getValorInteiro();
                servico.escolherSinalNumeroRelativo(id, valorCurado < 0 ? "-" : "+");
            }
        }
        estado = (Map<String, Object>) servico.engatarIncognita(alvo, alvo).get("estado");
        exigir(Boolean.FALSE.equals(estado.get("concluida")), "engatar não confirma o valor");
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
        System.out.println("APROVADO: Comparação de Medidas funcional via serviço web.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
