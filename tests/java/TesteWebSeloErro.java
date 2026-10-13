package gerard.aplicacao.portabilidade;

import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.TentativaModelagemAditiva;
import gerard.dominio.campoaditivo.TipoRestauracaoModelagem;
import java.util.Map;

import java.util.Map;

/**
 * Selo de erro pela camada que as rotas web chamam: a proposta errada da incógnita publica cena.estado_feedback=ERRO
 * (mesma decisão de FluxoConfirmacaoValorIncognita, do desktop); o valor correto, reiniciar e a conclusão devolvem NEUTRO.
 */
public final class TesteWebSeloErro {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        PortaRegistroAtividade porta = new PortaRegistroAtividade() {
            public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
            public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                    String e, String f, String g, String h, String i) { }
            public void persistir(RegistroFactualAcaoInstrumental r) { }
            public void registrarRestauracaoModelagem(gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem r) { }
        };
        ServicoSorteioAtividade servico = new ServicoSorteioAtividade(
                new gerard.aplicacao.PoliticaSorteioSituacoesAditivas(),
                new gerard.aplicacao.FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new gerard.campoaditivo.servico.CatalogoDefinicoesAditivas(),
                        new gerard.campoaditivo.curadoria.ConstrutorResultadoCurado()),
                new java.util.Random(5), gerard.idioma.IdiomaInterface.PORTUGUES, porta,
                new gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb(),
                new gerard.infraestrutura.web.scaffolding.AdaptadorQuestionamentoPosicionamentoWeb());
        Map<String, Object> estado = null;
        for (int i = 0; i < 200 && estado == null; i++) {
            servico.sortearMedidas();
            Map<String, Object> r = servico.escolherCategoria("COMPOSICAO_MEDIDAS");
            if (Boolean.TRUE.equals(r.get("correta"))) estado = (Map<String, Object>) r.get("estado");
        }
        exigir(estado != null, "não sorteou Composição de Medidas");
        exigir("NEUTRO".equals(feedback(estado)), "início neutro: " + feedback(estado) + " chaves=" + estado.keySet() + " cena=" + (estado.get("cena") instanceof Map ? ((Map<String, Object>) estado.get("cena")).keySet() : estado.get("cena")));
        Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
        String alvo = String.valueOf(modelagem.get("papel_desconhecido_original"));
        Map<String, Integer> valores = new java.util.LinkedHashMap<String, Integer>();
        for (String chave : new String[] {"parte1", "parte2", "todo"}) {
            Map<String, Object> papel = (Map<String, Object>) modelagem.get(chave);
            if (papel == null) continue;
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            Map<String, Object> r = servico.posicionarValorConhecido(id, id);
            Map<String, Object> m2 = (Map<String, Object>) ((Map<String, Object>) r.get("estado")).get("modelagem");
            valores.put(id, ((Number) ((Map<String, Object>) m2.get(chave)).get("valor")).intValue());
        }
        servico.engatarIncognita(alvo, alvo);
        int correto = 0;
        if (alvo.contains("todo")) { for (int x : valores.values()) correto += x; }
        else { int todo = 0, outra = 0; for (Map.Entry<String, Integer> en : valores.entrySet()) {
            if (en.getKey().contains("todo")) todo = en.getValue(); else outra = en.getValue(); } correto = todo - outra; }

        Map<String, Object> errada = servico.proporValor(alvo, correto + 1);
        exigir(Boolean.FALSE.equals(errada.get("aceita")), "a proposta errada é rejeitada");
        exigir("ERRO".equals(feedback((Map<String, Object>) errada.get("estado"))),
                "valor errado publica ERRO: " + feedback((Map<String, Object>) errada.get("estado")));
        Map<String, Object> reiniciada = servico.reiniciarAtividadeAtual();
        exigir("NEUTRO".equals(feedback(reiniciada)), "reiniciar devolve NEUTRO");
        for (String chave : new String[] {"parte1", "parte2", "todo"}) {
            Map<String, Object> papel = (Map<String, Object>) modelagem.get(chave);
            if (papel != null && !String.valueOf(papel.get("id")).equals(alvo)) {
                servico.posicionarValorConhecido(String.valueOf(papel.get("id")), String.valueOf(papel.get("id")));
            }
        }
        servico.engatarIncognita(alvo, alvo);
        Map<String, Object> novoErro = servico.proporValor(alvo, correto + 2);
        exigir("ERRO".equals(feedback((Map<String, Object>) novoErro.get("estado"))), "novo erro volta a publicar ERRO");
        Map<String, Object> certa = servico.proporValor(alvo, correto);
        exigir(Boolean.TRUE.equals(certa.get("aceita")), "a proposta correta é aceita");
        exigir("NEUTRO".equals(feedback((Map<String, Object>) certa.get("estado"))),
                "valor correto/conclusão publica NEUTRO: " + feedback((Map<String, Object>) certa.get("estado")));
        System.out.println("APROVADO: erro da incógnita publica ERRO; correto, reiniciar e conclusão publicam NEUTRO.");
    }

    @SuppressWarnings("unchecked")
    private static String feedback(Map<String, Object> estado) {
        return String.valueOf(((Map<String, Object>) estado.get("cena")).get("estado_feedback"));
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
