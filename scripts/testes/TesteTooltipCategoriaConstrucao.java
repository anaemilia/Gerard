package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public final class TesteTooltipCategoriaConstrucao {
    public static void main(String[] args) throws Exception {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        localizacao.definirIdioma(IdiomaInterface.PORTUGUES);
        confirmar("Construir situação-problema".equals(localizacao.texto("ui.tab.assembly")),
                "o nome da aba em português deve usar Construir");
        confirmar("Construa a situação-problema".equals(localizacao.texto("montagem.title")),
                "o título interno em português deve usar Construa");

        SituacaoProblemaAditiva transformacao = null;
        for (SituacaoProblemaAditiva candidata : CatalogoAtividadesMontagemPadrao.listar(IdiomaInterface.PORTUGUES)) {
            if (candidata.getTipo() == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
                transformacao = candidata;
                break;
            }
        }
        confirmar(transformacao != null, "o catálogo deve conter transformação de medidas");

        GeradorBlocosMontagem gerador = new GeradorBlocosMontagem();
        ConjuntoBlocosMontagem atividade = gerador.gerar(transformacao);
        PainelDiagramaPreenchido painel = new PainelDiagramaPreenchido();
        painel.setSize(540, 370);
        painel.definirSituacao(transformacao, atividade.getValoresDiagrama());

        BufferedImage imagem = new BufferedImage(540, 370, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            painel.paint(g2);
        } finally {
            g2.dispose();
        }

        Field campoLimites = PainelDiagramaPreenchido.class.getDeclaredField("limitesTituloCategoria");
        campoLimites.setAccessible(true);
        Rectangle limites = (Rectangle) campoLimites.get(painel);
        confirmar(limites.width > 0 && limites.height > 0,
                "o nome da categoria deve possuir uma área interativa");

        MouseEvent sobreTitulo = new MouseEvent(painel, MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(), 0, limites.x + limites.width / 2,
                limites.y + limites.height / 2, 0, false);
        String tooltip = painel.getToolTipText(sobreTitulo);
        confirmar(tooltip != null && tooltip.contains("uma medida inicial")
                        && tooltip.contains("transformação") && tooltip.contains("medida final"),
                "o onmouseover deve apresentar a definição localizada da categoria");

        MouseEvent foraTitulo = new MouseEvent(painel, MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(), 0, painel.getWidth() - 5,
                painel.getHeight() - 5, 0, false);
        confirmar(painel.getToolTipText(foraTitulo) == null,
                "a definição não deve aparecer fora do nome da categoria");

        System.out.println("Teste aprovado: nomenclatura Construir e definição da categoria no onmouseover.");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
