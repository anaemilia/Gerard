import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.InvarianteOperatorio;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do objeto piloto da nova arquitetura
 * (gerard.dominio.campoaditivo.PapelQuantitativo) — mesmo espírito de
 * TesteBaseConhecimento: não é um framework de testes, só falha alto
 * (AssertionError) se o piloto quebrar. Não toca em Main.java nem em
 * nenhum caminho de produção — o piloto é demonstrado isoladamente, como
 * pede a etapa de implementação piloto.
 */
public class TestePilotoPapelQuantitativo {

    public static void main(String[] args) {
        List<EventoDominio> eventosCapturados = new ArrayList<>();
        PublicadorEventoDominio publicador = eventosCapturados::add;

        System.out.println("=== Piloto: PapelQuantitativo (Composição de Medidas) ===");

        PapelQuantitativo parte1 = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2 = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todo = PapelQuantitativo.todo(publicador);

        checar("parte1 começa como incógnita", String.valueOf(parte1.ehIncognita()), "true");

        Optional<DiagnosticoErroPapel> r1 = parte1.posicionar(new NumeroNatural(8));
        checar("posicionar 8 em Parte1 é aceito", String.valueOf(r1.isPresent()), "false");
        checar("parte1 deixa de ser incógnita", String.valueOf(parte1.ehIncognita()), "false");

        parte2.posicionar(new NumeroNatural(6));
        todo.posicionar(new NumeroNatural(14));

        InvarianteOperatorio invariante = InvarianteOperatorio.composicaoDeMedidas();
        System.out.println("invariante: " + invariante.explicar());
        checar("invariante Todo=Parte1+Parte2 satisfeito (8+6=14)",
                String.valueOf(invariante.verificar(parte1, parte2, todo)), "true");

        System.out.println();
        System.out.println("=== Rejeição de valor fora do domínio ===");
        PapelQuantitativo outraParte = PapelQuantitativo.parte1(publicador);
        Optional<DiagnosticoErroPapel> r2 = outraParte.posicionar(new NumeroInteiro(-3));
        checar("Parte (NATURAIS) rejeita valor negativo", String.valueOf(r2.isPresent()), "true");
        checar("diagnóstico traz tipo de erro correto", r2.get().getTipo().name(), "VALOR_FORA_DO_DOMINIO");
        checar("diagnóstico traz chave de mensagem", r2.get().getChaveMensagem(),
                "erro.papel.valorForaDoDominio");
        checar("diagnóstico traz chave de feedback pedagógico", r2.get().getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("diagnóstico traz chave de sugestão de correção", r2.get().getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");
        checar("outraParte permanece incógnita após rejeição", String.valueOf(outraParte.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Compatibilidade entre papéis ===");
        PapelQuantitativo outraTodo = PapelQuantitativo.todo(publicador);
        checar("todo é compatível consigo mesmo (outra instância, mesma chave)",
                String.valueOf(todo.compativelCom(outraTodo)), "true");
        checar("parte1 não é compatível com todo", String.valueOf(parte1.compativelCom(todo)), "false");

        System.out.println();
        System.out.println("=== Eventos semânticos publicados ===");
        System.out.println("total de eventos capturados: " + eventosCapturados.size());
        checar("cada posicionamento tentado gera exatamente um evento",
                String.valueOf(eventosCapturados.size()), "4");
        EventoDominio ultimo = eventosCapturados.get(eventosCapturados.size() - 1);
        System.out.println("último evento: " + ultimo.getTipo() + " " + ultimo.paraMapa());
        checar("último evento é uma rejeição", ultimo.getTipo(), "VALOR_REJEITADO");

        System.out.println();
        System.out.println("=== Serialização ===");
        System.out.println("parte1.paraMapa() = " + parte1.paraMapa());
        checar("mapa serializado traz o valor correto", String.valueOf(parte1.paraMapa().get("valor_atual")), "8");

        System.out.println();
        System.out.println("=== Funciona sem publicador (uso isolado, sem infraestrutura) ===");
        PapelQuantitativo semPublicador = PapelQuantitativo.parte1(null);
        semPublicador.posicionar(new NumeroNatural(5));
        checar("papel sem publicador ainda funciona", String.valueOf(semPublicador.valorAtual().valorOuNull()), "5");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
