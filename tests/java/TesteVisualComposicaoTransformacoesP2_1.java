import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import gerard.ui.UITemaGerard;
import gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * P2.1 — validação visual determinística de Composição de Transformações.
 *
 * O harness não inventa nem recalcula conhecimento curado. Ele copia
 * literalmente duas situações validadas do catálogo canônico: a primeira
 * contém estado_intermediario e a segunda contém
 * operacao_estado_transformacao. Como ainda não há uma única situação
 * validada com os dois campos preenchidos, cada campo é verificado em sua
 * própria situação; a segunda situação, que contém as duas operações, é
 * usada para validar o protocolo visual dos seletores.
 */
public class TesteVisualComposicaoTransformacoesP2_1 {
    private static final String ID_ESTADO_INTERMEDIARIO =
            "PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_guloseimas_400916186";
    private static final String ID_DUAS_OPERACOES =
            "PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012";
    private static final int LARGURA = 1400;
    private static final int ALTURA = 800;

    public static void main(String[] args) throws Exception {
        String userHomeAnterior = System.getProperty("user.home");
        final Throwable[] erro = new Throwable[1];
        try {
            final DadosCurados dados = prepararEValidarCuradoriaTemporaria();
            escreverCuradoria(dados.arquivoCuradoria, dados.cabecalho, dados.linhaDuasOperacoes);

            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        executarValidacaoVisual(dados);
                    } catch (Throwable t) {
                        erro[0] = t;
                    }
                }
            });
        } finally {
            if (userHomeAnterior == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", userHomeAnterior);
            }
        }

        if (erro[0] != null) {
            erro[0].printStackTrace();
            System.exit(1);
        }
        System.out.println("APROVADO: P2.1 preservou os dois campos curados e validou ordem, geometria e feedback dos seletores.");
        System.exit(0);
    }

    private static DadosCurados prepararEValidarCuradoriaTemporaria() throws Exception {
        Path fonte = new File("src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv").toPath();
        List<String> linhas = Files.readAllLines(fonte, StandardCharsets.UTF_8);
        exigir(!linhas.isEmpty(), "Catálogo canônico vazio.");

        String cabecalho = linhas.get(0);
        String linhaEstadoIntermediario = localizarLinha(linhas, ID_ESTADO_INTERMEDIARIO);
        String linhaDuasOperacoes = localizarLinha(linhas, ID_DUAS_OPERACOES);
        exigir(campos(cabecalho).length == 37, "O cabeçalho canônico não possui 37 campos.");
        exigir(campos(linhaEstadoIntermediario).length == 37,
                "A situação de estado intermediário não possui 37 campos.");
        exigir(campos(linhaDuasOperacoes).length == 37,
                "A situação das duas operações não possui 37 campos.");

        Path raiz = Files.createTempDirectory("gerard-p2-1-home-");
        System.setProperty("user.home", raiz.toAbsolutePath().toString());
        Path arquivoCuradoria = raiz.resolve("Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
        escreverCuradoria(arquivoCuradoria, cabecalho, linhaEstadoIntermediario, linhaDuasOperacoes);

        RepositorioSituacoesAditivas repositorio = new RepositorioSituacoesAditivas();
        SituacaoProblemaAditiva situacaoEstado = localizarSituacao(
                repositorio.listarTodas(), ID_ESTADO_INTERMEDIARIO);
        SituacaoProblemaAditiva situacaoOperacoes = localizarSituacao(
                repositorio.listarTodas(), ID_DUAS_OPERACOES);
        exigir("33".equals(situacaoEstado.getEstadoIntermediario()),
                "estado_intermediario não foi carregado literalmente como 33.");
        exigir("soma".equals(situacaoOperacoes.getOperacaoEstadoTransformacao()),
                "operacao_estado_transformacao não foi carregada literalmente como soma.");
        exigir("soma".equals(situacaoOperacoes.getOperacaoRelacao()),
                "A primeira operação curada da situação visual deixou de ser soma.");

        return new DadosCurados(cabecalho, linhaDuasOperacoes, arquivoCuradoria);
    }

    private static void executarValidacaoVisual(DadosCurados dados) throws Exception {
        Main.TelaGerard tela = new Main.TelaGerard();
        tela.setSize(LARGURA, ALTURA);
        tela.doLayout();
        tela.idiomaSelecionado = IdiomaInterface.PORTUGUES;
        tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
        tela.categoriaSelecionadaParaAtividade = true;
        invocar(tela, "aplicarIdiomaSelecionado");

        exigir(tela.situacaoProblemaAtual != null, "A situação visual não foi carregada.");
        exigir(ID_DUAS_OPERACOES.equals(tela.situacaoProblemaAtual.getId()),
                "A interface não carregou a situação curada reservada ao teste.");
        exigir(tela.elementosVergnaud.size() == 6,
                "Composição de Transformações não criou seus seis elementos semânticos.");

        SeletorOperacaoRelacaoAluno primeiro = tela.seletorOperacaoRelacaoAluno;
        SeletorOperacaoRelacaoAluno segundo = tela.seletorOperacaoEstadoTransformacaoAluno;
        exigir(primeiro.estaAtivo(), "O seletor entre transformações não está ativo.");
        exigir(segundo.estaAtivo(), "O seletor entre estado e transformação não está ativo.");

        Rectangle primeiraSoma = area(primeiro, "areaSoma");
        Rectangle primeiraSubtracao = area(primeiro, "areaSubtracao");
        Rectangle segundaSoma = area(segundo, "areaSoma");
        Rectangle segundaSubtracao = area(segundo, "areaSubtracao");
        validarGeometria(tela, primeiraSoma, primeiraSubtracao, segundaSoma, segundaSubtracao);

        File diretorioSaida = new File(System.getProperty(
                "gerard.p2_1.saida", System.getProperty("java.io.tmpdir")));
        Files.createDirectories(diretorioSaida.toPath());
        BufferedImage antesPrimeira = renderizar(tela,
                new File(diretorioSaida, "01_antes_primeira_operacao.png"));

        clicar(tela, centroX(segundaSoma), centroY(segundaSoma));
        exigir(segundo.obterEscolhaAluno() == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                "O segundo seletor aceitou clique antes do acerto do primeiro.");
        exigir(!portaOperacoesConcluida(tela),
                "A porta de conclusão aceitou operações ainda não respondidas.");

        clicar(tela, centroX(primeiraSoma), centroY(primeiraSoma));
        exigir(primeiro.respondeuCorretamente(), "A primeira operação soma não foi aceita.");
        exigir(!portaOperacoesConcluida(tela),
                "A porta de conclusão abriu antes da resposta da segunda operação.");
        BufferedImage aposPrimeira = renderizar(tela,
                new File(diretorioSaida, "02_apos_primeira_operacao.png"));

        Rectangle regiaoSegundo = uniaoComMargem(segundaSoma, segundaSubtracao, 55, 42);
        exigir(contarPixelsDiferentes(antesPrimeira, aposPrimeira, regiaoSegundo) > 20,
                "O segundo seletor não apareceu visualmente após o primeiro acerto.");
        exigir(contarCor(aposPrimeira, primeiraSoma, UITemaGerard.COR_SUCESSO) > 5,
                "O acerto da primeira operação não recebeu feedback azul.");
        exigir(contarCor(aposPrimeira, segundaSoma, UITemaGerard.COR_SUCESSO) == 0,
                "O segundo seletor recebeu feedback de sucesso antes de ser respondido.");

        clicar(tela, centroX(segundaSoma), centroY(segundaSoma));
        exigir(segundo.respondeuCorretamente(), "A segunda operação soma não foi aceita.");
        exigir(portaOperacoesConcluida(tela),
                "A porta de conclusão não abriu após as duas operações corretas.");
        BufferedImage aposDuas = renderizar(tela,
                new File(diretorioSaida, "03_apos_duas_operacoes.png"));
        exigir(contarCor(aposDuas, segundaSoma, UITemaGerard.COR_SUCESSO) > 5,
                "O acerto da segunda operação não recebeu feedback azul.");
    }

    private static void validarGeometria(Main.TelaGerard tela,
            Rectangle primeiraSoma, Rectangle primeiraSubtracao,
            Rectangle segundaSoma, Rectangle segundaSubtracao) {
        ElementoVergnaud transformacao1 = tela.elementosVergnaud.get(0);
        ElementoVergnaud transformacao2 = tela.elementosVergnaud.get(1);
        ElementoVergnaud transformacaoResultante = tela.elementosVergnaud.get(2);

        int centroPrimeiro = (centroX(primeiraSoma) + centroX(primeiraSubtracao)) / 2;
        int centroTransformacoes = ((transformacao1.x + transformacao1.largura / 2)
                + (transformacao2.x + transformacao2.largura / 2)) / 2;
        exigir(centroPrimeiro == centroTransformacoes,
                "O primeiro seletor não deriva o centro das duas transformações.");
        exigir(Math.max(primeiraSoma.y + primeiraSoma.height,
                primeiraSubtracao.y + primeiraSubtracao.height)
                < Math.min(transformacao1.y, transformacao2.y),
                "O primeiro seletor não está acima das transformações.");

        exigir(Math.max(segundaSoma.x + segundaSoma.width,
                segundaSubtracao.x + segundaSubtracao.width) < transformacaoResultante.x,
                "O segundo seletor não está à esquerda da transformação resultante.");
        exigir((centroY(segundaSoma) + centroY(segundaSubtracao)) / 2
                == transformacaoResultante.y + transformacaoResultante.altura / 2,
                "O segundo seletor não acompanha o centro vertical da transformação resultante.");

        Rectangle[] botoes = new Rectangle[] {
            primeiraSoma, primeiraSubtracao, segundaSoma, segundaSubtracao
        };
        for (Rectangle botao : botoes) {
            for (ElementoVergnaud elemento : tela.elementosVergnaud) {
                Rectangle figura = new Rectangle(elemento.x, elemento.y,
                        elemento.largura, elemento.altura);
                exigir(!botao.intersects(figura),
                        "Um botão do seletor se sobrepõe a um elemento do diagrama.");
            }
        }
    }

    private static void clicar(Main.TelaGerard tela, int x, int y) {
        MouseEvent evento = new MouseEvent(tela, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, x, y, 1, false, MouseEvent.BUTTON1);
        tela.mousePressed(evento);
    }

    private static boolean portaOperacoesConcluida(Main.TelaGerard tela) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(
                "operacoesDeSomaSubtracaoRespondidasCorretamente");
        metodo.setAccessible(true);
        return ((Boolean) metodo.invoke(tela)).booleanValue();
    }

    private static Rectangle area(SeletorOperacaoRelacaoAluno seletor, String nome) throws Exception {
        Field campo = SeletorOperacaoRelacaoAluno.class.getDeclaredField(nome);
        campo.setAccessible(true);
        Rectangle valor = (Rectangle) campo.get(seletor);
        exigir(valor != null, "Área geométrica ausente no seletor: " + nome);
        return new Rectangle(valor);
    }

    private static void invocar(Main.TelaGerard tela, String nome) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        metodo.invoke(tela);
    }

    private static BufferedImage renderizar(Main.TelaGerard tela, File destino) throws Exception {
        BufferedImage imagem = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        tela.paint(g2);
        g2.dispose();
        ImageIO.write(imagem, "png", destino);
        return imagem;
    }

    private static int contarPixelsDiferentes(BufferedImage a, BufferedImage b, Rectangle area) {
        Rectangle limite = new Rectangle(0, 0, Math.min(a.getWidth(), b.getWidth()),
                Math.min(a.getHeight(), b.getHeight()));
        Rectangle regiao = area.intersection(limite);
        int diferentes = 0;
        for (int y = regiao.y; y < regiao.y + regiao.height; y++) {
            for (int x = regiao.x; x < regiao.x + regiao.width; x++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) diferentes++;
            }
        }
        return diferentes;
    }

    private static int contarCor(BufferedImage imagem, Rectangle area, Color cor) {
        int rgb = cor.getRGB();
        int quantidade = 0;
        Rectangle regiao = area.intersection(new Rectangle(0, 0, imagem.getWidth(), imagem.getHeight()));
        for (int y = regiao.y; y < regiao.y + regiao.height; y++) {
            for (int x = regiao.x; x < regiao.x + regiao.width; x++) {
                if (imagem.getRGB(x, y) == rgb) quantidade++;
            }
        }
        return quantidade;
    }

    private static Rectangle uniaoComMargem(Rectangle a, Rectangle b, int margemX, int margemY) {
        Rectangle uniao = a.union(b);
        uniao.grow(margemX, margemY);
        return uniao;
    }

    private static int centroX(Rectangle area) {
        return area.x + area.width / 2;
    }

    private static int centroY(Rectangle area) {
        return area.y + area.height / 2;
    }

    private static String localizarLinha(List<String> linhas, String id) {
        String prefixo = id + "\t";
        for (String linha : linhas) {
            if (linha.startsWith(prefixo)) return linha;
        }
        throw new AssertionError("Situação curada não localizada: " + id);
    }

    private static SituacaoProblemaAditiva localizarSituacao(
            List<SituacaoProblemaAditiva> situacoes, String id) {
        for (SituacaoProblemaAditiva situacao : situacoes) {
            if (id.equals(situacao.getId())) return situacao;
        }
        throw new AssertionError("Situação não carregada pelo repositório: " + id);
    }

    private static String[] campos(String linha) {
        return linha.split("\\t", -1);
    }

    private static void escreverCuradoria(Path destino, String cabecalho, String... situacoes)
            throws Exception {
        Files.createDirectories(destino.getParent());
        String[] linhas = new String[situacoes.length + 1];
        linhas[0] = cabecalho;
        System.arraycopy(situacoes, 0, linhas, 1, situacoes.length);
        Files.write(destino, Arrays.asList(linhas), StandardCharsets.UTF_8);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static final class DadosCurados {
        final String cabecalho;
        final String linhaDuasOperacoes;
        final Path arquivoCuradoria;

        DadosCurados(String cabecalho, String linhaDuasOperacoes, Path arquivoCuradoria) {
            this.cabecalho = cabecalho;
            this.linhaDuasOperacoes = linhaDuasOperacoes;
            this.arquivoCuradoria = arquivoCuradoria;
        }
    }
}
