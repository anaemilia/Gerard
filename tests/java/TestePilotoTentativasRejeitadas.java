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

/** Harness da identidade vigente: uma ação por tentativa e uma sequência para as rejeições. */
public class TestePilotoTentativasRejeitadas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<EventoDominio>();
        PublicadorEventoDominio publicador = eventos::add;
        ContextoAcao contexto = new ContextoAcao("sessao-teste-tentativas", "usuario-local-1",
                "tentativa-1", "situacao-composicao-8-6-14", "diagrama-vergnaud-1");
        Optional<DiagnosticoErroPapel> incorreto = Optional.of(new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_INCORRETO, "erro.papel.valorIncorreto",
                "feedback.papel.valorIncorreto", "correcao.papel.valorIncorreto"));

        System.out.println("=== Três rejeições: três ações e uma sequência ===");
        PapelQuantitativo papel = FabricaPapeisComparacaoMedidas.referendo(publicador);
        ResultadoRegistroTentativaPapel r1 = rejeitar(papel, incorreto, contexto, 5);
        ResultadoRegistroTentativaPapel r2 = rejeitar(papel, incorreto, contexto, 6);
        ResultadoRegistroTentativaPapel r3 = rejeitar(papel, incorreto, contexto, 7);

        Set<String> actionIds = new HashSet<String>();
        actionIds.add(r1.getActionId());
        actionIds.add(r2.getActionId());
        actionIds.add(r3.getActionId());
        exigir(actionIds.size() == 3, "Cada rejeição deve receber action_id próprio.");
        exigir(r1.getRejectionSequenceId() != null
                        && r1.getRejectionSequenceId().equals(r2.getRejectionSequenceId())
                        && r1.getRejectionSequenceId().equals(r3.getRejectionSequenceId()),
                "As três rejeições devem compartilhar uma única sequência.");
        exigir(!r1.isLimiteAtingidoAgora() && !r2.isLimiteAtingidoAgora()
                        && r3.isLimiteAtingidoAgora(),
                "Somente a terceira rejeição deve atingir o limite.");
        exigir(papel.estaBloqueadoPorLimiteTentativas(),
                "O papel deve ficar bloqueado após a terceira rejeição.");
        exigir(eventos.size() == 3, "Cada ação rejeitada deve publicar um evento.");
        for (int i = 0; i < eventos.size(); i++) {
            EventoPapelQuantitativo evento = (EventoPapelQuantitativo) eventos.get(i);
            ResultadoRegistroTentativaPapel resultado = i == 0 ? r1 : (i == 1 ? r2 : r3);
            exigir(resultado.getActionId().equals(evento.getActionId()),
                    "O evento deve conservar somente o action_id de sua ação.");
            exigir(r1.getRejectionSequenceId().equals(evento.getRejectionSequenceId()),
                    "O evento rejeitado deve conservar a sequência compartilhada.");
        }

        System.out.println("=== Tentativa bloqueada continua sendo ação, mas não amplia a sequência ===");
        eventos.clear();
        ResultadoRegistroTentativaPapel bloqueada = rejeitar(papel, incorreto, contexto, 8);
        exigir(bloqueada.isRejeitadaSemAvaliacaoPorBloqueio(),
                "A tentativa bloqueada deve ser factual, sem fingir avaliação matemática.");
        exigir(!actionIds.contains(bloqueada.getActionId()),
                "A tentativa bloqueada também deve ter action_id próprio.");
        exigir(bloqueada.getRejectionSequenceId() == null,
                "A tentativa bloqueada não pode tornar a sequência maior que três rejeições avaliadas.");
        exigir(papel.getTentativasRejeitadasConsecutivas() == 3,
                "A tentativa bloqueada não deve alterar a contagem.");

        System.out.println("=== Restaurar encerra a sequência; a próxima rejeição abre outra ===");
        String primeiraSequencia = r1.getRejectionSequenceId();
        papel.restaurar();
        exigir(papel.getRejectionSequenceIdAtual() == null,
                "Restaurar deve encerrar a sequência corrente.");
        ResultadoRegistroTentativaPapel nova = rejeitar(papel, incorreto, contexto, 9);
        exigir(!primeiraSequencia.equals(nova.getRejectionSequenceId()),
                "Uma rejeição após restaurar deve abrir sequência nova.");

        System.out.println("=== Acerto é nova ação e encerra rejeições anteriores ===");
        eventos.clear();
        IdentidadeAcaoInstrumentalPapel identidadeAcerto =
                papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        ResultadoRegistroTentativaPapel acerto = papel.registrarTentativaComIdentidade(
                identidadeAcerto, Optional.<DiagnosticoErroPapel>empty(), contexto,
                new NumeroNatural(10));
        exigir(acerto.isCorreta() && acerto.getActionId().equals(identidadeAcerto.getActionId()),
                "O acerto deve conservar sua identidade de ação.");
        exigir(acerto.getRejectionSequenceId() == null,
                "O acerto não pertence à sequência rejeitada.");
        exigir(papel.getTentativasRejeitadasConsecutivas() == 0
                        && papel.getRejectionSequenceIdAtual() == null,
                "O acerto deve encerrar a sequência corrente.");
        exigir(eventos.size() == 1
                        && "TENTATIVA_AVALIADA".equals(eventos.get(0).getTipo()),
                "O acerto deve produzir seu evento factual próprio.");

        System.out.println("=== Origem de sistema não consome tentativa do participante ===");
        PapelQuantitativo sistema = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        boolean limiteSistema = sistema.registrarTentativa(incorreto, OrigemAcao.ORIGEM_SISTEMA,
                contexto, new NumeroNatural(1));
        exigir(!limiteSistema && sistema.getUltimoActionId() == null
                        && sistema.getTentativasRejeitadasConsecutivas() == 0,
                "A API compatível deve continuar ignorando origem de sistema.");

        System.out.println("Teste aprovado: action_id separado de rejection_sequence_id.");
    }

    private static ResultadoRegistroTentativaPapel rejeitar(PapelQuantitativo papel,
            Optional<DiagnosticoErroPapel> diagnostico, ContextoAcao contexto, int valor) {
        IdentidadeAcaoInstrumentalPapel identidade =
                papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        return papel.registrarTentativaComIdentidade(identidade, diagnostico,
                contexto, new NumeroNatural(valor));
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
        System.out.println("OK - " + mensagem);
    }
}
