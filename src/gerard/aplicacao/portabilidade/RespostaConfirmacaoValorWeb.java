package gerard.aplicacao.portabilidade;

import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.semantica.numero.ValorNumerico;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resposta Sim/Não à confirmação de um valor já rejeitado da incógnita,
 * comum às categorias web (decisão da usuária, 2026-09-28). A mesma
 * {@link IncognitaQuantitativa} do desktop constitui a tentativa, na mesma
 * sequência de rejeições e contando para o limite; o registro segue pela
 * tentativa até a persistência (decisão de 2026-09-29). Este adaptador só
 * projeta o resultado. Nunca reabre a pergunta: após a resposta só há aviso
 * quando o limite é atingido.
 */
final class RespostaConfirmacaoValorWeb {

    private RespostaConfirmacaoValorWeb() {
    }

    static Map<String, Object> responder(IncognitaQuantitativa incognita,
            boolean confirmou, ValorNumerico proposta, ValorNumerico esperado,
            ContextoAcaoInstrumental contexto, EscopoTentativaWeb escopo) {
        RegistroAcaoInstrumental registro = incognita.avaliarRespostaConfirmacao(
                incognita.getFluxoTentativas().iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                confirmou, proposta, esperado, contexto);
        ResultadoRegistroTentativaPapel tentativa = registro == null
                ? null : registro.getResultadoTentativa().orElse(null);
        if (registro != null) {
            escopo.persistir(registro);
        }
        boolean registrada = tentativa != null && tentativa.isAcaoRegistrada();
        boolean limite = registrada && MensagemFeedbackIncognitaWeb.limiteAtingido(tentativa);
        Map<String, Object> resultado = new LinkedHashMap<String, Object>();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("registrada", Boolean.valueOf(registrada));
        resultado.put("action_id", registrada ? tentativa.getActionId() : null);
        resultado.put("rejection_sequence_id", registrada ? tentativa.getRejectionSequenceId() : null);
        resultado.put("aceita", Boolean.FALSE);
        resultado.put("resposta", confirmou ? "SIM" : "NAO");
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(tentativa == null ? 0 : tentativa.getRejeicoesConsecutivas()));
        resultado.put("limite_atingido", Boolean.valueOf(limite));
        resultado.put("chave_mensagem", limite
                ? MensagemFeedbackIncognitaWeb.resolver(incognita.getFluxoTentativas(), tentativa)
                : null);
        return resultado;
    }
}
