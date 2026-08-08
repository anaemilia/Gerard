package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.TipoProcessoTransformacao;
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

/**
 * Renderiza os três funis de Composição de Transformações (Transformação 1,
 * Transformação 2, Transformação Final) sobre um único canal — análogo a
 * {@link gerard.campoaditivo.transformacao.processo.RenderizadorProcessoTransformacao},
 * mas sem caixas de estado: todas as três zonas são "processo", cada uma com
 * seu próprio sinal (inserção/retirada) independente das outras duas —
 * diferente da Transformação de Medidas simples, onde há um único
 * TipoProcessoTransformacao compartilhado. Ver
 * RELATORIO_PROCESSO_COMPOSICAO_TRANSFORMACOES_2026-08-07.md.
 */
public final class RenderizadorComposicaoTransformacoesProcesso {
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
        g2.drawString(localizacao.texto("ui.transformationCompositionBoard.title"),
                area.x + 18, area.y + 27);
        g2.setColor(TEXTO_SECUNDARIO);
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString(localizacao.texto("ui.transformationCompositionBoard.description"),
                area.x + 18, area.y + 44);
    }

    public void desenharZona(Graphics2D g2, CirculoVenn zona, int indice,
            EstadoComposicaoTransformacoes estado,
            ServicoLocalizacao localizacao,
            PlanoUnidadesProcessoTransformacao planoUnidades) {
        if (zona == null) {
            return;
        }
        Color cor = estado.getTipoProcesso(indice) == TipoProcessoTransformacao.RETIRADA
                ? RETIRADA : AZUL;

        if (estado.isConhecido(indice)) {
            String valorExibido = planoUnidades == null
                    ? estado.formatar(indice)
                    : planoUnidades.getValorFormatado(indice);
            if (valorExibido == null || valorExibido.length() == 0) {
                valorExibido = estado.formatar(indice);
            }
            desenharValor(g2, zona, valorExibido, cor);
        }

        String papel = zona.rotulo == null ? "" : zona.rotulo;
        g2.setColor(TEXTO_ETAPA_NEUTRA);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fmPapel = g2.getFontMetrics();
        g2.drawString(papel,
                zona.x + (zona.largura - fmPapel.stringWidth(papel)) / 2,
                zona.y + zona.altura - 11);
    }

    public void desenharEstrutura(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado,
            PlanoUnidadesProcessoTransformacao planoUnidades,
            ServicoLocalizacao localizacao) {
        if (zonas == null || zonas.size() < 3 || estado == null) {
            return;
        }
        Object antialias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke anterior = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GeometriaComposicaoTransformacoes geo =
                GeometriaComposicaoTransformacoes.calcular(zonas);
        Rectangle canal = geo.getCanal();

        g2.setColor(FUNDO_ESTADO);
        g2.fillRoundRect(canal.x, canal.y, canal.width, canal.height, 10, 10);
        g2.setColor(BORDA_ESTADO_NEUTRA);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(canal.x, canal.y, canal.x + canal.width, canal.y);
        g2.drawLine(canal.x, canal.y + canal.height,
                canal.x + canal.width, canal.y + canal.height);
        desenharSeta(g2, canal.x + canal.width - 8, canal.y + canal.height / 2);

        for (int i = 0; i < 3; i++) {
            desenharFunilDaZona(g2, geo, estado, i);
            desenharRotulosDoProcesso(g2, zonas.get(i), geo, estado, i, localizacao);
            desenharValorTransformacaoAoLadoDoFunil(g2, geo, estado, i, planoUnidades);
        }
        desenharLegendaEscala(g2, zonas, geo, planoUnidades);
        g2.setStroke(anterior);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);
    }

    private void desenharFunilDaZona(Graphics2D g2,
            GeometriaComposicaoTransformacoes geo,
            EstadoComposicaoTransformacoes estado, int i) {
        TipoProcessoTransformacao tipo = estado.getTipoProcesso(i);
        if (tipo == TipoProcessoTransformacao.INSERCAO) {
            desenharFunil(g2, geo.getFunilInsercao(i), geo.getHasteInsercao(i),
                    AZUL, true, false);
        } else if (tipo == TipoProcessoTransformacao.RETIRADA) {
            desenharFunil(g2, geo.getFunilRetirada(i), geo.getHasteRetirada(i),
                    RETIRADA, true, true);
        } else {
            // O funil é parte estrutural da tarefa e já aparece antes de a
            // transformação ser conhecida, mesmo padrão da Transformação de
            // Medidas: a primeira ação + ou - define o sentido sem
            // antecipar quadradinhos nem valores.
            desenharFunil(g2, geo.getFunilInsercao(i), geo.getHasteInsercao(i),
                    BORDA_ESTADO_NEUTRA, false, false);
        }
    }

    // Mesmos valores de ControleSinalComposicaoTransformacoes (TAMANHO,
    // ESPACO_FUNIL, ESPACO_CONTROLES) — repetidos aqui porque o rótulo do
    // valor precisa alinhar exatamente à mesma coluna x dos controles de
    // sinal, não apenas aproximar a posição.
    private static final int ESPACO_FUNIL_CONTROLES = 7;
    private static final int TAMANHO_CONTROLE_SINAL = 20;
    private static final int ESPACO_ENTRE_CONTROLES = 5;
    private static final int ESPACO_ABAIXO_DOS_CONTROLES = 10;

    private void desenharValorTransformacaoAoLadoDoFunil(Graphics2D g2,
            GeometriaComposicaoTransformacoes geo,
            EstadoComposicaoTransformacoes estado, int i,
            PlanoUnidadesProcessoTransformacao planoUnidades) {
        if (!estado.isConhecido(i)) {
            return;
        }
        TipoProcessoTransformacao tipo = estado.getTipoProcesso(i);
        if (tipo != TipoProcessoTransformacao.INSERCAO
                && tipo != TipoProcessoTransformacao.RETIRADA) {
            return;
        }
        String valorExibido = planoUnidades == null
                ? estado.formatar(i)
                : planoUnidades.getValorFormatado(i);
        if (valorExibido == null || valorExibido.length() == 0) {
            valorExibido = estado.formatar(i);
        }
        Color cor = tipo == TipoProcessoTransformacao.RETIRADA ? RETIRADA : AZUL;
        Rectangle funilBounds = (tipo == TipoProcessoTransformacao.RETIRADA
                ? geo.getFunilRetirada(i) : geo.getFunilInsercao(i)).getBounds();
        Rectangle hasteBounds = tipo == TipoProcessoTransformacao.RETIRADA
                ? geo.getHasteRetirada(i) : geo.getHasteInsercao(i);
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
        // Mesma coluna x dos controles de sinal (+/-) — ver
        // ControleSinalComposicaoTransformacoes.obterAreas: os círculos
        // começam em "ancora.x + ancora.width + ESPACO_FUNIL" (mesmo valor
        // de ESPACO_FUNIL_CONTROLES aqui). O rótulo fica abaixo dos dois
        // círculos, não numa terceira coluna à direita — a pedido da
        // usuária, 2026-08-08 — evitando também que a caixa do último
        // funil (Transformação Final) seja cortada pela borda do painel
        // quando os três funis dividem um canal estreito.
        int x = ancora.x + ancora.width + ESPACO_FUNIL_CONTROLES;
        int y = ancora.y + 2 + TAMANHO_CONTROLE_SINAL + ESPACO_ENTRE_CONTROLES
                + TAMANHO_CONTROLE_SINAL + ESPACO_ABAIXO_DOS_CONTROLES;
        g2.setColor(new Color(0xFC, 0xFB, 0xF8, 232));
        g2.fillRoundRect(x, y, largura, altura, 12, 12);
        g2.setColor(borda);
        g2.drawRoundRect(x, y, largura, altura, 12, 12);
        g2.drawString(texto, x + (largura - fm.stringWidth(texto)) / 2,
                y + (altura - fm.getHeight()) / 2 + fm.getAscent());
    }

    private void desenharLegendaEscala(Graphics2D g2, List<CirculoVenn> zonas,
            GeometriaComposicaoTransformacoes geo,
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
            CirculoVenn zonaProcesso, GeometriaComposicaoTransformacoes geo,
            EstadoComposicaoTransformacoes estado, int i,
            ServicoLocalizacao localizacao) {
        ServicoLocalizacao loc = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        TipoProcessoTransformacao tipo = estado.getTipoProcesso(i);
        String etapa = !estado.isConhecido(i) || tipo == TipoProcessoTransformacao.NEUTRA
                ? "" : (tipo == TipoProcessoTransformacao.RETIRADA
                        ? loc.texto("ui.transformationBoard.out")
                        : loc.texto("ui.transformationBoard.in"));
        Color cor;
        int y;
        if (!estado.isConhecido(i) || tipo == TipoProcessoTransformacao.NEUTRA) {
            cor = AZUL;
            y = geo.getCanal().y - 16;
        } else if (tipo == TipoProcessoTransformacao.RETIRADA) {
            cor = RETIRADA;
            y = geo.getHasteRetirada(i).y + geo.getHasteRetirada(i).height + 34;
        } else {
            cor = AZUL;
            y = geo.getFunilInsercao(i).getBounds().y - 10;
        }
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int centroX = zonaProcesso.x + zonaProcesso.largura / 2;
        if (etapa.length() > 0) {
            g2.setColor(cor);
            g2.drawString(etapa, centroX - fm.stringWidth(etapa) / 2, y);
        }
        // O rótulo do papel (Transformação 1/2/Final) já é desenhado por
        // desenharZona, junto à base do próprio funil — repeti-lo aqui,
        // abaixo do canal, era redundante e ficava visualmente distante
        // do funil a que pertence (removido em 2026-08-07, a pedido da
        // usuária).
    }

    private void desenharValor(Graphics2D g2, CirculoVenn zona, String texto,
            Color borda) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 16;
        int altura = 26;
        int x = zona.x + (zona.largura - largura) / 2;
        int y = zona.y + 8;
        g2.setColor(new Color(0xFC, 0xFB, 0xF8, 232));
        g2.fillRoundRect(x, y, largura, altura, 12, 12);
        g2.setColor(borda);
        g2.drawRoundRect(x, y, largura, altura, 12, 12);
        g2.drawString(texto, x + (largura - fm.stringWidth(texto)) / 2,
                y + (altura - fm.getHeight()) / 2 + fm.getAscent());
    }

    private void desenharFunil(Graphics2D g2, Polygon funil, Rectangle haste,
            Color corBorda, boolean desenharFluxo, boolean retirada) {
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
        int pontaY = retirada ? haste.y + haste.height + 12 : haste.y + haste.height - 4;
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
