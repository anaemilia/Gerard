import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Composição de Medidas — versão corrigida
 * (baseline v2): distingue relação estrutural formal de invariante
 * operatório, testa origem/contexto das ações, Null Object do publicador,
 * e os estados explícitos de consistência (não mais boolean).
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoComposicaoMedidas {

    public static void main(String[] args) {
        List<EventoDominio> eventosCapturados = new ArrayList<>();
        PublicadorEventoDominio publicador = eventosCapturados::add;
        RelacaoEstruturalComposicao relacao = RelacaoEstruturalComposicao.composicaoDeMedidas();

        System.out.println("=== Piloto: PapelQuantitativo (Composição de Medidas) ===");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        PapelQuantitativo parte1 = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2 = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todo = PapelQuantitativo.todo(publicador);

        checar("parte1 começa como incógnita", String.valueOf(parte1.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== EstadoConsistencia.REPRESENTACAO_INCOMPLETA antes de preencher ===");
        checar("consistência com papéis vazios é REPRESENTACAO_INCOMPLETA",
                relacao.verificarConsistencia(parte1, parte2, todo).name(), "REPRESENTACAO_INCOMPLETA");

        ContextoAcao contexto = new ContextoAcao("sessao-teste-1", "usuario-local-1", "tentativa-1",
                "situacao-composicao-8-6-14", "diagrama-vergnaud-1");

        Optional<DiagnosticoErroPapel> r1 = parte1.posicionar(new NumeroNatural(8), OrigemAcao.ORIGEM_USUARIO, contexto);
        checar("posicionar 8 em Parte1 (ação do usuário, com contexto) é aceito", String.valueOf(r1.isPresent()), "false");
        checar("parte1 deixa de ser incógnita", String.valueOf(parte1.ehIncognita()), "false");

        parte2.posicionar(new NumeroNatural(6));
        todo.posicionar(new NumeroNatural(14));

        System.out.println();
        System.out.println("=== Relação estrutural (não invariante operatório) ===");
        checar("relação estrutural CONSISTENTE (8+6=14)",
                relacao.verificarConsistencia(parte1, parte2, todo).name(), "CONSISTENTE");

        PapelQuantitativo parte1x = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2x = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todox = PapelQuantitativo.todo(publicador);
        parte1x.posicionar(new NumeroNatural(8));
        parte2x.posicionar(new NumeroNatural(6));
        todox.posicionar(new NumeroNatural(100));
        checar("representação estruturalmente inconsistente (8+6 != 100)",
                relacao.verificarConsistencia(parte1x, parte2x, todox).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== Rejeição de valor fora do domínio ===");
        PapelQuantitativo outraParte = PapelQuantitativo.parte1(publicador);
        Optional<DiagnosticoErroPapel> r2 = outraParte.posicionar(new NumeroInteiro(-3));
        checar("Parte (NATURAIS) rejeita valor negativo", String.valueOf(r2.isPresent()), "true");
        checar("diagnóstico traz tipo de erro correto", r2.get().getTipo().name(), "VALOR_FORA_DO_DOMINIO");
        checar("diagnóstico traz chave de mensagem", r2.get().getChaveMensagem(), "erro.papel.valorForaDoDominio");
        checar("diagnóstico traz chave de feedback pedagógico", r2.get().getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("diagnóstico traz chave de sugestão de correção", r2.get().getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");
        checar("outraParte permanece incógnita após rejeição", String.valueOf(outraParte.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Identidade semântica (não mais chamada de 'compatibilidade') ===");
        PapelQuantitativo outraTodo = PapelQuantitativo.todo(publicador);
        checar("todo tem a mesma identidade semântica que outra instância de todo",
                String.valueOf(todo.mesmaIdentidadeSemantica(outraTodo)), "true");
        checar("parte1 não tem a mesma identidade semântica que todo",
                String.valueOf(parte1.mesmaIdentidadeSemantica(todo)), "false");

        System.out.println();
        System.out.println("=== Origem da ação, id_acao e estado anterior/posterior no evento ===");
        EventoDominio eventoDoUsuario = eventosCapturados.get(0);
        checarCampoEvento(eventoDoUsuario, "origem_da_acao", "ORIGEM_USUARIO");
        checarCampoEvento(eventoDoUsuario, "papel_semantico", "papel.parte1");
        checarCampoEvento(eventoDoUsuario, "estado_anterior", "?");
        checarCampoEvento(eventoDoUsuario, "estado_posterior", "8");
        checarCampoEvento(eventoDoUsuario, "id_sessao", "sessao-teste-1");
        checarCampoEvento(eventoDoUsuario, "id_tentativa", "tentativa-1");
        checar("id_acao presente e não vazio", String.valueOf(eventoDoUsuario.paraMapa().get("id_acao") != null
                && !eventoDoUsuario.paraMapa().get("id_acao").toString().isEmpty()), "true");
        checar("resultado ACEITO presente na chave 'resultado'", String.valueOf(eventoDoUsuario.paraMapa().get("resultado")), "ACEITO");

        System.out.println();
        System.out.println("=== Eventos semânticos publicados ===");
        System.out.println("total de eventos capturados: " + eventosCapturados.size());
        checar("cada posicionamento tentado gera exatamente um evento",
                String.valueOf(eventosCapturados.size()), "7");

        System.out.println();
        System.out.println("=== Serialização ===");
        System.out.println("parte1.paraMapa() = " + parte1.paraMapa());
        checar("mapa serializado traz o valor correto", String.valueOf(parte1.paraMapa().get("valor_atual")), "8");

        System.out.println();
        System.out.println("=== Null Object do publicador (nunca null internamente) ===");
        PapelQuantitativo semPublicador = PapelQuantitativo.parte1(null);
        semPublicador.posicionar(new NumeroNatural(5));
        checar("papel construído com publicador nulo ainda funciona (Null Object)",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "5");

        PapelQuantitativo comNenhum = PapelQuantitativo.parte1(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroNatural(9));
        checar("papel construído explicitamente com PublicadorEventoDominio.NENHUM funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "9");

        System.out.println();
        System.out.println("=== calcularValorAusente com Todo desconhecido (8 + 6 = ?) ===");
        PapelQuantitativo parte1B = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2B = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todoB = PapelQuantitativo.todo(publicador);
        parte1B.posicionar(new NumeroNatural(8));
        parte2B.posicionar(new NumeroNatural(6));
        ResultadoCalculo resTodo = relacao.calcularValorAusente(parte1B, parte2B, todoB);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(todoB.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resTodo.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resTodo.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicTodo = relacao.aplicar(resTodo, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicTodo.isPresent()), "false");
        checar("Todo resolvido corretamente (14) após aplicar", String.valueOf(todoB.valorAtual().valorOuNull()), "14");

        System.out.println();
        System.out.println("=== calcularValorAusente com Parte1 desconhecida (? + 6 = 14) ===");
        PapelQuantitativo parte1C = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2C = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todoC = PapelQuantitativo.todo(publicador);
        parte2C.posicionar(new NumeroNatural(6));
        todoC.posicionar(new NumeroNatural(14));
        ResultadoCalculo resParte1 = relacao.calcularValorAusente(parte1C, parte2C, todoC);
        relacao.aplicar(resParte1, contexto);
        checar("Parte1 resolvida corretamente (8)", String.valueOf(parte1C.valorAtual().valorOuNull()), "8");

        System.out.println();
        System.out.println("=== calcularValorAusente com Parte2 desconhecida (8 + ? = 14) ===");
        PapelQuantitativo parte1D = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2D = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todoD = PapelQuantitativo.todo(publicador);
        parte1D.posicionar(new NumeroNatural(8));
        todoD.posicionar(new NumeroNatural(14));
        ResultadoCalculo resParte2 = relacao.calcularValorAusente(parte1D, parte2D, todoD);
        relacao.aplicar(resParte2, contexto);
        checar("Parte2 resolvida corretamente (6)", String.valueOf(parte2D.valorAtual().valorOuNull()), "6");

        System.out.println();
        System.out.println("=== diagnosticarValorProposto (2026-08-06): avalia o que foi proposto, não só o que falta ===");
        PapelQuantitativo parte1E = PapelQuantitativo.parte1(publicador);
        PapelQuantitativo parte2E = PapelQuantitativo.parte2(publicador);
        PapelQuantitativo todoE = PapelQuantitativo.todo(publicador);
        parte1E.posicionar(new NumeroNatural(8));
        parte2E.posicionar(new NumeroNatural(6));
        checar("proposta correta (14) -> Optional.empty()",
                String.valueOf(relacao.diagnosticarValorProposto(parte1E, parte2E, todoE, todoE, new NumeroNatural(14)).isPresent()),
                "false");
        checar("proposta com operação invertida (8 - 6 = 2, devia somar) -> OPERACAO_INVERTIDA",
                relacao.diagnosticarValorProposto(parte1E, parte2E, todoE, todoE, new NumeroNatural(2)).get().getTipo().name(),
                "OPERACAO_INVERTIDA");
        checar("proposta sem padrão reconhecido (999) -> VALOR_INCORRETO",
                relacao.diagnosticarValorProposto(parte1E, parte2E, todoE, todoE, new NumeroNatural(999)).get().getTipo().name(),
                "VALOR_INCORRETO");
        checar("proposta fora do domínio (Todo natural, -1) -> VALOR_FORA_DO_DOMINIO",
                relacao.diagnosticarValorProposto(parte1E, parte2E, todoE, todoE, new NumeroInteiro(-1)).get().getTipo().name(),
                "VALOR_FORA_DO_DOMINIO");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO PASSARAM.");
    }

    private static void checarCampoEvento(EventoDominio evento, String chaveMapa, String esperado) {
        Object obtido = evento.paraMapa().get(chaveMapa);
        checar("evento[" + chaveMapa + "]", String.valueOf(obtido), esperado);
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
