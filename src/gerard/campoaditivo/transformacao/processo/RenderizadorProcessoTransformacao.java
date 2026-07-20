package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.i18n.ServicoLocalizacao;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

/** Renderiza estados, canal, funil de entrada ou funil de retirada. */
public final class RenderizadorProcessoTransformacao {
    private final RotulosProcessoTransformacao rotulosProcesso =
            new RotulosProcessoTransformacao();
    private static final Color AZUL = gerard.ui.UITemaGerard.COR_SUCESSO;
    private static final Color FUNDO_ESTADO = gerard.ui.UITemaGerard.COR_SUPERFICIE;
    private static final Color BORDA_ESTADO_NEUTRA = gerard.ui.UITemaGerard.COR_BORDA;
    private static final Color TEXTO_ETAPA_NEUTRA = gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;
    private static final Color RETIRADA = new Color(166, 72, 72);
    private static final Color TEXTO = gerard.ui.UITemaGerard.COR_TEXTO;
    private static final Color TEXTO_SECUNDARIO = gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;

    public void desenharCabecalho(Graphics2D g2, Rectangle area,
            ServicoLocalizacao localizacao) {
        g2.setColor(TEXTO);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(localizacao.texto("ui.transformationBoard.title"),
                area.x + 18, area.y + 27);
        g2.setColor(TEXTO_SECUNDARIO);
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString(localizacao.texto("ui.transformationBoard.description"),
                area.x + 18, area.y + 44);
    }

    public void desenharZona(Graphics2D g2, CirculoVenn zona, int indice,
            EstadoProcessoTransformacao estado,
            ServicoLocalizacao localizacao) {
        desenharZona(g2, zona, indice, estado, localizacao, null);
    }

    public void desenharZona(Graphics2D g2, CirculoVenn zona, int indice,
            EstadoProcessoTransformacao estado,
            ServicoLocalizacao localizacao,
            PlanoUnidadesProcessoTransformacao planoUnidades) {
        if (zona == null) {
            return;
        }
        Stroke anterior = g2.getStroke();
        if (indice != 1) {
            g2.setColor(FUNDO_ESTADO);
            g2.fillRoundRect(zona.x, zona.y, zona.largura, zona.altura, 18, 18);
            g2.setColor(BORDA_ESTADO_NEUTRA);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(zona.x, zona.y, zona.largura, zona.altura, 18, 18);
        }

        Color cor = indice == 1
                ? (estado.getTipoProcesso() == TipoProcessoTransformacao.RETIRADA
                        ? RETIRADA : AZUL)
                : TEXTO_ETAPA_NEUTRA;
        String etapa;
        if (indice == 0) {
            etapa = localizacao.texto("ui.transformationBoard.before");
        } else if (indice == 2) {
            etapa = localizacao.texto("ui.transformationBoard.after");
        } else if (!estado.isConhecido(1)
                || estado.getTipoProcesso() == TipoProcessoTransformacao.NEUTRA) {
            etapa = localizacao.texto("ui.transformationBoard.change");
        } else if (estado.getTipoProcesso()
                == TipoProcessoTransformacao.RETIRADA) {
            etapa = localizacao.texto("ui.transformationBoard.out");
        } else {
            etapa = localizacao.texto("ui.transformationBoard.in");
        }

        if (indice != 1) {
            g2.setColor(cor);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fmEtapa = g2.getFontMetrics();
            g2.drawString(etapa,
                    zona.x + (zona.largura - fmEtapa.stringWidth(etapa)) / 2,
                    zona.y + 21);
        }

        if (estado.isConhecido(indice) && indice != 1) {
            String valorExibido = planoUnidades == null
                    ? estado.formatar(indice)
                    : planoUnidades.getValorFormatado(indice);
            if (valorExibido == null || valorExibido.length() == 0) {
                valorExibido = estado.formatar(indice);
            }
            desenharValor(g2, zona, indice, valorExibido, cor);
        }

        if (indice != 1) {
            String papel = zona.rotulo == null ? "" : zona.rotulo;
            g2.setColor(TEXTO_ETAPA_NEUTRA);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fmPapel = g2.getFontMetrics();
            g2.drawString(papel,
                    zona.x + (zona.largura - fmPapel.stringWidth(papel)) / 2,
                    zona.y + zona.altura - 11);
        }
        g2.setStroke(anterior);
    }

    public void desenharEstrutura(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado) {
        desenharEstrutura(g2, zonas, estado, null);
    }

