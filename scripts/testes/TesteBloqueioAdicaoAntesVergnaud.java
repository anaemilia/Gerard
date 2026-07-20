import gerard.Scaffolding.venn.CondicaoDiagramaVergnaudNaoVazio;
import gerard.Scaffolding.venn.CondicaoHabilitacaoAdicaoUnidades;
import gerard.Scaffolding.venn.EstadoModelagemVergnaud;
import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.venn.interacao.RepresentacaoVennEditavel;
import gerard.idioma.IdiomaInterface;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;

public final class TesteBloqueioAdicaoAntesVergnaud {
    public static void main(String[] args) throws Exception {
        testarContrato();
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    testarTela();
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });
        if (erro[0] != null) {
            throw new RuntimeException(erro[0]);
        }
        System.out.println("Teste aprovado: + e - ficam bloqueados somente com Vergnaud vazio e respeitam zero/limite.");
        System.exit(0);
    }

    private static void testarContrato() {
        final boolean[] possuiConteudo = {false};
        CondicaoHabilitacaoAdicaoUnidades condicao =
                new CondicaoDiagramaVergnaudNaoVazio(
                        new EstadoModelagemVergnaud() {
                            @Override
                            public boolean possuiConteudoSemantico() {
                                return possuiConteudo[0];
                            }
                        },
                        "ui.tooltip.venn.positionFirst");
        exigir(!condicao.estaSatisfeita(), "A condição deveria iniciar bloqueada.");
        possuiConteudo[0] = true;
        exigir(condicao.estaSatisfeita(),
                "A condição não foi liberada quando o diagrama deixou de estar vazio.");
    }

    private static void testarTela() throws Exception {
        Main.TelaGerard tela = new Main.TelaGerard();
        tela.setSize(1240, 760);
        tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
        tela.categoriaSelecionadaParaAtividade = true;
        tela.indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
        tela.situacaoProblemaAtual = new SituacaoProblemaAditiva(
                "teste",
                true,
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES,
                "Teste",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "2",
                "4",
                "2",
                "positivo",
                "referendo",
                "",
                "");
        tela.elementosVergnaud.clear();
        tela.circulosVenn.clear();
        tela.quadradinhosVenn.clear();

        for (int i = 0; i < 3; i++) {
            tela.elementosVergnaud.add(new ElementoVergnaud(
                    100 + i * 120, 300, 80, 50,
                    i == 1 ? TipoFiguraDiagrama.ELIPSE : TipoFiguraDiagrama.RETANGULO,
                    "papel" + i,
                    new Rectangle(20, 220, 620, 500),
                    false));
        }

        CirculoVenn barra = new CirculoVenn(
                800, 300, 100, 220, "Referido", 2, true);
        barra.formaRetangular = true;
        tela.circulosVenn.add(barra);

        Method possuiConteudo = Main.TelaGerard.class.getDeclaredMethod(
                "diagramaVergnaudPossuiConteudoSemantico");
        possuiConteudo.setAccessible(true);
        exigir(!((Boolean) possuiConteudo.invoke(tela)).booleanValue(),
                "O diagrama vazio foi considerado preenchido.");

        clicarAdicionar(tela, 0);
        exigir(tela.quadradinhosVenn.isEmpty(),
                "O controle + criou unidade com Vergnaud vazio.");
        clicarRemover(tela, 0);
        exigir(tela.quadradinhosVenn.isEmpty(),
                "O controle - alterou a quantidade com Vergnaud vazio.");

        // Simula preenchimento automático: há conteúdo visual, mas a flag de
        // edição explícita permanece falsa. A nova regra deve liberar os controles.
        tela.elementosVergnaud.get(0).textoEditavel = "1";
        exigir(((Boolean) possuiConteudo.invoke(tela)).booleanValue(),
                "O preenchimento automático não liberou os controles.");

        clicarAdicionar(tela, 0);
        exigir(contarNoAgrupamento(tela, 0) == 1,
                "O + não adicionou unidade após o primeiro conteúdo em Vergnaud.");
        clicarRemover(tela, 0);
        exigir(contarNoAgrupamento(tela, 0) == 0,
                "O - não removeu a unidade até zero.");
        clicarRemover(tela, 0);
        exigir(contarNoAgrupamento(tela, 0) == 0,
                "O - permitiu quantidade negativa.");

        clicarAdicionar(tela, 0);
        clicarAdicionar(tela, 0);
        exigir(contarNoAgrupamento(tela, 0) == 2,
                "O + não alcançou o limite configurado.");
        clicarAdicionar(tela, 0);
        exigir(contarNoAgrupamento(tela, 0) == 2,
                "O + ultrapassou o limite configurado.");
    }

    private static void clicarAdicionar(Main.TelaGerard tela, int indice) throws Exception {
        Rectangle area = obterAreaComplementar(tela);
        CirculoVenn agrupamento = tela.circulosVenn.get(indice);
        RepresentacaoVennEditavel representacao = new RepresentacaoVennEditavel(
                agrupamento, "", tela.operacoesUnidadesVenn);
        Rectangle controle = tela.controleAdicionarQuadradinhoVenn.obterArea(
                representacao, area);
        clicar(tela, controle.x + controle.width / 2, controle.y + controle.height / 2);
    }

    private static void clicarRemover(Main.TelaGerard tela, int indice) throws Exception {
        Rectangle area = obterAreaComplementar(tela);
        CirculoVenn agrupamento = tela.circulosVenn.get(indice);
        RepresentacaoVennEditavel representacao = new RepresentacaoVennEditavel(
                agrupamento, "", tela.operacoesUnidadesVenn);
        Rectangle controle = tela.controleRemoverQuadradinhoVenn.obterArea(
                representacao, area);
        clicar(tela, controle.x + controle.width / 2, controle.y + controle.height / 2);
    }

    private static Rectangle obterAreaComplementar(Main.TelaGerard tela) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(
                "obterAreaDiagramaAditivo");
        metodo.setAccessible(true);
        return (Rectangle) metodo.invoke(tela);
    }

    private static int contarNoAgrupamento(Main.TelaGerard tela, int indice) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(
                "contarQuadradinhosNoAgrupamento", CirculoVenn.class);
        metodo.setAccessible(true);
        return ((Integer) metodo.invoke(tela, tela.circulosVenn.get(indice))).intValue();
    }

    private static void clicar(Main.TelaGerard tela, int x, int y) {
        MouseEvent clique = new MouseEvent(
                tela, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                0, x, y, 1, false);
        tela.mousePressed(clique);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
