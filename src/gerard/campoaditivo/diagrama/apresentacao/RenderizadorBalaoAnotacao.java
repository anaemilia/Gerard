package gerard.campoaditivo.diagrama.apresentacao;

import gerard.campoaditivo.diagrama.elementos.FragmentoAnotacao;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

/**
 * Desenha o balão da anotação de mouse-over. Só mede e pinta: a mensagem, a âncora e a
 * decisão de aparecer vêm de fora; nada aqui lê ou altera o estado da tela.
 */
public class RenderizadorBalaoAnotacao {
    private final Color corFundo;
    private final Color corBorda;
    private final Color corTexto;

    public RenderizadorBalaoAnotacao(Color corFundo, Color corBorda, Color corTexto) {
        this.corFundo = corFundo;
        this.corBorda = corBorda;
        this.corTexto = corTexto;
    }

    public void desenhar(
            Graphics2D g2,
            String mensagem,
            int baseX,
            int baseY,
            int larguraPainel,
            int alturaPainel,
            boolean forcarAbaixo
    ) {
        Font fonteAnotacao = new Font("Arial", Font.PLAIN, 13);
        Font fonteAnotacaoNegrito = fonteAnotacao.deriveFont(Font.BOLD);
        g2.setFont(fonteAnotacao);
        FontMetrics fm = g2.getFontMetrics(fonteAnotacao);

        int larguraMaxima = Math.min(430, Math.max(240, larguraPainel - 40));
        List<List<FragmentoAnotacao>> linhas = quebrarTextoFormatado(
                mensagem, fonteAnotacao, fonteAnotacaoNegrito, larguraMaxima - 18, g2);

        int larguraTexto = 0;
        for (List<FragmentoAnotacao> linha : linhas) {
            larguraTexto = Math.max(larguraTexto, medirLinha(linha, fonteAnotacao, fonteAnotacaoNegrito, g2));
        }

        int largura = larguraTexto + 18;
        int altura = linhas.size() * fm.getHeight() + 10;

        int x = baseX + 14;
        int y = forcarAbaixo ? (baseY + 18) : (baseY - altura - 10);

        if (x + largura > larguraPainel - 8) {
            x = larguraPainel - largura - 8;
        }

        if (x < 8) {
            x = 8;
        }

        if (!forcarAbaixo && y < 50) {
            y = baseY + 18;
        }

        if (y + altura > alturaPainel - 8) {
            y = alturaPainel - altura - 8;
        }

        Composite originalComposite = g2.getComposite();
        Stroke originalStroke = g2.getStroke();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.97f));
        g2.setColor(corFundo);
        g2.fillRoundRect(x, y, largura, altura, 12, 12);

        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(x, y, largura, altura, 10, 10);

        g2.setColor(corTexto);
        int yLinha = y + fm.getAscent() + 5;
        for (List<FragmentoAnotacao> linha : linhas) {
            desenharLinha(g2, linha, x + 9, yLinha, fonteAnotacao, fonteAnotacaoNegrito);
            yLinha += fm.getHeight();
        }

        g2.setComposite(originalComposite);
        g2.setStroke(originalStroke);
    }

    private List<List<FragmentoAnotacao>> quebrarTextoFormatado(
            String texto,
            Font fonteNormal,
            Font fonteNegrito,
            int larguraMaxima,
            Graphics2D g2
    ) {
        List<FragmentoAnotacao> tokens = tokenizar(texto);
        List<List<FragmentoAnotacao>> linhas = new ArrayList<List<FragmentoAnotacao>>();
        List<FragmentoAnotacao> linhaAtual = new ArrayList<FragmentoAnotacao>();
        int larguraAtual = 0;

        for (FragmentoAnotacao tokenOriginal : tokens) {
            if (tokenOriginal == null || tokenOriginal.texto.length() == 0) {
                continue;
            }

            FragmentoAnotacao token = tokenOriginal;
            int larguraToken = medirFragmento(token, fonteNormal, fonteNegrito, g2);

            if (!linhaAtual.isEmpty() && larguraAtual + larguraToken > larguraMaxima) {
                linhas.add(linhaAtual);
                linhaAtual = new ArrayList<FragmentoAnotacao>();
                larguraAtual = 0;
                token = new FragmentoAnotacao(removerEspacoInicial(token.texto), token.negrito);
                larguraToken = medirFragmento(token, fonteNormal, fonteNegrito, g2);
            }

            linhaAtual.add(token);
            larguraAtual += larguraToken;
        }

        if (!linhaAtual.isEmpty()) {
            linhas.add(linhaAtual);
        }

        if (linhas.isEmpty()) {
            List<FragmentoAnotacao> linhaVazia = new ArrayList<FragmentoAnotacao>();
            linhaVazia.add(new FragmentoAnotacao("", false));
            linhas.add(linhaVazia);
        }

        return linhas;
    }

    private List<FragmentoAnotacao> tokenizar(String texto) {
        List<FragmentoAnotacao> fragmentos = extrairFragmentos(texto);
        List<FragmentoAnotacao> tokens = new ArrayList<FragmentoAnotacao>();

        for (FragmentoAnotacao fragmento : fragmentos) {
            if (fragmento == null || fragmento.texto == null) {
                continue;
            }

            String textoFragmento = fragmento.texto.replace('\n', ' ');
            String[] palavras = textoFragmento.trim().length() == 0 ? new String[0] : textoFragmento.trim().split("\\s+");

            for (String palavra : palavras) {
                if (palavra.length() == 0) {
                    continue;
                }
                boolean pontuacaoSemEspaco = palavra.matches("^[,.;:!?)]$");
                boolean precisaEspaco = !tokens.isEmpty() && !pontuacaoSemEspaco;
                String textoToken = (precisaEspaco ? " " : "") + palavra;
                tokens.add(new FragmentoAnotacao(textoToken, fragmento.negrito));
            }
        }

        return tokens;
    }

    private List<FragmentoAnotacao> extrairFragmentos(String texto) {
        List<FragmentoAnotacao> fragmentos = new ArrayList<FragmentoAnotacao>();
        String origem = texto == null ? "" : texto;
        int posicao = 0;

        while (posicao < origem.length()) {
            int inicioNegrito = origem.indexOf("<b>", posicao);
            if (inicioNegrito < 0) {
                fragmentos.add(new FragmentoAnotacao(origem.substring(posicao), false));
                break;
            }

            if (inicioNegrito > posicao) {
                fragmentos.add(new FragmentoAnotacao(origem.substring(posicao, inicioNegrito), false));
            }

            int fimNegrito = origem.indexOf("</b>", inicioNegrito + 3);
            if (fimNegrito < 0) {
                fragmentos.add(new FragmentoAnotacao(origem.substring(inicioNegrito + 3), true));
                break;
            }

            fragmentos.add(new FragmentoAnotacao(origem.substring(inicioNegrito + 3, fimNegrito), true));
            posicao = fimNegrito + 4;
        }

        if (fragmentos.isEmpty()) {
            fragmentos.add(new FragmentoAnotacao(origem, false));
        }

        return fragmentos;
    }

    private String removerEspacoInicial(String texto) {
        if (texto == null) {
            return "";
        }
        int indice = 0;
        while (indice < texto.length() && Character.isWhitespace(texto.charAt(indice))) {
            indice++;
        }
        return texto.substring(indice);
    }

    private int medirLinha(List<FragmentoAnotacao> linha, Font fonteNormal, Font fonteNegrito, Graphics2D g2) {
        int largura = 0;
        for (FragmentoAnotacao fragmento : linha) {
            largura += medirFragmento(fragmento, fonteNormal, fonteNegrito, g2);
        }
        return largura;
    }

    private int medirFragmento(FragmentoAnotacao fragmento, Font fonteNormal, Font fonteNegrito, Graphics2D g2) {
        if (fragmento == null || fragmento.texto == null || fragmento.texto.length() == 0) {
            return 0;
        }
        Font fonte = fragmento.negrito ? fonteNegrito : fonteNormal;
        return g2.getFontMetrics(fonte).stringWidth(fragmento.texto);
    }

    private void desenharLinha(
            Graphics2D g2,
            List<FragmentoAnotacao> linha,
            int x,
            int y,
            Font fonteNormal,
            Font fonteNegrito
    ) {
        int xAtual = x;
        for (FragmentoAnotacao fragmento : linha) {
            if (fragmento == null || fragmento.texto == null) {
                continue;
            }
            Font fonte = fragmento.negrito ? fonteNegrito : fonteNormal;
            g2.setFont(fonte);
            g2.drawString(fragmento.texto, xAtual, y);
            xAtual += g2.getFontMetrics(fonte).stringWidth(fragmento.texto);
        }
        g2.setFont(fonteNormal);
    }
}
