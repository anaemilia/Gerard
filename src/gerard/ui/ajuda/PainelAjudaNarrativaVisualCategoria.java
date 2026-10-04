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
    private final RotuloAnimacaoComLegendas imagem;
    private final JLabel indicador;
    private final JButton anterior;
    private final JButton proxima;
    private int indiceAtual;

    private PainelAjudaNarrativaVisualCategoria(
            String chaveRepertorio,
            FormatoAjudaNarrativaVisual formato,
            List<ImageIcon> historias,
            Dimension tamanhoApresentacao) {
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

        imagem = new RotuloAnimacaoComLegendas(historias.get(0));
        imagem.setPreferredSize(tamanhoApresentacao);
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
        return criarSeDisponivel(repertorio, formato, TAMANHO_APRESENTACAO);
    }

    public static PainelAjudaNarrativaVisualCategoria criarSeDisponivel(
            RepertorioAjudaVisual repertorio,
            FormatoAjudaNarrativaVisual formato,
            Dimension tamanhoApresentacao) {
        return criarSeDisponivel(repertorio, formato, tamanhoApresentacao, true);
    }

    public static PainelAjudaNarrativaVisualCategoria criarPassivoSeDisponivel(
            RepertorioAjudaVisual repertorio,
            FormatoAjudaNarrativaVisual formato,
            Dimension tamanhoApresentacao) {
        return criarSeDisponivel(repertorio, formato, tamanhoApresentacao, false);
    }

    /**
     * Cria o painel a partir da ajuda visual projetada pela API (a mesma que o React
     * consome): cada item traz {@code identificador} e {@code referencia}. O painel só
     * resolve as referências em imagens; não escolhe entre narrativas.
     */
    public static PainelAjudaNarrativaVisualCategoria criarPassivoDaProjecao(
            java.util.List<Object> ajudaVisual,
            FormatoAjudaNarrativaVisual formato,
            Dimension tamanhoApresentacao) {
        if (ajudaVisual == null || ajudaVisual.isEmpty()) {
            return null;
        }
        List<HistorinhaAjudaVisual> historinhas = new ArrayList<HistorinhaAjudaVisual>();
        for (Object item : ajudaVisual) {
            java.util.Map<?, ?> mapa = (java.util.Map<?, ?>) item;
            historinhas.add(new HistorinhaAjudaVisual(
                    String.valueOf(mapa.get("identificador")), "projecao",
                    String.valueOf(mapa.get("referencia"))));
        }
        PainelAjudaNarrativaVisualCategoria painel = criarSeDisponivel(
                RepertorioAjudaVisual.criar("projecao:" + historinhas.get(0).getIdentificador(),
                        historinhas.toArray(new HistorinhaAjudaVisual[0])),
                formato, tamanhoApresentacao, false);
        if (painel != null) {
            // As legendas ficam DENTRO da animação, com o cronograma que a API projetou.
            painel.imagem.definirCronograma((java.util.Map<?, ?>) ajudaVisual.get(0));
        }
        return painel;
    }

    private static PainelAjudaNarrativaVisualCategoria criarSeDisponivel(
            RepertorioAjudaVisual repertorio,
            FormatoAjudaNarrativaVisual formato,
            Dimension tamanhoApresentacao,
            boolean navegacaoVisivel) {
        if (repertorio == null || repertorio.estaVazio() || formato == null) {
            return null;
        }
        Dimension limite = tamanhoApresentacao == null
                ? TAMANHO_APRESENTACAO : new Dimension(
                        Math.max(1, tamanhoApresentacao.width),
                        Math.max(1, tamanhoApresentacao.height));
        List<ImageIcon> icones = new ArrayList<ImageIcon>();
        for (HistorinhaAjudaVisual historinha : repertorio.getHistorinhas()) {
            URL recurso = PainelAjudaNarrativaVisualCategoria.class.getResource(
                    caminhoRecurso(historinha, formato));
            if (recurso == null) {
                return null;
            }
            ImageIcon icone = new ImageIcon(recurso);
            icones.add(reduzirParaApresentacao(icone, limite));
        }
        PainelAjudaNarrativaVisualCategoria painel =
                new PainelAjudaNarrativaVisualCategoria(
                        repertorio.getChave(), formato, icones, limite);
        painel.anterior.setVisible(navegacaoVisivel);
        painel.proxima.setVisible(navegacaoVisivel);
        painel.indicador.setVisible(navegacaoVisivel);
        return painel;
    }

    private static String caminhoRecurso(
            HistorinhaAjudaVisual historinha,
            FormatoAjudaNarrativaVisual formato) {
        String sufixo = formato == FormatoAjudaNarrativaVisual.ANIMACAO
                ? ".gif" : "_storyboard.png";
        return RAIZ_RECURSOS + historinha.getReferenciaConteudo() + sufixo;
    }

    private static ImageIcon reduzirParaApresentacao(
            ImageIcon original,
            Dimension limite) {
        int larguraOriginal = original.getIconWidth();
        int alturaOriginal = original.getIconHeight();
        if (larguraOriginal <= limite.width
                && alturaOriginal <= limite.height) {
            return original;
        }
        double escala = Math.min(
                (double) limite.width / larguraOriginal,
                (double) limite.height / alturaOriginal);
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
