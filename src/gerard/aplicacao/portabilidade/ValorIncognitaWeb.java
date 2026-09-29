package gerard.aplicacao.portabilidade;

import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
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
final class ValorIncognitaWeb {

    private ValorIncognitaWeb() {
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
            EscopoTentativaWeb escopo) {
        RegistroAcaoInstrumental registro = incognita.avaliarAcao(
                incognita.getFluxoTentativas().iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                TarefaInteracao.TEXTO, proposta, esperado, contexto);
        escopo.persistir(registro);
        boolean aceita = registro.possuiCriterioAplicavel()
                ? registro.foiCorreta()
                : Boolean.TRUE.equals(incognita.correspondeAoEsperado(proposta, esperado));
        ResultadoRegistroTentativaPapel tentativa = registro.getResultadoTentativa().orElse(null);
        boolean rejeitada = !aceita && tentativa != null;
        Map<String, Object> resultado = new LinkedHashMap<String, Object>();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : registro.getTipoDiagnosticoFactual());
        // O texto real exibido (aviso de limite ou pergunta de confirmação)
        // vem de MensagemFeedbackIncognitaWeb, como no desktop.
        resultado.put("chave_mensagem", rejeitada
                ? MensagemFeedbackIncognitaWeb.resolver(incognita.getFluxoTentativas(), tentativa)
                : null);
        resultado.put("limite_atingido", Boolean.valueOf(
                rejeitada && MensagemFeedbackIncognitaWeb.limiteAtingido(tentativa)));
        resultado.put("rejeicoes_consecutivas", Integer.valueOf(
                tentativa == null ? 0 : tentativa.getRejeicoesConsecutivas()));
        return new Resultado(resultado, aceita);
    }
}
