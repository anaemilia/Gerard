package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.RegistroAcaoEscolhaSinalPapelQuantitativo;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;

import java.util.List;
import java.util.Map;

/**
 * Escolha de sinal do número relativo na web. As mesmas
 * TentativaEscolhaSinalPapelQuantitativo do desktop (uma por papel curado com
 * sinal, criadas por SemanticaCuradaSituacao) constituem e avaliam a ação; o
 * registro segue pela tentativa até a persistência. A divergência do sinal
 * curado é o diagnóstico do proprietário, não uma comparação da tela.
 */
final class SinalNumeroRelativoWeb {

    private final Map<String, TentativaEscolhaSinalPapelQuantitativo> tentativas;

    SinalNumeroRelativoWeb(SituacaoProblemaAditiva situacao) {
        this.tentativas = SemanticaCuradaSituacao.criarTentativasEscolhaSinal(
                situacao, ServicoLocalizacao.getInstancia());
    }

    /** @return true quando o proprietário julga o sinal escolhido errado. */
    boolean avaliarDivergencia(String chavePapel, String sinal, int base,
            List<String> participantes, EscopoTentativaWeb escopo) {
        TentativaEscolhaSinalPapelQuantitativo tentativa = tentativas.get(chavePapel);
        if (tentativa == null) {
            return false;
        }
        RegistroAcaoEscolhaSinalPapelQuantitativo registro = tentativa.avaliarEscolha(
                OpcaoSinalNumeroInteiro.doSimbolo(sinal),
                ContextosAcaoInstrumentalWeb.sinal(chavePapel, base, sinal, participantes));
        escopo.persistir(registro);
        return registro.foiErrada();
    }
}
