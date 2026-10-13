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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Regra de 2026-10-10 pela camada que as rotas web chamam (ServicoAtividade*, a mesma que /api/acoes/propor-valor
 * e /api/acoes/escolher-operacao usam): ao atingir o limite de 3 rejeições consecutivas (valor da incógnita ou escolha
 * de operação) o registro para; a ação que atinge o limite ainda é persistida; reiniciar reabre.
 */
public final class TesteWebRegistroParaNoLimite {
    static final class Gravador implements PortaPersistenciaAcaoInstrumental {
        final List<RegistroFactualAcaoInstrumental> registros = new ArrayList<RegistroFactualAcaoInstrumental>();
        public void persistir(RegistroFactualAcaoInstrumental r) { registros.add(r); }
    }

    public static void main(String[] args) {
        operacao();
        incognita();
        System.out.println("APROVADO: a web para de registrar no limite de 3 rejeições (incógnita e operação), registra a ação do limite e reabre ao reiniciar.");
    }

    private static ServicoAtividadeComposicaoRelacoes preparar(Gravador g, EscopoTentativa[] escopoSaida,
            String[] alvoSaida, int[] valorSaida) {
        SituacaoProblemaAditiva situacao = null;
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if ("PO_COMPOSICAO_RELACOES_idades_642701604".equals(s.getId())) situacao = s;
        }
        exigir(situacao != null, "situação curada não encontrada");
        EscopoTentativa escopo = new EscopoTentativa(new TentativaModelagemAditiva("t.web.limite"), g);
        ServicoAtividadeComposicaoRelacoes servico =
                new ServicoAtividadeComposicaoRelacoes("t.web.limite", situacao, escopo);
        String alvo = new ResolvedorIncognitaCurada().resolver(situacao).getChaveEfetiva();
        @SuppressWarnings("unchecked")
        Map<String, Object> estado = servico.estadoAtual();
        for (String chave : new String[] {"relacao_1", "relacao_2", "relacao_final"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> papel = (Map<String, Object>) estado.get(chave);
            String id = String.valueOf(papel.get("id"));
            if (!id.equals(alvo)) servico.posicionarValorConhecido(id, id);
        }
        servico.engatarIncognita(alvo, alvo);
        escopoSaida[0] = escopo; alvoSaida[0] = alvo;
        valorSaida[0] = SemanticaCuradaSituacao.buscar(situacao, null, alvo).getValorInteiro();
        return servico;
    }

    private static void operacao() {
        Gravador g = new Gravador(); EscopoTentativa[] e = new EscopoTentativa[1]; String[] a = new String[1]; int[] v = new int[1];
        ServicoAtividadeComposicaoRelacoes servico = preparar(g, e, a, v);
        servico.proporValor(a[0], v[0]);
        int base = g.registros.size();
        StringBuilder passos = new StringBuilder();
        for (int k = 1; k <= 6; k++) {
            servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
            passos.append(g.registros.size() - base).append(' ');
        }
        exigir((g.registros.size() - base) == 3,
                "operação: 3 erros registrados (inclusive o que atinge o limite) e nada depois; passos=" + passos);
        servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SOMA");
        exigir((g.registros.size() - base) == 3, "operação: nem a escolha correta é registrada durante a ajuda visual");
        e[0].restaurar(TipoRestauracaoModelagem.DIAGRAMA_COMPLETO, OrigemAcao.ORIGEM_USUARIO);
        servico.escolherOperacao("ENTRE_TRANSFORMACOES", "SUBTRACAO");
        exigir((g.registros.size() - base) == 4, "operação: restaurar reabre o registro");
        System.out.println("operação (linhas após cada erro): " + passos);
    }

    @SuppressWarnings("unchecked")
    private static void incognita() {
        final List<RegistroFactualAcaoInstrumental> registros = new ArrayList<RegistroFactualAcaoInstrumental>();
        PortaRegistroAtividade porta = new PortaRegistroAtividade() {
            public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
            public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                    String e, String f, String g, String h, String i) { }
            public void persistir(RegistroFactualAcaoInstrumental r) { registros.add(r); }
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
            Map<String, Object> p2 = (Map<String, Object>) m2.get(chave);
            valores.put(id, ((Number) p2.get("valor")).intValue());
        }
        servico.engatarIncognita(alvo, alvo);
        int correto = 0;
        if (alvo.contains("todo")) { for (int x : valores.values()) correto += x; }
        else { int todo = 0, outra = 0; for (Map.Entry<String, Integer> en : valores.entrySet()) {
            if (en.getKey().contains("todo")) todo = en.getValue(); else outra = en.getValue(); } correto = todo - outra; }
        int base = registros.size();
        StringBuilder passos = new StringBuilder();
        for (int k = 1; k <= 6; k++) {
            servico.proporValor(alvo, correto + k);
            passos.append(registros.size() - base).append(' ');
        }
        exigir((registros.size() - base) == 3,
                "incógnita: 3 valores errados registrados (inclusive o que atinge o limite) e nada depois; passos=" + passos);
        RegistroFactualAcaoInstrumental ultimo = registros.get(registros.size() - 1);
        exigir(ultimo instanceof gerard.dominio.atividade.RegistroAcaoInstrumental
                        && ((gerard.dominio.atividade.RegistroAcaoInstrumental) ultimo).getResultadoTentativa().isPresent()
                        && ((gerard.dominio.atividade.RegistroAcaoInstrumental) ultimo).getResultadoTentativa().get().isLimiteAtingidoAgora(),
                "incógnita: o último registro persistido é o que atingiu o limite");
        servico.reiniciarAtividadeAtual();
        for (String chave : new String[] {"parte1", "parte2", "todo"}) {
            Map<String, Object> papel = (Map<String, Object>) modelagem.get(chave);
            if (papel != null && !String.valueOf(papel.get("id")).equals(alvo)) {
                servico.posicionarValorConhecido(String.valueOf(papel.get("id")), String.valueOf(papel.get("id")));
            }
        }
        int antes = registros.size();
        servico.engatarIncognita(alvo, alvo);
        servico.proporValor(alvo, correto + 9);
        exigir(registros.size() > antes, "incógnita: reiniciar reabre o registro (" + antes + "->" + registros.size() + ")");
        System.out.println("incógnita (linhas após cada erro): " + passos);
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
