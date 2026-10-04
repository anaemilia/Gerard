package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.Map;

/**
 * Nos seletores de operação (Soma/Subtração), cada escolha errada devolve a explicação curada;
 * a escolha errada que atinge o limite de 3 rejeições consecutivas não traz aviso algum: a
 * historinha assume sozinha.
 */
public final class TesteSemExplicacaoNoLimiteDeOperacaoWeb {
    public static void main(String[] args) {
        SituacaoProblemaAditiva situacao = null;
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if ("PO_COMPOSICAO_RELACOES_idades_642701604".equals(s.getId())) {
                situacao = s;
            }
        }
        exigir(situacao != null, "situação curada não encontrada");
        ServicoAtividadeWebComposicaoRelacoes servico = new ServicoAtividadeWebComposicaoRelacoes(
                "tentativa.teste.sem_explicacao_no_limite", situacao);
        String alvo = new ResolvedorIncognitaCurada().resolver(situacao).getChaveEfetiva();
        @SuppressWarnings("unchecked")
        Map<String, Object> estado = servico.estadoAtual();
        for (String chave : new String[] {"relacao_1", "relacao_2", "relacao_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estado.get(chave);
            String id = String.valueOf(papel.get("id"));
            if (!id.equals(alvo)) {
                servico.posicionarValorConhecido(id, id);
            }
        }
        servico.engatarIncognita(alvo, alvo);
        int valor = SemanticaCuradaSituacao.buscar(situacao, null, alvo).getValorInteiro();
        servico.proporValor(alvo, valor);

        Map<String, Object> primeira = servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir(Boolean.FALSE.equals(primeira.get("aceita")) && primeira.get("chave_mensagem") != null,
                "a 1ª escolha errada traz a explicação");
        Map<String, Object> segunda = servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir(segunda.get("chave_mensagem") != null, "a 2ª escolha errada traz a explicação");
        Map<String, Object> terceira = servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir(Boolean.FALSE.equals(terceira.get("aceita")), "a 3ª escolha continua errada");
        exigir(terceira.get("chave_mensagem") == null, "a 3ª escolha errada, que atinge o limite, vem sem aviso");
        System.out.println("APROVADO: a escolha de operação que atinge o limite não traz explicação.");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
