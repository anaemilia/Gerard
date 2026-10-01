import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.portabilidade.PortaRegistroAtividadeWeb;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;
import gerard.idioma.IdiomaInterface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Decisão da usuária (2026-09-29): a modelagem web precisa persistir (não
 * ficou azul, então o histórico não pode sumir no recarregamento). Pela
 * camada exposta ao HTTP, cada ação da modelagem produz o registro do mesmo
 * proprietário semântico do desktop e chega à porta de persistência pela
 * TentativaModelagemAditiva; depois do azul nada mais é registrado, nem a
 * restauração, até outra situação ser sorteada.
 */
public final class TesteModelagemWebPersisteNaTentativa {

    public static void main(String[] args) {
        PortaGravadora porta = new PortaGravadora();
        ServicoSorteioAtividadeWeb servico = novoServico(porta, 5);
        Map<String, Object> estado = classificarComposicao(servico);
        exigir(porta.ultimo() != null && gerard.dominio.campoaditivo.TentativaClassificacaoCategoriaAditiva
                        .CHAVE_PROPRIETARIO.equals(proprietario(porta.ultimo())),
                "a classificação correta deve ser persistida: " + descrever(porta));

        // Restaurar antes do azul é ação constituída e registrada.
        servico.reiniciarAtividadeAtual();
        exigir(porta.restauracoes == 1, "restauração antes do azul deve ser registrada");

        Map<String, Integer> valores = new LinkedHashMap<String, Integer>();
        String alvo = String.valueOf(modelagem(estado).get("papel_desconhecido_original"));
        for (Object item : papeis(estado)) {
            String id = String.valueOf(((Map<?, ?>) item).get("id"));
            if (id.equals(alvo)) continue;
            int antes = porta.registros.size();
            Map<String, Object> r = servico.posicionarValorConhecido(id, id);
            exigir(porta.registros.size() == antes + 1
                            && id.equals(porta.ultimo().getAlvoSemantico()),
                    "posicionar " + id + " deve gerar um registro de posicionamento");
            for (Object p : papeis(estadoDe(r))) {
                Map<?, ?> papel = (Map<?, ?>) p;
                if (id.equals(papel.get("id"))) valores.put(id, ((Number) papel.get("valor")).intValue());
            }
        }
        int antes = porta.registros.size();
        servico.engatarIncognita(alvo, alvo);
        exigir(porta.registros.size() == antes + 1, "engatar o ? é posicionamento registrado");

        int correto = alvo.contains("todo") ? soma(valores)
                : valorDeChaveCom(valores, "todo") - valorOutro(valores, alvo);
        Map<String, Object> errado = servico.proporValor(alvo, correto + 1);
        exigir(Boolean.FALSE.equals(errado.get("aceita")), "valor errado rejeitado");
        RegistroFactualAcaoInstrumental rErrado = porta.ultimo();
        exigir("papel.incognita".equals(rErrado.getProprietarioSemantico())
                        && rErrado.getResultado().name().equals("ERRADA")
                        && errado.get("action_id").equals(rErrado.getActionId()),
                "valor errado: registro da IncognitaQuantitativa com o mesmo action_id");
        servico.responderConfirmacaoValor(alvo, true, correto + 1);
        RegistroFactualAcaoInstrumental rSim = porta.ultimo();
        exigir(rSim != rErrado && "CONFIRMOU_VALOR_REJEITADO".equals(rSim.getTipoDiagnosticoFactual())
                        && rErrado.getRejectionSequenceId().equals(rSim.getRejectionSequenceId()),
                "resposta Sim: tentativa própria na mesma sequência");
        Map<String, Object> certo = servico.proporValor(alvo, correto);
        exigir(Boolean.TRUE.equals(certo.get("aceita"))
                        && porta.ultimo().getResultado().name().equals("CORRETA"),
                "valor correto aceito e registrado como CORRETA");
        exigir(Boolean.TRUE.equals(modelagem(estadoDe(certo)).get("concluida")),
                "modelagem concluída (azul)");

        int totalNoAzul = porta.registros.size();
        Map<String, Object> reiniciado = servico.reiniciarAtividadeAtual();
        exigir(porta.restauracoes == 1, "restauração após o azul não é registrada");
        for (Object item : papeis(reiniciado)) {
            String id = String.valueOf(((Map<?, ?>) item).get("id"));
            if (!id.equals(alvo)) servico.posicionarValorConhecido(id, id);
        }
        servico.engatarIncognita(alvo, alvo);
        Map<String, Object> exploratorio = servico.proporValor(alvo, correto + 2);
        exigir(Boolean.FALSE.equals(exploratorio.get("aceita"))
                        && exploratorio.get("chave_mensagem") == null,
                "após o azul: sem pergunta de confirmação");
        servico.proporValor(alvo, correto);
        exigir(porta.registros.size() == totalNoAzul,
                "após o azul nada é registrado (" + totalNoAzul + "->" + porta.registros.size() + ")");

        // Nova situação sorteada encerra o modo exploratório.
        classificarComposicao(servico);
        exigir(porta.registros.size() > totalNoAzul,
                "nova situação sorteada volta a registrar");

        System.out.println("Teste aprovado: modelagem web persiste pela tentativa; após o azul é exploratória.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> classificarComposicao(ServicoSorteioAtividadeWeb servico) {
        for (int i = 0; i < 200; i++) {
            servico.sortearMedidas();
            Map<String, Object> r = servico.escolherCategoria("COMPOSICAO_MEDIDAS");
            if (Boolean.TRUE.equals(r.get("correta"))) {
                return (Map<String, Object>) r.get("estado");
            }
        }
        throw new AssertionError("não sorteou Composição de Medidas");
    }

    private static ServicoSorteioAtividadeWeb novoServico(PortaRegistroAtividadeWeb porta, int semente) {
        return new ServicoSorteioAtividadeWeb(new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(semente), IdiomaInterface.PORTUGUES, porta,
                new gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb(),
                new gerard.infraestrutura.web.scaffolding.AdaptadorQuestionamentoPosicionamentoWeb());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> estadoDe(Map<String, Object> r) {
        return (Map<String, Object>) r.get("estado");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> modelagem(Map<String, Object> estado) {
        return (Map<String, Object>) estado.get("modelagem");
    }

    @SuppressWarnings("unchecked")
    private static List<Object> papeis(Map<String, Object> estado) {
        Map<String, Object> m = modelagem(estado);
        if (m.get("papeis") != null) return (List<Object>) m.get("papeis");
        List<Object> lista = new ArrayList<Object>();
        for (String chave : new String[] {"parte1", "parte2", "todo"}) lista.add(m.get(chave));
        return lista;
    }

    private static int soma(Map<String, Integer> v) {
        int s = 0; for (int x : v.values()) s += x; return s;
    }

    private static int valorDeChaveCom(Map<String, Integer> v, String trecho) {
        for (Map.Entry<String, Integer> e : v.entrySet()) if (e.getKey().contains(trecho)) return e.getValue();
        throw new AssertionError("sem papel " + trecho + " em " + v);
    }

    private static int valorOutro(Map<String, Integer> v, String alvo) {
        for (Map.Entry<String, Integer> e : v.entrySet())
            if (!e.getKey().contains("todo") && !e.getKey().equals(alvo)) return e.getValue();
        throw new AssertionError("sem outra parte em " + v);
    }

    private static String proprietario(RegistroFactualAcaoInstrumental r) {
        return r == null ? null : r.getProprietarioSemantico();
    }

    private static String descrever(PortaGravadora p) {
        StringBuilder b = new StringBuilder();
        for (RegistroFactualAcaoInstrumental r : p.registros)
            b.append(r.getProprietarioSemantico()).append('/').append(r.getResultado()).append(' ');
        return b.toString();
    }

    private static void exigir(boolean c, String m) { if (!c) throw new AssertionError(m); }

    private static final class PortaGravadora implements PortaRegistroAtividadeWeb {
        final List<RegistroFactualAcaoInstrumental> registros = new ArrayList<RegistroFactualAcaoInstrumental>();
        int restauracoes;
        RegistroFactualAcaoInstrumental ultimo() { return registros.isEmpty() ? null : registros.get(registros.size() - 1); }
        public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
        public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                String e, String f, String g, String h, String i) { }
        public void persistir(RegistroFactualAcaoInstrumental registro) { registros.add(registro); }
        public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem r) { restauracoes++; }
    }
}
