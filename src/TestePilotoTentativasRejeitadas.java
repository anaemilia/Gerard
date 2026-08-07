import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.TipoErroPapel;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.EventoPapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do fluxo de tentativas rejeitadas + action_id
 * (REFERENCE.md §4.8, cardinalidade ação:evento, Alternativa B) —
 * PapelQuantitativo.registrarTentativa/restaurar/estaBloqueadoPorLimiteTentativas.
 *
 * Não toca em Main.java nem em nenhum caminho de produção — mesmo padrão de
 * risco zero dos demais harnesses do pacote piloto.
 */
public class TestePilotoTentativasRejeitadas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        ContextoAcao contexto = new ContextoAcao("sessao-teste-tentativas", "usuario-local-1",
                "tentativa-1", "situacao-composicao-8-6-14", "diagrama-vergnaud-1");
        RelacaoEstruturalComposicao relacao = RelacaoEstruturalComposicao.composicaoDeMedidas();

        System.out.println("=== Sequência de 3 rejeições consecutivas atinge o limite ===");
        eventos.clear();
        PapelQuantitativo todo = FabricaPapeisComparacaoMedidas.referendo(publicador); // qualquer papel serve; usamos um genérico
        checar("começa sem bloqueio", String.valueOf(todo.estaBloqueadoPorLimiteTentativas()), "false");
        checar("começa com 0 tentativas", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "0");
        checar("começa sem action_id", String.valueOf(todo.getActionIdAtual()), "null");

        Optional<DiagnosticoErroPapel> incorreto = Optional.of(new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_INCORRETO, "erro.papel.valorIncorreto",
                "feedback.papel.valorIncorreto", "correcao.papel.valorIncorreto"));

        boolean r1 = todo.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(5));
        checar("1ª rejeição: limite ainda não atingido", String.valueOf(r1), "false");
        checar("1ª rejeição: contador = 1", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "1");
        String actionIdAposR1 = todo.getActionIdAtual();
        checar("1ª rejeição: abre action_id", String.valueOf(actionIdAposR1 != null), "true");

        boolean r2 = todo.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(6));
        checar("2ª rejeição: limite ainda não atingido", String.valueOf(r2), "false");
        checar("2ª rejeição: contador = 2", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "2");
        checar("2ª rejeição: mesmo action_id da 1ª", String.valueOf(actionIdAposR1.equals(todo.getActionIdAtual())), "true");

        boolean r3 = todo.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(7));
        checar("3ª rejeição: limite atingido agora", String.valueOf(r3), "true");
        checar("3ª rejeição: contador = 3", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "3");
        checar("3ª rejeição: papel fica bloqueado", String.valueOf(todo.estaBloqueadoPorLimiteTentativas()), "true");
        checar("3 eventos VALOR_REJEITADO publicados", String.valueOf(eventos.size()), "3");
        for (EventoDominio e : eventos) {
            EventoPapelQuantitativo evento = (EventoPapelQuantitativo) e;
            checar("evento correlacionado ao mesmo action_id", String.valueOf(actionIdAposR1.equals(evento.getActionId())), "true");
        }

        System.out.println();
        System.out.println("=== Enquanto bloqueado, novas tentativas não contam nem avaliam o valor ===");
        eventos.clear();
        boolean r4 = todo.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(8));
        checar("tentativa enquanto bloqueado: devolve false", String.valueOf(r4), "false");
        checar("tentativa enquanto bloqueado: contador não passa de 3", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "3");
        checar("tentativa enquanto bloqueado: continua bloqueado", String.valueOf(todo.estaBloqueadoPorLimiteTentativas()), "true");
        checar("publica evento de bloqueio mesmo assim", String.valueOf(eventos.size()), "1");
        EventoPapelQuantitativo eventoBloqueio = (EventoPapelQuantitativo) eventos.get(0);
        checar("diagnóstico do evento de bloqueio é BLOQUEADO_AGUARDANDO_RESTAURACAO",
                eventoBloqueio.getDiagnostico().getTipo().name(), "BLOQUEADO_AGUARDANDO_RESTAURACAO");

        System.out.println();
        System.out.println("=== restaurar() encerra o bloqueio e zera a contagem ===");
        todo.restaurar();
        checar("após restaurar: sem bloqueio", String.valueOf(todo.estaBloqueadoPorLimiteTentativas()), "false");
        checar("após restaurar: contador = 0", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "0");
        checar("após restaurar: sem action_id", String.valueOf(todo.getActionIdAtual()), "null");

        eventos.clear();
        boolean r5 = todo.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(9));
        checar("nova rejeição após restaurar: limite não atingido", String.valueOf(r5), "false");
        checar("nova rejeição após restaurar: contador = 1", String.valueOf(todo.getTentativasRejeitadasConsecutivas()), "1");
        checar("nova rejeição após restaurar: novo action_id, diferente do primeiro",
                String.valueOf(!actionIdAposR1.equals(todo.getActionIdAtual())), "true");

        System.out.println();
        System.out.println("=== Uma tentativa correta zera a contagem e encerra a ação, sem bloqueio ===");
        PapelQuantitativo outro = FabricaPapeisComparacaoMedidas.referido(publicador);
        outro.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(1));
        outro.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(2));
        checar("2 rejeições acumuladas antes do acerto", String.valueOf(outro.getTentativasRejeitadasConsecutivas()), "2");
        boolean rCorreto = outro.registrarTentativa(Optional.empty(), OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(3));
        checar("tentativa correta: devolve false", String.valueOf(rCorreto), "false");
        checar("tentativa correta: contador zera", String.valueOf(outro.getTentativasRejeitadasConsecutivas()), "0");
        checar("tentativa correta: sem action_id", String.valueOf(outro.getActionIdAtual()), "null");
        checar("tentativa correta: sem bloqueio", String.valueOf(outro.estaBloqueadoPorLimiteTentativas()), "false");

        System.out.println();
        System.out.println("=== Só conta tentativas do participante (origem == ORIGEM_USUARIO) ===");
        PapelQuantitativo sistema = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        boolean rSistema = sistema.registrarTentativa(incorreto, OrigemAcao.ORIGEM_SISTEMA, contexto, new NumeroNatural(1));
        checar("origem SISTEMA: devolve false", String.valueOf(rSistema), "false");
        checar("origem SISTEMA: não abre action_id", String.valueOf(sistema.getActionIdAtual()), "null");
        checar("origem SISTEMA: contador continua 0", String.valueOf(sistema.getTentativasRejeitadasConsecutivas()), "0");

        System.out.println();
        System.out.println("=== registrarTentativa nunca altera valorAtual (posicionar continua exclusivo para isso) ===");
        PapelQuantitativo valor = FabricaPapeisComparacaoMedidas.referendo(publicador);
        checar("começa incógnita", String.valueOf(valor.ehIncognita()), "true");
        valor.registrarTentativa(incorreto, OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(5));
        checar("continua incógnita após tentativa rejeitada", String.valueOf(valor.ehIncognita()), "true");
        valor.registrarTentativa(Optional.empty(), OrigemAcao.ORIGEM_USUARIO, contexto, new NumeroNatural(5));
        checar("continua incógnita mesmo após tentativa 'correta' (não armazena)", String.valueOf(valor.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== relação estrutural usada só para lembrar o contrato de diagnosticarValorProposto (contexto) ===");
        System.out.println("relação: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("TODOS OS TESTES DE TENTATIVAS REJEITADAS PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
