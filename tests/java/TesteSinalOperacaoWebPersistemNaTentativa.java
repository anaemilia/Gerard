import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.portabilidade.PortaRegistroAtividadeWeb;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;
import java.util.ArrayList;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Decisão da usuária (2026-09-29): as escolhas de sinal e de operação da
 * modelagem web também persistem, com os registros dos mesmos proprietários
 * do desktop (TentativaEscolhaSinalPapelQuantitativo e
 * CriterioOperacaoModelagem), pela camada exposta ao HTTP.
 */
public final class TesteSinalOperacaoWebPersistemNaTentativa {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        PortaGravadora porta = new PortaGravadora();
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(0), IdiomaInterface.PORTUGUES, porta);

        Map<String, Object> estado = localizarBonecas(servico);
        exigir(estado != null, "não sorteou a situação 'bonecas' em tentativas razoáveis");

        exigir(!servico.possuiAtividadeEscolhaOperacaoAtiva(),
                "não deveria ter escolha de operação ativa antes da classificação");

        Map<String, Object> classificacao = servico.escolherCategoria("TRANSFORMACAO_RELACAO");
        exigir(Boolean.TRUE.equals(classificacao.get("correta")),
                "TRANSFORMACAO_RELACAO deveria ser aceita para 'bonecas'");
        estado = (Map<String, Object>) classificacao.get("estado");

        Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
        String alvo = String.valueOf(modelagem.get("papel_desconhecido_original"));
        int somaEstrutural = 0;
        int sinaisEscolhidos = 0;
        for (Object item : (List<Object>) modelagem.get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) item;
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            Map<String, Object> posicionado = servico.posicionarValorConhecido(id, id);
            Map<String, Object> estadoPos = (Map<String, Object>) posicionado.get("estado");
            Map<String, Object> modelagemPos = (Map<String, Object>) estadoPos.get("modelagem");
            if (id.equals(modelagemPos.get("papel_aguardando_sinal"))) {
                sinaisEscolhidos++;
                int antesSinal = porta.registros.size();
                Map<String, Object> comSinal = servico.escolherSinalNumeroRelativo(id, "+");
                exigir(porta.registros.size() == antesSinal + 1
                                && (id + ".sinal").equals(porta.ultimo().getAlvoSemantico()),
                        "escolha de sinal deve gerar o registro do proprietário do sinal");
                exigir((comSinal.get("mensagem_sinal_divergente") != null)
                                == porta.ultimo().getResultado().name().equals("ERRADA"),
                        "a divergência exibida é o diagnóstico do proprietário");
                modelagemPos = (Map<String, Object>)
                        ((Map<String, Object>) comSinal.get("estado")).get("modelagem");
            }
            for (Object outro : (List<Object>) modelagemPos.get("papeis")) {
                Map<String, Object> candidato = (Map<String, Object>) outro;
                if (id.equals(candidato.get("id"))) {
                    somaEstrutural += ((Number) candidato.get("valor")).intValue();
                }
            }
        }

        // O bug real: esta checagem (e a requisição HTTP correspondente)
        // falhava com "a situação atual não possui escolha de operação
        // implementada" antes da correção.
        exigir(servico.possuiAtividadeEscolhaOperacaoAtiva(),
                "deveria ter escolha de operação ativa depois dos dois papéis posicionados "
                        + "('bonecas' tem operacao_relacao curado)");

        exigir(sinaisEscolhidos > 0, "o fluxo deve passar pela escolha de sinal");
        int antesErrada = porta.registros.size();
        Map<String, Object> errada = servico.escolherOperacao(null, "SOMA");
        exigir(Boolean.FALSE.equals(errada.get("aceita"))
                        && porta.registros.size() == antesErrada + 1
                        && porta.ultimo().getResultado().name().equals("ERRADA")
                        && errada.get("action_id").equals(porta.ultimo().getActionId()),
                "operação errada gera registro ERRADA com o mesmo action_id");
        Map<String, Object> operacao = servico.escolherOperacao(null, "SUBTRACAO");
        exigir(porta.ultimo().getResultado().name().equals("CORRETA"),
                "operação correta gera registro CORRETA");
        exigir(Boolean.TRUE.equals(operacao.get("aceita")),
                "SUBTRACAO deveria ser aceita para 'bonecas' (curado é subtracao)");

        servico.proporValor(alvo, somaEstrutural);

        System.out.println("APROVADO: sinal e operação web persistem pelos proprietários do desktop.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> localizarBonecas(ServicoSorteioAtividadeWeb servico) {
        for (int tentativa = 0; tentativa < 60; tentativa++) {
            Map<String, Object> estado = servico.sortearRelacoes();
            String enunciado = String.valueOf(estado.get("enunciado"));
            if (enunciado.contains("bonecas")) {
                return estado;
            }
        }
        return null;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class PortaGravadora implements PortaRegistroAtividadeWeb {
        final List<RegistroFactualAcaoInstrumental> registros = new ArrayList<RegistroFactualAcaoInstrumental>();
        RegistroFactualAcaoInstrumental ultimo() { return registros.get(registros.size() - 1); }
        public void registrarNovaSituacao(SituacaoProblemaAditiva s, String c, String e) { }
        public void registrarAcaoGranularUsuario(String a, String b, String c, String d,
                String e, String f, String g, String h, String i) { }
        public void persistir(RegistroFactualAcaoInstrumental registro) { registros.add(registro); }
        public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem r) { }
    }
}
