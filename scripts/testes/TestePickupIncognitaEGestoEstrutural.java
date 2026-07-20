import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.ControladorLimiarArrasteEstrutural;
import gerard.interacao.arraste.PoliticaGestoEstrutural;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import gerard.interacao.texto.PoliticaElementoMatematicoTexto;
import gerard.interacao.texto.PoliticaUnicidadeElementoMatematicoTexto;
import gerard.interacao.texto.ResolvedorPickupElementoMatematicoTexto;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class TestePickupIncognitaEGestoEstrutural {
    public static void main(String[] args) {
        testarPickupDiretoDaInterrogacao();
        testarFallbackDoVinculoSemantico();
        testarUnicidadePorOcorrencia();
        testarLimiarDeArrasteEstrutural();
        testarDuploCliqueNaoIniciaArraste();
        System.out.println("TestePickupIncognitaEGestoEstrutural: OK");
    }

    private static void testarPickupDiretoDaInterrogacao() {
        PoliticaElementoMatematicoTexto politica = new PoliticaElementoMatematicoTexto();
        ResolvedorPickupElementoMatematicoTexto resolvedor =
                new ResolvedorPickupElementoMatematicoTexto(politica);
        List<MarcadorTexto> marcadores = new ArrayList<MarcadorTexto>();
        marcadores.add(new MarcadorTexto(500, 70, 18, 22, "?", true, "papel.transformacao"));
        MarcadorTexto encontrado = resolvedor.encontrar(
                508, 80, marcadores, new ArrayList<ElementoTextoMovel>(), metricas());
        exigir(encontrado != null, "A interrogação deve ser capturada pelo pickup.");
        exigir("?".equals(encontrado.valor), "O pickup deve preservar a interrogação.");
        exigir("papel.transformacao".equals(encontrado.chavePapel),
                "O pickup deve preservar o papel de transformação.");
    }

    private static void testarFallbackDoVinculoSemantico() {
        PoliticaElementoMatematicoTexto politica = new PoliticaElementoMatematicoTexto();
        ResolvedorPickupElementoMatematicoTexto resolvedor =
                new ResolvedorPickupElementoMatematicoTexto(politica);
        ElementoTextoMovel elemento = new ElementoTextoMovel("comeu?");
        elemento.x = 300;
        elemento.y = 100;
        elemento.atualizarTamanho(metricas());
        elemento.vincularSemantica("papel.transformacao", 5, 6, "?");
        List<ElementoTextoMovel> elementos = new ArrayList<ElementoTextoMovel>();
        elementos.add(elemento);
        int xInterrogacao = elemento.x + metricas().stringWidth("comeu") + 3;
        MarcadorTexto encontrado = resolvedor.encontrar(
                xInterrogacao, 91, new ArrayList<MarcadorTexto>(), elementos, metricas());
        exigir(encontrado != null, "O vínculo semântico deve recuperar a interrogação quando o marcador não for encontrado.");
        exigir(encontrado.editavel && "?".equals(encontrado.valor),
                "O fallback deve produzir a mesma incógnita manipulável.");
    }

    private static void testarUnicidadePorOcorrencia() {
        MarcadorTexto incognita = new MarcadorTexto(
                500, 70, 18, 22, "?", true, "papel.transformacao");
        ItemTextoArrastavel seis = new ItemTextoArrastavel(
                680, 420, 24, 22, "6", false, "6", "papel.estadoFinal", "papel.estadoFinal|6|430|70");
        List<ItemTextoArrastavel> itens = new ArrayList<ItemTextoArrastavel>();
        itens.add(seis);
        PoliticaUnicidadeElementoMatematicoTexto unicidade =
                new PoliticaUnicidadeElementoMatematicoTexto();
        exigir(!unicidade.jaEstaNoDiagrama(incognita, itens),
                "Um número posicionado não pode bloquear a interrogação por valor ou papel.");

        SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();
        ItemTextoArrastavel proxy = sessao.iniciarPorMarcador(incognita);
        proxy.y = 400;
        itens.add(proxy);
        exigir(unicidade.jaEstaNoDiagrama(incognita, itens),
                "A mesma ocorrência textual não pode ser duplicada no diagrama.");
    }

    private static void testarLimiarDeArrasteEstrutural() {
        ControladorLimiarArrasteEstrutural controlador =
                new ControladorLimiarArrasteEstrutural(6);
        controlador.iniciar(100, 100);
        exigir(!controlador.deveMovimentar(103, 102),
                "Oscilação curta de clique não deve mover a figura estrutural.");
        exigir(controlador.deveMovimentar(108, 100),
                "Deslocamento intencional deve iniciar o arraste estrutural.");
        exigir(controlador.foiArraste(), "O controlador deve registrar o gesto como arraste.");
        controlador.finalizar();
    }

    private static void testarDuploCliqueNaoIniciaArraste() {
        PoliticaGestoEstrutural politica = new PoliticaGestoEstrutural();
        exigir(!politica.ehPressionamentoDeDuploClique(1),
                "Clique simples pode preparar o arraste.");
        exigir(politica.ehPressionamentoDeDuploClique(2),
                "O segundo pressionamento do duplo clique deve ser reservado à edição.");
    }

    private static FontMetrics metricas() {
        BufferedImage imagem = new BufferedImage(800, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        g.dispose();
        return fm;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
