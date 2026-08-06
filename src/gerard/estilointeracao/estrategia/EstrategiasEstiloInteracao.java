package gerard.estilointeracao.estrategia;

import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import gerard.estilointeracao.EstiloInteracao;
import gerard.ui.UITemaGerard;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

/** Registro único das estratégias de interação. */
public final class EstrategiasEstiloInteracao {
    private final Map<EstiloInteracao, EstrategiaEstiloInteracao> estrategias =
            new EnumMap<EstiloInteracao, EstrategiaEstiloInteracao>(EstiloInteracao.class);

    public EstrategiasEstiloInteracao() {
        estrategias.put(EstiloInteracao.PROXIMIDADE,
                simples(EstadoRealceAlvo.PROXIMIDADE, false, true));
        estrategias.put(EstiloInteracao.DROP_TARGET_HIGHLIGHTING,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return dentro ? EstadoRealceAlvo.DROP_TARGET : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }

                    public void desenhar(Graphics2D g2,
                            int itemX, int itemY, int itemLargura, int itemAltura,
                            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
                            boolean proximo, boolean dentro) {
                        if (!dentro) {
                            return;
                        }
                        Composite compostoOriginal = g2.getComposite();
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
                        g2.setColor(UITemaGerard.COR_PRIMARIA);
                        g2.fillRoundRect(alvoX + 2, alvoY + 2, alvoLargura - 4, alvoAltura - 4, 10, 10);
                        g2.setComposite(compostoOriginal);
                        g2.setStroke(new BasicStroke(2.4f));
                        g2.setColor(UITemaGerard.COR_PRIMARIA);
                        int px = alvoX + Math.max(5, (alvoLargura - itemLargura) / 2);
                        int py = alvoY + Math.max(5, (alvoAltura - itemAltura) / 2);
                        g2.drawRoundRect(px, py, Math.max(4, Math.min(itemLargura, alvoLargura - 10)),
                                Math.max(4, Math.min(itemAltura, alvoAltura - 10)), 8, 8);
                    }
                });
        estrategias.put(EstiloInteracao.DRAG_OVER_FEEDBACK,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return (proximo || dentro) ? EstadoRealceAlvo.DRAG_OVER : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }

                    public void desenhar(Graphics2D g2,
                            int itemX, int itemY, int itemLargura, int itemAltura,
                            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
                            boolean proximo, boolean dentro) {
                        if (!(proximo || dentro)) {
                            return;
                        }
                        int itemCx = itemX + itemLargura / 2;
                        int itemCy = itemY + itemAltura / 2;
                        int alvoCx = alvoX + alvoLargura / 2;
                        int alvoCy = alvoY + alvoAltura / 2;
                        g2.setStroke(new BasicStroke(dentro ? 3.0f : 2.0f,
                                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(UITemaGerard.COR_PRIMARIA);
                        g2.drawLine(itemCx, itemCy, alvoCx, alvoCy);
                        g2.drawRoundRect(alvoX - (dentro ? 5 : 3), alvoY - (dentro ? 5 : 3),
                                alvoLargura + (dentro ? 10 : 6), alvoAltura + (dentro ? 10 : 6), 10, 10);
                        g2.fillOval(alvoCx - (dentro ? 5 : 3), alvoCy - (dentro ? 5 : 3),
                                dentro ? 10 : 6, dentro ? 10 : 6);
                    }
                });
        estrategias.put(EstiloInteracao.AFFORDANCE,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return proximo ? EstadoRealceAlvo.DRAG_OVER : EstadoRealceAlvo.AFFORDANCE;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }

                    public void desenhar(Graphics2D g2,
                            int itemX, int itemY, int itemLargura, int itemAltura,
                            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
                            boolean proximo, boolean dentro) {
                        // Indica disponibilidade por cantos de enquadramento. Evita o
                        // sinal "+", que pode ser confundido com o sinal positivo da
                        // representação formal.
                        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(UITemaGerard.COR_PRIMARIA);
                        desenharCantosDeEnquadramento(g2, alvoX, alvoY, alvoLargura, alvoAltura);
                    }
                });
        estrategias.put(EstiloInteracao.SNAP_TO_TARGET,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return proximo ? EstadoRealceAlvo.SNAP : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return proximo; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return proximo; }

                    public void desenhar(Graphics2D g2,
                            int itemX, int itemY, int itemLargura, int itemAltura,
                            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
                            boolean proximo, boolean dentro) {
                        if (!proximo) {
                            return;
                        }
                        int itemCx = itemX + itemLargura / 2;
                        int itemCy = itemY + itemAltura / 2;
                        int alvoCx = alvoX + alvoLargura / 2;
                        int alvoCy = alvoY + alvoAltura / 2;
                        g2.setStroke(new BasicStroke(2.0f));
                        g2.setColor(UITemaGerard.COR_PRIMARIA);
                        desenharSetaCurta(g2, itemCx, itemCy, alvoCx, alvoCy);
                    }
                });
    }

    public EstrategiaEstiloInteracao obter(EstiloInteracao estilo) {
        EstrategiaEstiloInteracao estrategia = estrategias.get(estilo);
        return estrategia != null ? estrategia : simples(EstadoRealceAlvo.NENHUM, false, false);
    }

    private EstrategiaEstiloInteracao simples(final EstadoRealceAlvo estado,
            final boolean magnetico, final boolean desenhaLinhaTracejada) {
        return new EstrategiaEstiloInteracao() {
            public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                return proximo ? estado : EstadoRealceAlvo.NENHUM;
            }
            public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return magnetico && proximo; }
            public boolean deveCentralizarAoSoltar(boolean proximo) { return magnetico && proximo; }

            public void desenhar(Graphics2D g2,
                    int itemX, int itemY, int itemLargura, int itemAltura,
                    int alvoX, int alvoY, int alvoLargura, int alvoAltura,
                    boolean proximo, boolean dentro) {
                if (!desenhaLinhaTracejada || !proximo) {
                    return;
                }
                int itemCx = itemX + itemLargura / 2;
                int itemCy = itemY + itemAltura / 2;
                int alvoCx = alvoX + alvoLargura / 2;
                int alvoCy = alvoY + alvoAltura / 2;
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[]{5.0f, 5.0f}, 0.0f));
                g2.setColor(UITemaGerard.COR_PRIMARIA);
                g2.drawLine(itemCx, itemCy, alvoCx, alvoCy);
                g2.drawRoundRect(alvoX - 4, alvoY - 4, alvoLargura + 8, alvoAltura + 8, 10, 10);
            }
        };
    }

    private static void desenharCantosDeEnquadramento(Graphics2D g2,
            int alvoX, int alvoY, int alvoLargura, int alvoAltura) {
        int margem = 5;
        int comprimento = Math.max(6, Math.min(10, Math.min(alvoLargura, alvoAltura) / 4));
        int esquerda = alvoX + margem;
        int direita = alvoX + alvoLargura - margem;
        int topo = alvoY + margem;
        int base = alvoY + alvoAltura - margem;

        // Canto superior esquerdo.
        g2.drawLine(esquerda, topo, esquerda + comprimento, topo);
        g2.drawLine(esquerda, topo, esquerda, topo + comprimento);
        // Canto superior direito.
        g2.drawLine(direita - comprimento, topo, direita, topo);
        g2.drawLine(direita, topo, direita, topo + comprimento);
        // Canto inferior esquerdo.
        g2.drawLine(esquerda, base, esquerda + comprimento, base);
        g2.drawLine(esquerda, base - comprimento, esquerda, base);
        // Canto inferior direito.
        g2.drawLine(direita - comprimento, base, direita, base);
        g2.drawLine(direita, base - comprimento, direita, base);
    }

    private static void desenharSetaCurta(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int tamanho = 9;
        int ax1 = x2 - (int) Math.round(tamanho * Math.cos(angulo - Math.PI / 6));
        int ay1 = y2 - (int) Math.round(tamanho * Math.sin(angulo - Math.PI / 6));
        int ax2 = x2 - (int) Math.round(tamanho * Math.cos(angulo + Math.PI / 6));
        int ay2 = y2 - (int) Math.round(tamanho * Math.sin(angulo + Math.PI / 6));
        g2.drawLine(x2, y2, ax1, ay1);
        g2.drawLine(x2, y2, ax2, ay2);
    }
}
