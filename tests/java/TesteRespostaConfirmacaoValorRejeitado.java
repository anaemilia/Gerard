import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.TipoErroPapel;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.EventoPapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Revisão de 2026-10-02: Sim/Não permanece registrado na sequência, mas
 * somente propostas erradas incrementam o limite de três.
 */
public class TesteRespostaConfirmacaoValorRejeitado {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<EventoDominio>();
        PublicadorEventoDominio publicador = eventos::add;
        ContextoAcao contexto = new ContextoAcao("s", "u", "t", "sit", "diag");
        Optional<DiagnosticoErroPapel> incorreto = Optional.of(new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_INCORRETO, "erro.papel.valorIncorreto", null, null));

        // Sem sequência aberta não há pergunta: nada é registrado.
        PapelQuantitativo semRejeicao = FabricaPapeisComparacaoMedidas.referendo(publicador);
        ResultadoRegistroTentativaPapel ignorada = semRejeicao.registrarRespostaConfirmacaoValorRejeitado(
                id(semRejeicao), true, contexto, new NumeroNatural(5));
        exigir(!ignorada.isAcaoRegistrada() && eventos.isEmpty(),
                "Sem valor rejeitado, a resposta não pode ser registrada.");

        // valor errado (1) -> Sim (1) -> Não (1): confirmações não incrementam.
        PapelQuantitativo papel = FabricaPapeisComparacaoMedidas.referendo(publicador);
        ResultadoRegistroTentativaPapel r1 = papel.registrarTentativaComIdentidade(
                id(papel), incorreto, contexto, new NumeroNatural(5));
        ResultadoRegistroTentativaPapel sim = papel.registrarRespostaConfirmacaoValorRejeitado(
                id(papel), true, contexto, new NumeroNatural(5));
        exigir(sim.isAcaoRegistrada() && !sim.isCorreta(), "Sim deve ser tentativa registrada.");
        exigir(sim.getRejeicoesConsecutivas() == 1 && !sim.isLimiteAtingidoAgora(),
                "Sim preserva a contagem da primeira proposta.");
        ResultadoRegistroTentativaPapel nao = papel.registrarRespostaConfirmacaoValorRejeitado(
                id(papel), false, contexto, new NumeroNatural(5));
        exigir(nao.getRejeicoesConsecutivas() == 1 && !nao.isLimiteAtingidoAgora()
                        && !papel.estaBloqueadoPorLimiteTentativas(),
                "Não também preserva a contagem da proposta.");

        Set<String> acoes = new HashSet<String>();
        acoes.add(r1.getActionId()); acoes.add(sim.getActionId()); acoes.add(nao.getActionId());
        exigir(acoes.size() == 3, "Cada resposta recebe action_id próprio.");
        exigir(r1.getRejectionSequenceId().equals(sim.getRejectionSequenceId())
                        && r1.getRejectionSequenceId().equals(nao.getRejectionSequenceId()),
                "Valor e respostas pertencem à mesma sequência (a ação original).");

        EventoPapelQuantitativo eSim = (EventoPapelQuantitativo) eventos.get(1);
        EventoPapelQuantitativo eNao = (EventoPapelQuantitativo) eventos.get(2);
        exigir(eSim.getDiagnostico().getTipo() == TipoErroPapel.CONFIRMOU_VALOR_REJEITADO
                        && eNao.getDiagnostico().getTipo() == TipoErroPapel.RETIROU_VALOR_REJEITADO,
                "O diagnóstico distingue confirmação e retirada.");
        ResultadoRegistroTentativaPapel r2 = papel.registrarTentativaComIdentidade(
                id(papel), incorreto, contexto, new NumeroNatural(6));
        exigir(r2.getRejeicoesConsecutivas() == 2 && !r2.isLimiteAtingidoAgora(),
                "Segunda proposta errada não atinge limite.");
        papel.registrarRespostaConfirmacaoValorRejeitado(id(papel), true, contexto, new NumeroNatural(6));
        ResultadoRegistroTentativaPapel r3 = papel.registrarTentativaComIdentidade(
                id(papel), incorreto, contexto, new NumeroNatural(7));
        exigir(r3.getRejeicoesConsecutivas() == 3 && r3.isLimiteAtingidoAgora(),
                "Somente terceira proposta errada atinge limite.");

        // Acerto encerra a sequência: resposta posterior não é registrada.
        PapelQuantitativo outro = FabricaPapeisComparacaoMedidas.referendo(publicador);
        outro.registrarTentativaComIdentidade(id(outro), incorreto, contexto, new NumeroNatural(5));
        outro.registrarTentativaComIdentidade(id(outro), Optional.<DiagnosticoErroPapel>empty(),
                contexto, new NumeroNatural(14));
        exigir(!outro.registrarRespostaConfirmacaoValorRejeitado(
                id(outro), true, contexto, new NumeroNatural(5)).isAcaoRegistrada(),
                "Depois do acerto não há sequência aberta.");

        System.out.println("Teste aprovado: respostas Sim/Não são tentativas da mesma sequência e não contam para o limite.");
    }

    private static IdentidadeAcaoInstrumentalPapel id(PapelQuantitativo papel) {
        return papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
    }

    private static void exigir(boolean c, String m) { if (!c) throw new AssertionError(m); }
}
