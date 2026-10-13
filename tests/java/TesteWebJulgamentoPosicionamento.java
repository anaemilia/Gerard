package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import java.util.Map;

/**
 * O julgamento do posicionamento de um número solto vale para qualquer caixa (conhecida, já preenchida ou incógnita):
 * um só lugar decide (ServicoSorteioAtividadeWeb.julgarSoltura), o posicionamento incorreto fica publicado na cena até
 * ser corrigido e a restauração o limpa.
 */
public final class TesteWebJulgamentoPosicionamento {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        PortaRegistroAtividadeWeb porta = new PortaRegistroAtividadeWeb() {
            public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
            public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                    String e, String f, String g, String h, String i) { }
            public void persistir(RegistroFactualAcaoInstrumental r) { }
            public void registrarRestauracaoModelagem(gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem r) { }
        };
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new gerard.aplicacao.PoliticaSorteioSituacoesAditivas(),
                new gerard.aplicacao.FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new gerard.campoaditivo.servico.CatalogoDefinicoesAditivas(),
                        new gerard.campoaditivo.curadoria.ConstrutorResultadoCurado()),
                new java.util.Random(5), gerard.idioma.IdiomaInterface.PORTUGUES, porta,
                new gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb(),
                new gerard.infraestrutura.web.scaffolding.AdaptadorQuestionamentoPosicionamentoWeb());
        boolean classificou = false;
        for (int i = 0; i < 200 && !classificou; i++) {
            servico.sortearMedidas();
            classificou = Boolean.TRUE.equals(servico.escolherCategoria("COMPOSICAO_MEDIDAS").get("correta"));
        }
        exigir(classificou, "não sorteou Composição de Medidas");

        Map<String, Object> r = servico.posicionarValorConhecido("papel.parte1", "papel.parte2");
        exigir(Boolean.FALSE.equals(r.get("aceita")) && r.get("chave_mensagem") != null,
                "número de uma parte solto na outra parte é rejeitado com a pergunta");
        exigir(questionado(r, "papel.parte1"), "o posicionamento incorreto fica publicado na caixa onde foi solto");
        exigir(vazia(r, "papel.parte1"), "o posicionamento incorreto não grava valor no papel");

        r = servico.soltarNumero("papel.todo", "papel.parte2");
        exigir(Boolean.FALSE.equals(r.get("aceita")), "o mesmo número solto no todo (incógnita) é julgado do mesmo jeito");
        exigir(questionado(r, "papel.todo"), "o posicionamento incorreto passa a ser o do todo");

        r = servico.engatarIncognita("papel.todo", "papel.parte1");
        exigir(Boolean.FALSE.equals(r.get("aceita")), "engatar com número de outra parte é rejeitado");

        r = servico.posicionarValorConhecido("papel.parte1", "papel.parte1");
        exigir(Boolean.TRUE.equals(r.get("aceita")), "número solto na própria caixa é aceito");
        exigir(cena(r).get("posicionamento_questionado") == null, "o posicionamento correto do mesmo número resolve a pergunta");

        servico.soltarNumero("papel.parte1", "papel.parte2");
        exigir(cena(servico.reiniciarAtividadeAtual()).get("posicionamento_questionado") == null,
                "restaurar limpa o posicionamento incorreto");
        System.out.println("APROVADO: o julgamento do posicionamento vale para qualquer caixa e fica até a correção.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cena(Map<String, Object> resultadoOuEstado) {
        Map<String, Object> estado = resultadoOuEstado.containsKey("estado")
                ? (Map<String, Object>) resultadoOuEstado.get("estado") : resultadoOuEstado;
        return (Map<String, Object>) estado.get("cena");
    }

    @SuppressWarnings("unchecked")
    private static boolean questionado(Map<String, Object> resultado, String papel) {
        Map<String, Object> q = (Map<String, Object>) cena(resultado).get("posicionamento_questionado");
        return q != null && papel.equals(q.get("figura_chave_papel"));
    }

    @SuppressWarnings("unchecked")
    private static boolean vazia(Map<String, Object> resultado, String papel) {
        for (Object f : (java.util.List<Object>) cena(resultado).get("figuras")) {
            Map<String, Object> figura = (Map<String, Object>) f;
            if (papel.equals(figura.get("chave_papel_semantico"))) {
                return !Boolean.TRUE.equals(figura.get("conhecido")) || figura.get("valor") == null
                        || questionado(resultado, papel);
            }
        }
        return false;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
