package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.i18n.ServicoLocalizacao;

import java.util.List;
import java.util.Map;

/**
 * Fluxo de decisão depois do azul na web (paridade com FluxoFeedbackConclusaoModelagem do desktop): o estado publica
 * os textos reais (ui.completion.*), a pausa e as ações; Não permanece; Sim e Próxima tarefa iniciam nova situação;
 * antes do azul a escolha é recusada. Decisão de interface: nada é registrado no log por isso.
 */
public final class TesteWebConclusaoFluxo {
    private static final int[] registrosGravados = new int[1];

    public static void main(String[] args) {
        ServicoSorteioAtividade servico = novoServico();
        Map<String, Object> estado = classificarComposicao(servico);
        exigir(estado.get("conclusao") == null, "antes do azul o estado não publica o fluxo de conclusão");
        try {
            servico.escolherConclusao("SIM");
            exigir(false, "escolha antes do azul deve ser recusada");
        } catch (IllegalStateException esperado) { /* ok */ }
        estado = concluir(servico, estado);
        exigir(Boolean.TRUE.equals(estado.get("concluida")), "modelagem concluída");

        Map<String, Object> conclusao = map(estado.get("conclusao"));
        exigir(conclusao != null, "depois do azul o estado publica o fluxo de conclusão");
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        Map<String, Object> textos = map(conclusao.get("textos"));
        exigir(loc.texto("ui.completion.completed").equals(textos.get("selo")), "texto do selo = ui.completion.completed");
        exigir(loc.texto("ui.completion.congratulations").equals(textos.get("pergunta")), "pergunta = ui.completion.congratulations");
        exigir(loc.texto("ui.completion.yes").equals(textos.get("sim")) && loc.texto("ui.completion.no").equals(textos.get("nao")),
                "Sim/Não = ui.completion.yes/no");
        exigir(loc.texto("ui.completion.next").equals(textos.get("proxima_tarefa")), "Próxima tarefa = ui.completion.next");
        exigir(((Number) conclusao.get("atraso_decisao_ms")).intValue() == 1150, "pausa de 1150 ms, a mesma do desktop");
        exigir(((List<?>) conclusao.get("acoes")).size() == 3, "três ações publicadas");

        String situacao = String.valueOf(estado.get("situacao_id"));
        int antes = registrosGravados[0];
        Map<String, Object> nao = servico.escolherConclusao("NAO");
        exigir(situacao.equals(String.valueOf(nao.get("situacao_id"))) && Boolean.TRUE.equals(nao.get("concluida")),
                "Não permanece na situação atual, ainda concluída");
        exigir(registrosGravados[0] == antes, "a escolha é decisão de interface: nada é registrado");

        Map<String, Object> sim = servico.escolherConclusao("SIM");
        exigir(!situacao.equals(String.valueOf(sim.get("situacao_id"))), "Sim inicia uma nova situação");
        exigir(sim.get("conclusao") == null && !Boolean.TRUE.equals(sim.get("concluida")), "a nova situação não nasce concluída");

        estado = concluir(servico, classificarComposicao(servico));
        situacao = String.valueOf(estado.get("situacao_id"));
        Map<String, Object> proxima = servico.escolherConclusao("PROXIMA_TAREFA");
        exigir(!situacao.equals(String.valueOf(proxima.get("situacao_id"))), "Próxima tarefa inicia uma nova situação");
        try {
            servico.escolherConclusao("TALVEZ");
            exigir(false, "escolha desconhecida deve ser recusada");
        } catch (IllegalArgumentException esperado) { /* ok */ }
        System.out.println("APROVADO: o estado web publica o fluxo de conclusão (textos reais, pausa, ações); Não permanece; Sim e Próxima tarefa iniciam nova situação.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object o) { return (Map<String, Object>) o; }

    private static ServicoSorteioAtividade novoServico() {
        PortaRegistroAtividade porta = new PortaRegistroAtividade() {
            public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
            public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                    String e, String f, String g, String h, String i) { registrosGravados[0]++; }
            public void persistir(RegistroFactualAcaoInstrumental r) { registrosGravados[0]++; }
            public void registrarRestauracaoModelagem(gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem r) { registrosGravados[0]++; }
        };
        return new ServicoSorteioAtividade(new gerard.aplicacao.PoliticaSorteioSituacoesAditivas(),
                new gerard.aplicacao.FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new gerard.campoaditivo.servico.CatalogoDefinicoesAditivas(),
                        new gerard.campoaditivo.curadoria.ConstrutorResultadoCurado()),
                new java.util.Random(5), gerard.idioma.IdiomaInterface.PORTUGUES, porta,
                new gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb(),
                new gerard.infraestrutura.scaffolding.AdaptadorQuestionamentoPosicionamento());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> classificarComposicao(ServicoSorteioAtividade servico) {
        for (int i = 0; i < 300; i++) {
            servico.sortearMedidas();
            Map<String, Object> r = servico.escolherCategoria("COMPOSICAO_MEDIDAS");
            if (Boolean.TRUE.equals(r.get("correta"))) return (Map<String, Object>) r.get("estado");
        }
        throw new AssertionError("não sorteou Composição de Medidas");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> concluir(ServicoSorteioAtividade servico, Map<String, Object> estado) {
        Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
        String alvo = String.valueOf(modelagem.get("papel_desconhecido_original"));
        int todo = 0, outras = 0;
        for (String chave : new String[] {"parte1", "parte2", "todo"}) {
            Map<String, Object> papel = (Map<String, Object>) modelagem.get(chave);
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            Map<String, Object> r = servico.posicionarValorConhecido(id, id);
            Map<String, Object> m2 = (Map<String, Object>) ((Map<String, Object>) r.get("estado")).get("modelagem");
            int valor = ((Number) ((Map<String, Object>) m2.get(chave)).get("valor")).intValue();
            if (id.contains("todo")) todo = valor; else outras += valor;
        }
        servico.engatarIncognita(alvo, alvo);
        int correto = alvo.contains("todo") ? outras : todo - outras;
        Map<String, Object> r = servico.proporValor(alvo, correto);
        return (Map<String, Object>) r.get("estado");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
