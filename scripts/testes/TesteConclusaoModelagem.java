package gerard.campoaditivo.conclusao;

import gerard.Scaffolding.conclusao.AplicadorDestaqueConclusaoDiagrama;
import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TesteConclusaoModelagem {
    private static int verificacoes;

    public static void main(String[] args) {
        testarCicloDaIncognita();
        testarPreenchimentoAutomaticoNaoConclui();
        testarIncognitaEmDiferentesPapeis();
        testarValoresAssinadosEDecimais();
        testarIncompletaEIncorreta();
        testarCicloDoControlador();
        testarDestaqueVisual();
        testarRegistroDoProtocoloNoItem();
        System.out.println("Teste de conclusão da modelagem aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarCicloDaIncognita() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.estadoInicial", "papel.transformacao", "papel.estadoFinal");
        List<EstadoPosicionamentoModelagem> comInterrogacao = Arrays.asList(
                estado("papel.estadoInicial", "papel.estadoInicial", "25"),
                estado("papel.transformacao", "papel.transformacao", "-18,00"),
                incognita("papel.estadoFinal", "papel.estadoFinal", "?", false));
        confirmar(avaliador.avaliar(esperados, comInterrogacao)
                        == FaseConclusaoModelagem.AGUARDANDO_PREENCHIMENTO_INCOGNITA,
                "a interrogação posicionada deve aguardar o protocolo mouse/texto");
        confirmar(!avaliador.estaConcluida(esperados, comInterrogacao),
                "a interrogação ainda não conclui a modelagem");

        List<EstadoPosicionamentoModelagem> preenchida = Arrays.asList(
                estado("papel.estadoInicial", "papel.estadoInicial", "25"),
                estado("papel.transformacao", "papel.transformacao", "-18,00"),
                incognita("papel.estadoFinal", "papel.estadoFinal", "7,00", true));
        confirmar(avaliador.estaConcluida(esperados, preenchida),
                "a modelagem conclui após a incógnita ser preenchida pelo protocolo mouse/texto");
    }

    private static void testarPreenchimentoAutomaticoNaoConclui() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        List<EstadoPosicionamentoModelagem> autoPreenchida = Arrays.asList(
                estado("papel.parte1", "papel.parte1", "6"),
                estado("papel.parte2", "papel.parte2", "8"),
                incognita("papel.todo", "papel.todo", "14", false));
        confirmar(!avaliador.estaConcluida(esperados, autoPreenchida),
                "número automático sobre a incógnita não pode concluir a tarefa");
        confirmar(avaliador.avaliar(esperados, autoPreenchida)
                        == FaseConclusaoModelagem.INCOMPLETA,
                "preenchimento automático indevido deve ser classificado como incompleto");
    }

    private static void testarIncognitaEmDiferentesPapeis() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();

        List<String> composicao = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        confirmar(avaliador.estaConcluida(composicao, Arrays.asList(
                incognita("papel.parte1", "papel.parte1", "4", true),
                estado("papel.parte2", "papel.parte2", "7"),
                estado("papel.todo", "papel.todo", "11"))),
                "a incógnita pode ocupar parte 1");

        List<String> comparacao = Arrays.asList(
                "papel.referido", "papel.diferenca", "papel.referendo");
        confirmar(avaliador.estaConcluida(comparacao, Arrays.asList(
                estado("papel.referido", "papel.referido", "6"),
                incognita("papel.diferenca", "papel.diferenca", "+8", true),
                estado("papel.referendo", "papel.referendo", "14"))),
                "a incógnita pode ocupar o valor relativo");

        List<String> composta = Arrays.asList(
                "papel.estadoInicial", "papel.transformacao1",
                "papel.estadoIntermediario", "papel.transformacao2",
                "papel.estadoFinal");
        confirmar(avaliador.estaConcluida(composta, Arrays.asList(
                estado("papel.estadoInicial", "papel.estadoInicial", "25"),
                estado("papel.transformacao1", "papel.transformacao1", "-2"),
                incognita("papel.estadoIntermediario", "papel.estadoIntermediario", "23", true),
                estado("papel.transformacao2", "papel.transformacao2", "-5"),
                estado("papel.estadoFinal", "papel.estadoFinal", "18"))),
                "a incógnita pode ocupar o estado intermediário");
    }

    private static void testarValoresAssinadosEDecimais() {
        PoliticaValorNumericoConclusao politica = new PoliticaValorNumericoConclusao();
        confirmar(politica.ehNumero("18,00"), "decimal com vírgula deve ser aceito");
        confirmar(politica.ehNumero("-18.00"), "decimal assinado com ponto deve ser aceito");
        confirmar(politica.ehNumero("+7"), "inteiro positivo explícito deve ser aceito");
        confirmar(!politica.ehNumero("?"), "interrogação não é valor numérico concluído");
        confirmar(!politica.ehNumero("sete"), "texto livre não é valor numérico concluído");
    }

    private static void testarIncompletaEIncorreta() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        confirmar(!avaliador.estaConcluida(esperados, Arrays.asList(
                estado("papel.parte1", "papel.parte1", "4"),
                incognita("papel.parte2", "papel.parte2", "7", true))),
                "um papel ausente mantém a modelagem incompleta");
        confirmar(!avaliador.estaConcluida(esperados, Arrays.asList(
                estado("papel.parte1", "papel.parte1", "4"),
                incognita("papel.parte2", "papel.todo", "7", true),
                estado("papel.todo", "papel.parte2", "11"))),
                "posicionamentos trocados não podem concluir a modelagem");
        confirmar(!avaliador.estaConcluida(esperados, Arrays.asList(
                estado("papel.parte1", "papel.parte1", "4"),
                estado("papel.parte2", "papel.parte2", "7"),
                estado("papel.todo", "papel.todo", "11"))),
                "uma tarefa sem o item originalmente desconhecido não conclui");
    }

    private static void testarCicloDoControlador() {
        ControladorConclusaoModelagem controlador = new ControladorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.referido", "papel.diferenca", "papel.referendo");
        List<EstadoPosicionamentoModelagem> aguardando = Arrays.asList(
                estado("papel.referido", "papel.referido", "6"),
                incognita("papel.diferenca", "papel.diferenca", "?", false),
                estado("papel.referendo", "papel.referendo", "14"));
        confirmar(controlador.atualizar(esperados, aguardando)
                        == AtualizacaoConclusaoModelagem.CONTINUA_INCOMPLETA,
                "a fase aguardando texto não dispara o tip final");
        confirmar(controlador.getFase()
                        == FaseConclusaoModelagem.AGUARDANDO_PREENCHIMENTO_INCOGNITA,
                "o controlador deve registrar a fase de espera da incógnita");

        List<EstadoPosicionamentoModelagem> completos = Arrays.asList(
                estado("papel.referido", "papel.referido", "6"),
                incognita("papel.diferenca", "papel.diferenca", "+8", true),
                estado("papel.referendo", "papel.referendo", "14"));
        confirmar(controlador.atualizar(esperados, completos)
                        == AtualizacaoConclusaoModelagem.CONCLUIDA_AGORA,
                "o preenchimento pelo mouse/texto deve gerar a conclusão");
        confirmar(controlador.deveApresentarTip(), "tip deve ser apresentado uma vez");
        controlador.registrarTipApresentado();
        confirmar(!controlador.deveApresentarTip(), "tip não deve reaparecer continuamente");
        confirmar(controlador.atualizar(esperados,
                        new ArrayList<EstadoPosicionamentoModelagem>())
                        == AtualizacaoConclusaoModelagem.DEIXOU_DE_ESTAR_CONCLUIDA,
                "nova manipulação deve retirar o estado de conclusão");
    }

    private static void testarDestaqueVisual() {
        ElementoVergnaud elemento = new ElementoVergnaud(0, 0, 40, 40,
                TipoFiguraDiagrama.RETANGULO, "Estado",
                new Rectangle(0, 0, 100, 100), false);
        ConectorVergnaud conector = new ConectorVergnaud(
                TipoConectorDiagrama.SETA, 0, 0, 50, 0, "",
                new Rectangle(0, 0, 100, 100));
        ItemTextoArrastavel item = new ItemTextoArrastavel(0, 220, 30, 24,
                "4", false, "4", "papel.estadoInicial");
        AplicadorDestaqueConclusaoDiagrama aplicador =
                new AplicadorDestaqueConclusaoDiagrama();
        aplicador.aplicar(true, Arrays.asList(elemento),
                Arrays.asList(conector), Arrays.asList(item));
        confirmar(elemento.isConclusaoDestacada()
                        && conector.isConclusaoDestacada()
                        && item.isConclusaoDestacada(),
                "elementos, conectores e itens devem receber destaque azul");
    }

    private static void testarRegistroDoProtocoloNoItem() {
        ItemTextoArrastavel incognita = new ItemTextoArrastavel(
                0, 220, 30, 24, "?", true, "?", "papel.todo");
        confirmar(!incognita.isPreenchidoPeloProtocoloMouseTexto(),
                "a incógnita inicia sem registro de preenchimento");
        incognita.registrarPreenchimentoPeloProtocoloMouseTexto();
        confirmar(incognita.isPreenchidoPeloProtocoloMouseTexto(),
                "o item deve registrar o protocolo mouse/texto");
        ItemTextoArrastavel conhecido = new ItemTextoArrastavel(
                0, 220, 30, 24, "4", false, "4", "papel.parte1");
        conhecido.registrarPreenchimentoPeloProtocoloMouseTexto();
        confirmar(!conhecido.isPreenchidoPeloProtocoloMouseTexto(),
                "um número conhecido não pode se tornar incógnita original");
    }

    private static EstadoPosicionamentoModelagem estado(
            String item, String alvo, String valor) {
        return new EstadoPosicionamentoModelagem(
                item, alvo, valor, true, false, false);
    }

    private static EstadoPosicionamentoModelagem incognita(
            String item, String alvo, String valor, boolean preenchida) {
        return new EstadoPosicionamentoModelagem(
                item, alvo, valor, true, true, preenchida);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
