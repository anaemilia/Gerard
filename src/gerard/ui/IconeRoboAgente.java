package gerard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Ícone de robô (cabeça com antenas, olhos e "boca" em grade) usado pelos
 * indicadores de atividade dos três agentes da Ajuda Adaptativa — mesmo
 * desenho pra todos, só a cor muda por agente/estado (decisão da usuária,
 * 2026-07-28, com captura de referência de um catálogo de ícones; só a
 * forma foi reaproveitada — as cores continuam as já usadas em cada LED,
 * não as 5 cores do catálogo original).
 */
public final class IconeRoboAgente {
    private IconeRoboAgente() {
    }

    public static void desenhar(Graphics2D g2Original, int x, int y, int tamanho, Color cor) {
        Graphics2D g2 = (Graphics2D) g2Original.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(Math.max(1.2f, tamanho / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int margemTopo = Math.round(tamanho * 0.24f);
            int corpoX = x + Math.round(tamanho * 0.14f);
            int corpoY = y + margemTopo;
            int corpoLargura = Math.round(tamanho * 0.72f);
            int corpoAltura = Math.round(tamanho * 0.56f);
            int arco = Math.max(2, corpoLargura / 4);

            // Antena no topo.
            int antenaX = corpoX + corpoLargura / 2;
            g2.drawLine(antenaX, corpoY, antenaX, corpoY - margemTopo / 2);
            int pontaAntena = Math.max(2, tamanho / 7);
            g2.fillOval(antenaX - pontaAntena / 2, corpoY - margemTopo / 2 - pontaAntena, pontaAntena, pontaAntena);

            // Cabeça.
            g2.drawRoundRect(corpoX, corpoY, corpoLargura, corpoAltura, arco, arco);

            // Orelhas laterais.
            int orelhaDiam = Math.max(2, Math.round(tamanho * 0.16f));
            int orelhaY = corpoY + corpoAltura / 3;
            g2.drawOval(corpoX - orelhaDiam / 2, orelhaY, orelhaDiam, orelhaDiam);
            g2.drawOval(corpoX + corpoLargura - orelhaDiam / 2, orelhaY, orelhaDiam, orelhaDiam);

            // Olhos.
            int olhoDiam = Math.max(2, Math.round(tamanho * 0.11f));
            int olhoY = corpoY + corpoAltura / 4;
            g2.fillOval(corpoX + corpoLargura / 4 - olhoDiam / 2, olhoY, olhoDiam, olhoDiam);
            g2.fillOval(corpoX + corpoLargura * 3 / 4 - olhoDiam / 2, olhoY, olhoDiam, olhoDiam);

            // Boca em grade.
            int bocaLargura = corpoLargura / 2;
            int bocaAltura = Math.max(2, Math.round(tamanho * 0.11f));
            int bocaX = corpoX + (corpoLargura - bocaLargura) / 2;
            int bocaY = corpoY + corpoAltura * 2 / 3;
            g2.drawRoundRect(bocaX, bocaY, bocaLargura, bocaAltura, 2, 2);
            g2.drawLine(bocaX + bocaLargura / 2, bocaY, bocaX + bocaLargura / 2, bocaY + bocaAltura);
        } finally {
            g2.dispose();
        }
    }
}
