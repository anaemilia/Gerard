package gerard.ui.ajuda;

import gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual;
import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;
import gerard.ui.UITemaGerard;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Materializa em Swing as narrativas do repertório local de cada categoria.
 * A categoria fornece identidades de conteúdo; este adaptador resolve GIFs
 * ou storyboards conforme o formato solicitado.
 */
public final class PainelAjudaNarrativaVisualCategoria extends JPanel {

    private static final String RAIZ_RECURSOS = "/gerard/recursos/ajuda/";
    private static final Dimension TAMANHO_APRESENTACAO = new Dimension(640, 360);

    private final String chaveRepertorio;
    private final FormatoAjudaNarrativaVisual formato;
    private final List<ImageIcon> historias;
    private final JLabel imagem;
    private final JLabel indicador;
    private final JButton anterior;
    private final JButton proxima;
    private int indiceAtual;

    private PainelAjudaNarrativaVisualCategoria(
            String chaveRepertorio,
            FormatoAjudaNarrativaVisual formato,
            List<ImageIcon> historias) {
        super(new BorderLayout(0, 8));
        this.chaveRepertorio = chaveRepertorio;
        this.formato = formato;
        this.historias = historias;
        this.indiceAtual = 0;
        setOpaque(true);
        setBackground(UITemaGerard.COR_SUPERFICIE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITemaGerard.COR_BORDA),
                BorderFactory.createEmptyBorder(8, 8, 6, 8)));
        setAlignmentX(LEFT_ALIGNMENT);

        imagem = new JLabel(historias.get(0));
        imagem.setHorizontalAlignment(SwingConstants.CENTER);
        imagem.setPreferredSize(TAMANHO_APRESENTACAO);
        add(imagem, BorderLayout.CENTER);

        anterior = criarBotaoNavegacao("‹");
        proxima = criarBotaoNavegacao("›");
        indicador = new JLabel();
        indicador.setFont(new Font("Arial", Font.PLAIN, 13));
        indicador.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);

        anterior.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exibirAnterior();
            }
        });
        proxima.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exibirProxima();
            }
        });

        JPanel navegacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        navegacao.setOpaque(false);
        navegacao.add(anterior);
        navegacao.add(indicador);
        navegacao.add(proxima);
        add(navegacao, BorderLayout.SOUTH);
        atualizarExibicao();
    }

    public static PainelAjudaNarrativaVisualCategoria criarSeDisponivel(
            RepertorioAjudaVisual repertorio,
            FormatoAjudaNarrativaVisual formato) {
        if (repertorio == null || repertorio.estaVazio() || formato == null) {
            return null;
        }
        List<ImageIcon> icones = new ArrayList<ImageIcon>();
        for (HistorinhaAjudaVisual historinha : repertorio.getHistorinhas()) {
            URL recurso = PainelAjudaNarrativaVisualCategoria.class.getResource(
                    caminhoRecurso(historinha, formato));
            if (recurso == null) {
                return null;
            }
            ImageIcon icone = new ImageIcon(recurso);
            icones.add(formato == FormatoAjudaNarrativaVisual.HISTORIA_EM_QUADRINHOS
                    ? reduzirParaApresentacao(icone) : icone);
        }
        return new PainelAjudaNarrativaVisualCategoria(repertorio.getChave(), formato, icones);
    }

    private static String caminhoRecurso(
            HistorinhaAjudaVisual historinha,
            FormatoAjudaNarrativaVisual formato) {
        String sufixo = formato == FormatoAjudaNarrativaVisual.ANIMACAO
                ? ".gif" : "_storyboard.png";
        return RAIZ_RECURSOS + historinha.getReferenciaConteudo() + sufixo;
    }

    private static ImageIcon reduzirParaApresentacao(ImageIcon original) {
        int larguraOriginal = original.getIconWidth();
        int alturaOriginal = original.getIconHeight();
        if (larguraOriginal <= TAMANHO_APRESENTACAO.width
                && alturaOriginal <= TAMANHO_APRESENTACAO.height) {
            return original;
        }
        double escala = Math.min(
                (double) TAMANHO_APRESENTACAO.width / larguraOriginal,
                (double) TAMANHO_APRESENTACAO.height / alturaOriginal);
        int largura = Math.max(1, (int) Math.round(larguraOriginal * escala));
        int altura = Math.max(1, (int) Math.round(alturaOriginal * escala));
        Image reduzida = original.getImage().getScaledInstance(
                largura, altura, Image.SCALE_SMOOTH);
        return new ImageIcon(reduzida);
    }

    public String getChaveRepertorio() {
        return chaveRepertorio;
    }

    public FormatoAjudaNarrativaVisual getFormato() {
        return formato;
    }

    public int getQuantidadeHistorias() {
        return historias.size();
    }

    public int getIndiceAtual() {
        return indiceAtual;
    }

    public void exibirProxima() {
        if (indiceAtual < historias.size() - 1) {
            indiceAtual++;
            atualizarExibicao();
        }
    }

    public void exibirAnterior() {
        if (indiceAtual > 0) {
            indiceAtual--;
            atualizarExibicao();
        }
    }

    private JButton criarBotaoNavegacao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 20));
        botao.setForeground(UITemaGerard.COR_TEXTO);
        botao.setBackground(UITemaGerard.COR_SUPERFICIE_SUAVE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITemaGerard.COR_BORDA),
                BorderFactory.createEmptyBorder(1, 12, 2, 12)));
        return botao;
    }

    private void atualizarExibicao() {
        imagem.setIcon(historias.get(indiceAtual));
        indicador.setText((indiceAtual + 1) + " / " + historias.size());
        anterior.setEnabled(indiceAtual > 0);
        proxima.setEnabled(indiceAtual < historias.size() - 1);
    }
}
