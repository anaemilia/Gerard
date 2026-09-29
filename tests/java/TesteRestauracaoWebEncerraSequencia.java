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

/** Regressão: a restauração web captura a sequência antes de recriar os papéis. */
public final class TesteRestauracaoWebEncerraSequencia {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        PortaGravadora porta = new PortaGravadora();
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(5), IdiomaInterface.PORTUGUES, porta);

        Map<String, Object> estado = null;
        for (int i = 0; i < 200; i++) {
            servico.sortearMedidas();
            Map<String, Object> classificacao = servico.escolherCategoria("COMPOSICAO_MEDIDAS");
            if (Boolean.TRUE.equals(classificacao.get("correta"))) {
                estado = (Map<String, Object>) classificacao.get("estado");
                break;
            }
        }
        exigir(estado != null, "não sorteou Composição de Medidas");

        Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
        String alvo = String.valueOf(modelagem.get("papel_desconhecido_original"));
        Map<String, Integer> conhecidos = new LinkedHashMap<String, Integer>();
        for (Object item : papeis(modelagem)) {
            Map<String, Object> papel = (Map<String, Object>) item;
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            Map<String, Object> posicionado = servico.posicionarValorConhecido(id, id);
            Map<String, Object> modelagemPosicionada = (Map<String, Object>)
                    ((Map<String, Object>) posicionado.get("estado")).get("modelagem");
            for (Object outro : papeis(modelagemPosicionada)) {
                Map<String, Object> candidato = (Map<String, Object>) outro;
                if (id.equals(candidato.get("id"))) {
                    conhecidos.put(id, ((Number) candidato.get("valor")).intValue());
                }
            }
        }
        servico.engatarIncognita(alvo, alvo);
        int correto = valorCorreto(alvo, conhecidos);
        Map<String, Object> rejeicao = servico.proporValor(alvo, correto + 1);
        exigir(Boolean.FALSE.equals(rejeicao.get("aceita")), "o valor deve ser rejeitado");
        String sequencia = porta.ultimo().getRejectionSequenceId();
        exigir(sequencia != null && !sequencia.isEmpty(), "a rejeição deve abrir sequência");

        servico.reiniciarAtividadeAtual();
        exigir(porta.ultimaRestauracao != null, "a restauração deve ser persistida");
        exigir(porta.ultimaRestauracao.getSequenciasRejeicaoEncerradas().contains(sequencia),
                "a restauração deve listar a sequência encerrada: " + sequencia);
        exigir(porta.ultimaRestauracao.getPapeisParticipantes().contains(alvo),
                "a restauração deve referenciar o papel participante: " + alvo);

        System.out.println("APROVADO: restauração web encerra e registra a sequência de rejeição aberta.");
    }

    @SuppressWarnings("unchecked")
    private static List<Object> papeis(Map<String, Object> modelagem) {
        if (modelagem.get("papeis") != null) return (List<Object>) modelagem.get("papeis");
        List<Object> lista = new ArrayList<Object>();
        for (String chave : new String[] {"parte1", "parte2", "todo"}) lista.add(modelagem.get(chave));
        return lista;
    }

    private static int valorCorreto(String alvo, Map<String, Integer> conhecidos) {
        if (alvo.contains("todo")) {
            int soma = 0; for (int valor : conhecidos.values()) soma += valor; return soma;
        }
        Integer todo = null;
        Integer outraParte = null;
        for (Map.Entry<String, Integer> entrada : conhecidos.entrySet()) {
            if (entrada.getKey().contains("todo")) todo = entrada.getValue();
            else if (!entrada.getKey().equals(alvo)) outraParte = entrada.getValue();
        }
        if (todo == null || outraParte == null) throw new AssertionError("valores insuficientes: " + conhecidos);
        return todo.intValue() - outraParte.intValue();
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static final class PortaGravadora implements PortaRegistroAtividadeWeb {
        final List<RegistroFactualAcaoInstrumental> registros = new ArrayList<RegistroFactualAcaoInstrumental>();
        RegistroAcaoRestauracaoModelagem ultimaRestauracao;
        RegistroFactualAcaoInstrumental ultimo() { return registros.get(registros.size() - 1); }
        public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
        public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                String e, String f, String g, String h, String i) { }
        public void persistir(RegistroFactualAcaoInstrumental registro) { registros.add(registro); }
        public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem registro) {
            ultimaRestauracao = registro;
        }
    }
}