    public void desenharEstrutura(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado,
            PlanoUnidadesProcessoTransformacao planoUnidades) {
        if (zonas == null || zonas.size() < 3 || estado == null) {
            return;
        }
        Object antialias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke anterior = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GeometriaProcessoTransformacao geo =
                GeometriaProcessoTransformacao.calcular(zonas);
        Rectangle canal = geo.getCanal();

        g2.setColor(FUNDO_ESTADO);
        g2.fillRoundRect(canal.x, canal.y, canal.width, canal.height, 10, 10);
        g2.setColor(BORDA_ESTADO_NEUTRA);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(canal.x, canal.y, canal.x + canal.width, canal.y);
        g2.drawLine(canal.x, canal.y + canal.height,
                canal.x + canal.width, canal.y + canal.height);
        desenharSeta(g2, canal.x + canal.width - 8,
                canal.y + canal.height / 2);

        TipoProcessoTransformacao tipo = estado.getTipoProcesso();
        if (tipo == TipoProcessoTransformacao.INSERCAO) {
            desenharFunil(g2, geo.getFunilInsercao(),
                    geo.getHasteInsercao(), AZUL, true, false);
        } else if (tipo == TipoProcessoTransformacao.RETIRADA) {
            desenharFunil(g2, geo.getFunilRetirada(),
                    geo.getHasteRetirada(), RETIRADA, true, true);
        } else {
            // O funil é parte estrutural da tarefa e já aparece antes de a
            // transformação ser conhecida. A primeira ação + ou - define o
            // sentido sem antecipar quadradinhos nem valores.
            desenharFunil(g2, geo.getFunilInsercao(),
                    geo.getHasteInsercao(), BORDA_ESTADO_NEUTRA, false, false);
        }
        desenharRotulosDoProcesso(g2, zonas.get(1), geo, estado,
                ServicoLocalizacao.getInstancia());
        desenharValorTransformacaoAoLadoDoFunil(g2, geo, estado, planoUnidades);
        desenharLegendaEscala(g2, zonas, geo, planoUnidades);
        g2.setStroke(anterior);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);
    }

    private static final int ESPACO_FUNIL_CONTROLES = 7;
    private static final int TAMANHO_CONTROLE_SINAL = 20;
    private static final int ESPACO_APOS_CONTROLES = 12;

    private void desenharValorTransformacaoAoLadoDoFunil(Graphics2D g2,
            GeometriaProcessoTransformacao geo,
            EstadoProcessoTransformacao estado,
            PlanoUnidadesProcessoTransformacao planoUnidades) {
        if (!estado.isConhecido(1)) {
            return;
        }
        TipoProcessoTransformacao tipo = estado.getTipoProcesso();
        if (tipo != TipoProcessoTransformacao.INSERCAO
                && tipo != TipoProcessoTransformacao.RETIRADA) {
            return;
        }
        String valorExibido = planoUnidades == null
                ? estado.formatar(1)
                : planoUnidades.getValorFormatado(1);
        if (valorExibido == null || valorExibido.length() == 0) {
            valorExibido = estado.formatar(1);
        }
        Color cor = tipo == TipoProcessoTransformacao.RETIRADA ? RETIRADA : AZUL;
        Rectangle funilBounds = (tipo == TipoProcessoTransformacao.RETIRADA
                ? geo.getFunilRetirada() : geo.getFunilInsercao()).getBounds();
        Rectangle hasteBounds = tipo == TipoProcessoTransformacao.RETIRADA
                ? geo.getHasteRetirada() : geo.getHasteInsercao();
        // A coluna de controles +/- (ver ControleSinalProcessoTransformacao)
        // já ocupa a faixa logo à direita do funil/haste. O valor precisa
        // ficar depois dessa coluna, não sobreposto a ela.
        Rectangle ancora = new Rectangle(funilBounds);
        ancora.add(hasteBounds);
        desenharValorAoLadoDoFunil(g2, ancora, valorExibido, cor);
    }

    private void desenharValorAoLadoDoFunil(Graphics2D g2, Rectangle ancora,
            String texto, Color borda) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 16;
        int altura = 26;
        int x = ancora.x + ancora.width + ESPACO_FUNIL_CONTROLES
                + TAMANHO_CONTROLE_SINAL + ESPACO_APOS_CONTROLES;
        int y = ancora.y + (ancora.height - altura) / 2;
        g2.setColor(new Color(0xFC, 0xFB, 0xF8, 232));
        g2.fillRoundRect(x, y, largura, altura, 12, 12);
        g2.setColor(borda);
        g2.drawRoundRect(x, y, largura, altura, 12, 12);
        g2.drawString(texto, x + (largura - fm.stringWidth(texto)) / 2,
                y + (altura - fm.getHeight()) / 2 + fm.getAscent());
    }


    private void desenharLegendaEscala(Graphics2D g2,
            List<CirculoVenn> zonas,
            GeometriaProcessoTransformacao geo,
            PlanoUnidadesProcessoTransformacao plano) {
        if (plano == null || !plano.possuiEscalaAgrupada()) {
            return;
        }
        String legenda = plano.getLegendaEscala();
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        int x = geo.getCanal().x + Math.max(0,
                (geo.getCanal().width - fm.stringWidth(legenda)) / 2);
        int topo = Integer.MAX_VALUE;
        if (zonas != null) {
            for (CirculoVenn zona : zonas) {
                if (zona != null) topo = Math.min(topo, zona.y);
            }
        }
        int y = topo == Integer.MAX_VALUE
                ? geo.getCanal().y - 20 : Math.max(18, topo - 10);
        g2.setColor(TEXTO_SECUNDARIO);
        g2.drawString(legenda, x, y);
    }

    private void desenharRotulosDoProcesso(Graphics2D g2,
            CirculoVenn zonaProcesso, GeometriaProcessoTransformacao geo,
            EstadoProcessoTransformacao estado,
            ServicoLocalizacao localizacao) {
        TipoProcessoTransformacao tipo = estado.getTipoProcesso();
        String etapa = rotulosProcesso.obterEtapa(estado, localizacao);
        Color cor;
        int y;
        if (!estado.isConhecido(1) || tipo == TipoProcessoTransformacao.NEUTRA) {
            cor = AZUL;
            y = geo.getCanal().y - 16;
        } else if (tipo == TipoProcessoTransformacao.RETIRADA) {
            cor = RETIRADA;
            y = geo.getHasteRetirada().y + geo.getHasteRetirada().height + 34;
        } else {
            cor = AZUL;
            y = geo.getFunilInsercao().getBounds().y - 10;
        }
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int centroX = zonaProcesso.x + zonaProcesso.largura / 2;
        if (etapa.length() > 0) {
            g2.setColor(cor);
            g2.drawString(etapa, centroX - fm.stringWidth(etapa) / 2, y);
        }

        String papel = zonaProcesso.rotulo == null ? "Transformação"
                : zonaProcesso.rotulo;
        g2.setColor(TEXTO_SECUNDARIO);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fmPapel = g2.getFontMetrics();
        int yPapel = tipo == TipoProcessoTransformacao.RETIRADA
                ? y + 22 : geo.getCanal().y + geo.getCanal().height + 30;
        g2.drawString(papel, centroX - fmPapel.stringWidth(papel) / 2, yPapel);
    }

    private void desenharValor(Graphics2D g2, CirculoVenn zona, int indice,
            String texto, Color borda) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 16;
        int altura = 26;
        int x = zona.x + (zona.largura - largura) / 2;
        int y = indice == 1 ? zona.y + 8 : zona.y - altura - 8;
        g2.setColor(new Color(0xFC, 0xFB, 0xF8, 232));
        g2.fillRoundRect(x, y, largura, altura, 12, 12);
        g2.setColor(borda);
        g2.drawRoundRect(x, y, largura, altura, 12, 12);
        g2.drawString(texto, x + (largura - fm.stringWidth(texto)) / 2,
                y + (altura - fm.getHeight()) / 2 + fm.getAscent());
    }

    private void desenharFunil(Graphics2D g2, Polygon funil,
            Rectangle haste, Color corBorda, boolean desenharFluxo,
            boolean retirada) {
        g2.setColor(FUNDO_ESTADO);
        g2.fillPolygon(funil);
        g2.fillRoundRect(haste.x, haste.y, haste.width, haste.height, 6, 6);
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(funil);
        g2.drawRoundRect(haste.x, haste.y, haste.width, haste.height, 6, 6);
        if (!desenharFluxo) {
            return;
        }
        int cx = haste.x + haste.width / 2;
        int pontaY = retirada
                ? haste.y + haste.height + 12
                : haste.y + haste.height - 4;
        desenharSetaVertical(g2, cx, pontaY, true);
    }

    private void desenharSeta(Graphics2D g2, int pontaX, int y) {
        int corpoInicio = Math.max(pontaX - 24, 0);
        g2.drawLine(corpoInicio, y, pontaX, y);
        g2.drawLine(pontaX, y, pontaX - 7, y - 5);
        g2.drawLine(pontaX, y, pontaX - 7, y + 5);
    }

    private void desenharSetaVertical(Graphics2D g2, int x, int pontaY,
            boolean paraBaixo) {
        int inicioY = paraBaixo ? pontaY - 20 : pontaY + 20;
        g2.drawLine(x, inicioY, x, pontaY);
        int direcao = paraBaixo ? -1 : 1;
        g2.drawLine(x, pontaY, x - 5, pontaY + 7 * direcao);
        g2.drawLine(x, pontaY, x + 5, pontaY + 7 * direcao);
    }
}
