package gerard.aplicacao.portabilidade;

import gerard.aplicacao.FluxoConfirmacaoValorIncognita;
import gerard.aplicacao.ProtocoloValorIncognita;
import gerard.aplicacao.adaptacao.ResultadoExecucaoAjudaIncognita;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.semantica.numero.ValorNumerico;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proposta de valor para a incógnita, comum às categorias web. A mesma
 * {@link IncognitaQuantitativa} do desktop constitui a ação (compara com o
 * valor esperado, atualiza a sequência de rejeições do papel e produz o
 * registro factual); o valor esperado vem da relação estrutural da
 * categoria. O registro segue pela tentativa até a persistência. Após a
 * conclusão (modo exploratório) a incógnita não avalia nem registra; a
 * correspondência, também dela, só decide o que a figura mostra.
 */
final class ValorIncognita {

    private ValorIncognita() {
    }

    static final class Resultado {
        private final Map<String, Object> projecao;
        private final boolean aceita;

        private Resultado(Map<String, Object> projecao, boolean aceita) {
            this.projecao = projecao;
            this.aceita = aceita;
        }

        Map<String, Object> getProjecao() { return projecao; }
        boolean isAceita() { return aceita; }
    }

    static Resultado propor(IncognitaQuantitativa incognita, ValorNumerico proposta,
            ValorNumerico esperado, ContextoAcaoInstrumental contexto,
            EscopoTentativa escopo) {
        // A sequência (avaliar, persistir, feedback de erro, ajuda, limite, pergunta) é a MESMA do desktop:
        // FluxoConfirmacaoValorIncognita, escrita uma vez. Aqui só ficam os passos que dependem da tentativa web;
        // a ajuda e o limite chegam ao cliente pelo estado (DecisorAjudaVisual, material concreto), não por diálogo.
        final RegistroAcaoInstrumental[] avaliado = new RegistroAcaoInstrumental[1];
        FluxoConfirmacaoValorIncognita.executar(new FluxoConfirmacaoValorIncognita.Alvo() {
            public boolean ehIncognitaPreenchidaPeloProtocolo() {
                return true;
            }

            public String prepararPapelDaIncognita() {
                return null;
            }

            public IncognitaQuantitativa incognitaAtual() {
                return incognita;
            }

            public IdentidadeAcaoInstrumentalPapel iniciarAcaoDoUsuario() {
                return incognita.getFluxoTentativas().iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
            }

            public void registrarPapeisDadoModificadosSeHouver() {
            }

            public RegistroAcaoInstrumental avaliar(ProtocoloValorIncognita protocolo, IncognitaQuantitativa inc,
                    IdentidadeAcaoInstrumentalPapel identidade, String papelAlvo) {
                avaliado[0] = inc.avaliarAcao(identidade, TarefaInteracao.TEXTO, proposta, esperado, contexto);
                return avaliado[0];
            }

            public void persistir(RegistroAcaoInstrumental registro) {
                escopo.persistir(registro);
            }

            public void aplicarFeedbackVisualErro() {
                escopo.registrarFeedbackDaIncognita(avaliado[0]);
            }

            public void limparFeedbackVisualErro() {
                escopo.registrarFeedbackDaIncognita(avaliado[0]);
            }

            public ResultadoExecucaoAjudaIncognita executarAjuda(RegistroAcaoInstrumental registro,
                    ResultadoRegistroTentativaPapel resultadoTentativa) {
                return null;
            }

            public void processarLimiteTentativasAtingido(ResultadoRegistroTentativaPapel resultadoTentativa) {
            }

            public void perguntarConfirmacao(ProtocoloValorIncognita protocolo, RegistroAcaoInstrumental registro,
                    IncognitaQuantitativa inc, String papelAlvo) {
            }
        }, null, null);
        RegistroAcaoInstrumental registro = avaliado[0];
        boolean aceita = registro.possuiCriterioAplicavel()
                ? registro.foiCorreta()
                : Boolean.TRUE.equals(incognita.correspondeAoEsperado(proposta, esperado));
        ResultadoRegistroTentativaPapel tentativa = registro.getResultadoTentativa().orElse(null);
        boolean rejeitada = !aceita && tentativa != null;
        Map<String, Object> resultado = new LinkedHashMap<String, Object>();
        resultado.put("schema", ServicoAtividadeComposicao.SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : registro.getTipoDiagnosticoFactual());
        // O texto real exibido (aviso de limite ou pergunta de confirmação)
        // vem de MensagemFeedbackIncognita, como no desktop.
        resultado.put("chave_mensagem", rejeitada
                ? MensagemFeedbackIncognita.resolver(incognita.getFluxoTentativas(), tentativa)
                : null);
        resultado.put("limite_atingido", Boolean.valueOf(
                rejeitada && MensagemFeedbackIncognita.limiteAtingido(tentativa)));
        resultado.put("rejeicoes_consecutivas", Integer.valueOf(
                tentativa == null ? 0 : tentativa.getRejeicoesConsecutivas()));
        return new Resultado(resultado, aceita);
    }
}
