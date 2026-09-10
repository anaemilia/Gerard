import gerard.aplicacao.portabilidade.ServicoAtividadeWebComposicao;
import java.util.List;
import java.util.Map;

public class TesteServicoAtividadeWebComposicao {
    public static void main(String[] args) {
        ServicoAtividadeWebComposicao servico =
                new ServicoAtividadeWebComposicao("tentativa.teste.web");
        Map<String, Object> inicial = servico.estadoAtual();
        checar(!concluida(inicial), "começa incompleta");
        checar("PO_COMPOSICAO_MEDIDAS_bolas_1098018440".equals(
                inicial.get("situacao_id")), "usa a situação curada selecionada");
        checar(Boolean.TRUE.equals(inicial.get("situacao_validada")),
                "a situação está validada na curadoria");
        checar(!possuiAcao(inicial, "PROPOR_VALOR_PAPEL"),
                "não oferece digitação antes do posicionamento e engate");
        checar(possuiAcao(inicial, "POSICIONAR_CONHECIDO"), "oferece posicionamento");
        checar(!possuiAcao(inicial, "ENGATAR_INCOGNITA"), "engate aguarda as partes");
        // Validação semântica de origem/destino no soltar (servidor, não só
        // cliente): arrastar o token de parte2 até a caixa de parte1 tem que
        // ser rejeitado, sem preencher a caixa (mesma avaliação de
        // ScaffoldingQuestionamento.avaliarPosicionamento no desktop).
        Map<String, Object> rejeitadoPorOrigem = servico.posicionarValorConhecido("papel.parte1", "papel.parte2");
        checar(Boolean.FALSE.equals(rejeitadoPorOrigem.get("aceita")),
                "origem incompatível com o destino é rejeitada");
        checar(rejeitadoPorOrigem.get("chave_mensagem") != null,
                "rejeição por origem incompatível traz mensagem explicativa");
        @SuppressWarnings("unchecked")
        Map<String, Object> parte1AindaVazia =
                (Map<String, Object>) estado(rejeitadoPorOrigem).get("parte1");
        checar(!Boolean.TRUE.equals(parte1AindaVazia.get("conhecido")),
                "soltura rejeitada não posiciona o papel-alvo");

        servico.posicionarValorConhecido("papel.parte1", "papel.parte1");
        servico.posicionarValorConhecido("papel.parte2", "papel.parte2");
        checar(possuiAcao(servico.estadoAtual(), "ENGATAR_INCOGNITA"), "partes posicionadas liberam engate");
        inicial = estado(servico.engatarIncognita("papel.todo", "papel.todo"));
        checar(!concluida(inicial), "engatar não confirma o valor");
        checar(possuiAcao(inicial, "PROPOR_VALOR_PAPEL"),
                "estado incompleto publica a ação de propor valor");

        Map<String, Object> rejeitada = servico.proporTodo(13);
        checar(Boolean.FALSE.equals(rejeitada.get("aceita")), "a relação rejeita 13");
        checar(!concluida(estado(rejeitada)), "proposta rejeitada não altera o estado");
        checar(rejeitada.get("action_id") != null, "ação rejeitada possui action_id");

        Map<String, Object> aceita = servico.proporTodo(14);
        checar(Boolean.TRUE.equals(aceita.get("aceita")), "a relação aceita 14");
        checar(concluida(estado(aceita)), "ação aceita atualiza o estado");
        checar(!possuiAcao(estado(aceita), "PROPOR_VALOR_PAPEL"),
                "estado concluído deixa de publicar a ação de propor valor");
        checar(aceita.get("action_id") != null, "ação aceita possui action_id");
        System.out.println("APROVADO: ciclo funcional web usa o domínio Java.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> estado(Map<String, Object> resultado) {
        return (Map<String, Object>) resultado.get("estado");
    }

    private static boolean concluida(Map<String, Object> estado) {
        return Boolean.TRUE.equals(estado.get("concluida"));
    }

    @SuppressWarnings("unchecked")
    private static boolean possuiAcao(Map<String, Object> estado, String id) {
        for (Object item : (List<Object>) estado.get("acoes_disponiveis")) {
            if (id.equals(((Map<String, Object>) item).get("id"))) return true;
        }
        return false;
    }

    private static void checar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
