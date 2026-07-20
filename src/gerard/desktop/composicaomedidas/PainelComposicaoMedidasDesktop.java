package gerard.desktop.composicaomedidas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Tela de composição de medidas — versão desktop, visão do usuário comum.
 * Implementa exatamente o comportamento descrito nas instruções: arraste
 * cria cópia, peças seguem arrastáveis a qualquer momento, erro dispara
 * tremor + som apenas ao soltar, incógnita vira campo com confirmação,
 * sucesso pinta o diagrama de azul após uma pausa e mostra "Próxima
 * tarefa" discretamente, desfazendo tudo se uma peça for movida depois.
 */
public class PainelComposicaoMedidasDesktop extends JPanel {

    private static final int LARGURA_CAIXA = 90;
    private static final int ALTURA_CAIXA = 90;
    private static final int RAIO_CAIXA = 16;
    private static final int LARGURA_CHIP = 46;
    private static final int ALTURA_CHIP = 40;

    private final List<SituacaoComposicaoMedidas> situacoes;
    private int indiceSituacaoAtual;
    private SituacaoComposicaoMedidas situacao;

    private final List<TokenVisual> tokensEnunciado = new ArrayList<TokenVisual>();
    private final Rectangle areaEnunciado = new Rectangle(40, 30, 620, 160);
    private final Rectangle areaDiagrama = new Rectangle(40, 220, 620, 320);

    private CaixaDiagrama caixaParte1;
    private CaixaDiagrama caixaParte2;
    private CaixaDiagrama caixaTodo;

    // --- estado de arraste ---
    private boolean arrastando;
    private Integer valorArrastado;
    private boolean incognitaArrastada;
    private CaixaDiagrama caixaOrigemArraste; // null quando a origem é o texto (cria cópia)
    private Point posicaoMouseAtual = new Point();
    private CaixaDiagrama caixaEmHover;

    // --- confirmação da incógnita ---
    private final JTextField campoIncognita = new JTextField();
    private final JButton botaoConfirmar = new JButton("Confirmar");

    // --- sucesso ---
    private boolean todasCorretas;
    private boolean sucessoExibido;
    private float progressoSucesso; // 0..1, para a pintura gradual de azul
    private boolean mostrarProximaTarefa;
    private Timer temporizadorEsperaSucesso;
    private Timer animadorSucesso;

    // --- tremor ---
    private CaixaDiagrama caixaTremendo;
    private int quadroTremor;
    private Timer temporizadorTremor;

    public PainelComposicaoMedidasDesktop() {
        setBackground(TemaComposicaoMedidas.FUNDO_CONTEUDO);
        setLayout(null);
        setPreferredSize(new java.awt.Dimension(700, 620));

        situacoes = CarregadorSituacoesComposicaoMedidas.carregar();
        indiceSituacaoAtual = 0;
        carregarSituacaoAtual();

        campoIncognita.setFont(TemaComposicaoMedidas.FONTE_NUMERO);
        campoIncognita.setHorizontalAlignment(JTextField.CENTER);
        campoIncognita.setVisible(false);
        botaoConfirmar.setFont(TemaComposicaoMedidas.FONTE_ROTULO);
        botaoConfirmar.setVisible(false);
        botaoConfirmar.setFocusPainted(false);
        add(campoIncognita);
        add(botaoConfirmar);
        botaoConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarIncognita();
            }
        });
        campoIncognita.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarIncognita();
            }
        });

        MouseAdapter ouvinte = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                trataPressionar(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                trataSoltar(e.getPoint());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    trataDuploClique(e.getPoint());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                atualizarCursorHover(e.getPoint());
            }
        };
        addMouseListener(ouvinte);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                trataArrastar(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                atualizarCursorHover(e.getPoint());
            }
        });
    }

    private void carregarSituacaoAtual() {
        if (situacoes.isEmpty()) {
            situacao = null;
            return;
        }
        situacao = situacoes.get(indiceSituacaoAtual % situacoes.size());
        caixaParte1 = new CaixaDiagrama(CaixaDiagrama.Papel.PARTE1, "Parte 1",
                situacao.parte1.ehIncognita() ? null : situacao.parte1.valor,
                situacao.parte1.ehIncognita());
        caixaParte2 = new CaixaDiagrama(CaixaDiagrama.Papel.PARTE2, "Parte 2",
                situacao.parte2.ehIncognita() ? null : situacao.parte2.valor,
                situacao.parte2.ehIncognita());
        caixaTodo = new CaixaDiagrama(CaixaDiagrama.Papel.TODO, "Todo",
                situacao.todo.ehIncognita() ? null : situacao.todo.valor,
                situacao.todo.ehIncognita());

        caixaParte1.limites.setBounds(areaDiagrama.x + 10, areaDiagrama.y + 20, LARGURA_CAIXA, ALTURA_CAIXA);
        caixaParte2.limites.setBounds(areaDiagrama.x + 10, areaDiagrama.y + 150, LARGURA_CAIXA, ALTURA_CAIXA);
        caixaTodo.limites.setBounds(areaDiagrama.x + 250, areaDiagrama.y + 85, LARGURA_CAIXA, ALTURA_CAIXA);

        todasCorretas = false;
        sucessoExibido = false;
        progressoSucesso = 0f;
        mostrarProximaTarefa = false;
        campoIncognita.setVisible(false);
        botaoConfirmar.setVisible(false);
    }

    private List<CaixaDiagrama> caixas() {
        List<CaixaDiagrama> lista = new ArrayList<CaixaDiagrama>();
        lista.add(caixaParte1);
        lista.add(caixaParte2);
        lista.add(caixaTodo);
        return lista;
    }

    // ----------------------------------------------------------------
    // Layout do enunciado com números/? destacados diretamente no texto
    // ----------------------------------------------------------------

    private static final class TokenVisual {
        final String texto;
        final Rectangle bounds = new Rectangle();
        final NumeroTextoExtraido numero; // null para palavras comuns

        TokenVisual(String texto, NumeroTextoExtraido numero) {
            this.texto = texto;
            this.numero = numero;
        }
    }

    private void recalcularLayoutEnunciado(Graphics2D g2) {
        tokensEnunciado.clear();
        if (situacao == null) {
            return;
        }
        String texto = situacao.enunciado;
        List<NumeroTextoExtraido> spans = new ArrayList<NumeroTextoExtraido>();
        spans.add(situacao.parte1);
        spans.add(situacao.parte2);
        spans.add(situacao.todo);
        // ordena por posição no texto
        for (int i = 0; i < spans.size(); i++) {
            for (int j = i + 1; j < spans.size(); j++) {
                if (spans.get(j).inicio < spans.get(i).inicio) {
                    NumeroTextoExtraido tmp = spans.get(i);
                    spans.set(i, spans.get(j));
                    spans.set(j, tmp);
                }
            }
        }

        List<Object> partes = new ArrayList<Object>(); // String (texto comum) ou NumeroTextoExtraido
        int cursor = 0;
        for (NumeroTextoExtraido span : spans) {
            if (span.inicio > cursor) {
                partes.add(texto.substring(cursor, span.inicio));
            }
            partes.add(span);
            cursor = span.fim;
        }
        if (cursor < texto.length()) {
            partes.add(texto.substring(cursor));
        }

        g2.setFont(TemaComposicaoMedidas.FONTE_ENUNCIADO);
        FontMetrics fmTexto = g2.getFontMetrics();
        Font fonteNumero = TemaComposicaoMedidas.FONTE_NUMERO;
        FontMetrics fmNumero = g2.getFontMetrics(fonteNumero);

        int x = areaEnunciado.x;
        int y = areaEnunciado.y + fmTexto.getAscent();
        int alturaLinha = Math.max(fmTexto.getHeight(), fmNumero.getHeight()) + 10;
        int larguraMaxima = areaEnunciado.x + areaEnunciado.width;

        for (Object parte : partes) {
            if (parte instanceof String) {
                String[] palavras = ((String) parte).split("(?<=\\s)");
                for (String palavra : palavras) {
                    if (palavra.length() == 0) {
                        continue;
                    }
                    int largura = fmTexto.stringWidth(palavra);
                    if (x + largura > larguraMaxima && x > areaEnunciado.x) {
                        x = areaEnunciado.x;
                        y += alturaLinha;
                    }
                    TokenVisual tv = new TokenVisual(palavra, null);
                    tv.bounds.setBounds(x, y - fmTexto.getAscent(), largura, alturaLinha);
                    tokensEnunciado.add(tv);
                    x += largura;
                }
            } else {
                NumeroTextoExtraido numero = (NumeroTextoExtraido) parte;
                String rotulo = numero.ehIncognita() ? "?" : numero.textoOriginal;
                int largura = fmNumero.stringWidth(rotulo) + 14;
                if (x + largura > larguraMaxima && x > areaEnunciado.x) {
                    x = areaEnunciado.x;
                    y += alturaLinha;
                }
                TokenVisual tv = new TokenVisual(rotulo, numero);
                tv.bounds.setBounds(x, y - fmTexto.getAscent(), largura, alturaLinha - 4);
                tokensEnunciado.add(tv);
                x += largura + 4;
            }
        }
    }

    // ----------------------------------------------------------------
    // Interação de mouse
    // ----------------------------------------------------------------

    private void trataPressionar(Point p) {
        if (arrastando) {
            return;
        }
        // 1) uma peça já posicionada numa caixa (não a incógnita já confirmada)
        for (CaixaDiagrama caixa : caixas()) {
            if (caixa.limites.contains(p) && caixa.valorAtual != null
                    && !(caixa.ehIncognita && caixa.incognitaConfirmada)) {
                iniciarArraste(caixa.valorAtual, caixa.ehIncognita, caixa);
                return;
            }
        }
        // 2) um token no enunciado (sempre cria cópia)
        for (TokenVisual tv : tokensEnunciado) {
            if (tv.numero != null && tv.bounds.contains(p)) {
                Integer valor = tv.numero.ehIncognita() ? null : tv.numero.valor;
                iniciarArraste(valor, tv.numero.ehIncognita(), null);
                return;
            }
        }
    }

    private void iniciarArraste(Integer valor, boolean incognita, CaixaDiagrama origem) {
        arrastando = true;
        valorArrastado = valor;
        incognitaArrastada = incognita;
        caixaOrigemArraste = origem;
        if (origem != null) {
            origem.valorAtual = null;
            origem.dicaErro = "";
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    private void trataArrastar(Point p) {
        if (!arrastando) {
            return;
        }
        posicaoMouseAtual = p;
        caixaEmHover = null;
        for (CaixaDiagrama caixa : caixas()) {
            if (caixa.limites.contains(p)) {
                caixaEmHover = caixa;
                break;
            }
        }
        repaint();
    }

    private void trataSoltar(Point p) {
        if (!arrastando) {
            return;
        }
        CaixaDiagrama alvo = null;
        for (CaixaDiagrama caixa : caixas()) {
            if (caixa.limites.contains(p)) {
                alvo = caixa;
                break;
            }
        }
        finalizarArraste(alvo);
    }

    private void finalizarArraste(CaixaDiagrama alvo) {
        boolean eraMovimento = caixaOrigemArraste != null;
        if (alvo == null) {
            // solto fora de qualquer caixa: se era um movimento, volta para a origem
            if (eraMovimento) {
                caixaOrigemArraste.valorAtual = valorArrastado;
            }
            encerrarArraste();
            return;
        }

        if (alvo.valorAtual != null) {
            // caixa já ocupada: dispara erro e descarta a cópia (ou volta a peça movida)
            dispararErro(alvo, "");
            if (eraMovimento) {
                caixaOrigemArraste.valorAtual = valorArrastado;
            }
            encerrarArraste();
            return;
        }

        boolean acertou = incognitaArrastada
                ? alvo.ehIncognita
                : (!alvo.ehIncognita && alvo.valorEsperado != null && alvo.valorEsperado.equals(valorArrastado));

        if (!acertou) {
            alvo.valorAtual = valorArrastado; // a peça permanece na posição errada
            dispararErro(alvo, "Estou no lugar errado. Arraste-me para o local certo.");
            encerrarArraste();
            avaliarSucessoGlobal();
            return;
        }

        alvo.dicaErro = "";
        if (alvo.ehIncognita) {
            alvo.valorAtual = null; // não é um valor solto; vira campo de entrada
            posicionarCampoIncognita(alvo);
        } else {
            alvo.valorAtual = valorArrastado;
        }
        encerrarArraste();
        avaliarSucessoGlobal();
    }

    private void encerrarArraste() {
        arrastando = false;
        valorArrastado = null;
        caixaOrigemArraste = null;
        caixaEmHover = null;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    private void trataDuploClique(Point p) {
        // Atalho de mouse: duplo-clique num número do enunciado o posiciona
        // automaticamente na primeira caixa vazia compatível.
        for (TokenVisual tv : tokensEnunciado) {
            if (tv.numero != null && tv.bounds.contains(p)) {
                for (CaixaDiagrama caixa : caixas()) {
                    if (caixa.valorAtual == null && !(caixa.ehIncognita && caixa.incognitaConfirmada)) {
                        Integer valor = tv.numero.ehIncognita() ? null : tv.numero.valor;
                        boolean incognita = tv.numero.ehIncognita();
                        boolean acertou = incognita
                                ? caixa.ehIncognita
                                : (!caixa.ehIncognita && caixa.valorEsperado != null && caixa.valorEsperado.equals(valor));
                        if (acertou) {
                            if (incognita) {
                                posicionarCampoIncognita(caixa);
                            } else {
                                caixa.valorAtual = valor;
                            }
                            avaliarSucessoGlobal();
                            repaint();
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    private void atualizarCursorHover(Point p) {
        boolean sobreAlvo = false;
        for (TokenVisual tv : tokensEnunciado) {
            if (tv.numero != null && tv.bounds.contains(p)) {
                sobreAlvo = true;
                break;
            }
        }
        if (!sobreAlvo) {
            for (CaixaDiagrama caixa : caixas()) {
                if (caixa.limites.contains(p) && caixa.valorAtual != null) {
                    sobreAlvo = true;
                    break;
                }
            }
        }
        setCursor(Cursor.getPredefinedCursor(sobreAlvo ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    // ----------------------------------------------------------------
    // Erro: tremor + som + dica fixa (só ao soltar, nunca durante o arrasto)
    // ----------------------------------------------------------------

    private void dispararErro(final CaixaDiagrama caixa, String dica) {
        Toolkit.getDefaultToolkit().beep();
        caixa.dicaErro = dica;
        caixaTremendo = caixa;
        quadroTremor = 0;
        if (temporizadorTremor != null && temporizadorTremor.isRunning()) {
            temporizadorTremor.stop();
        }
        temporizadorTremor = new Timer(18, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quadroTremor++;
                if (quadroTremor > 16) {
                    caixaTremendo = null;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }
        });
        temporizadorTremor.start();

        // Se o sucesso já estava exibido, mover/errar uma peça desfaz o estado.
        desfazerSucessoSeNecessario();
    }

    private int deslocamentoTremor(CaixaDiagrama caixa) {
        if (caixa != caixaTremendo) {
            return 0;
        }
        double t = quadroTremor;
        double amplitude = Math.max(0, 6 - quadroTremor * 0.4);
        return (int) Math.round(Math.sin(t * 1.6) * amplitude);
    }

    // ----------------------------------------------------------------
    // Incógnita: campo numérico + confirmar
    // ----------------------------------------------------------------

    private CaixaDiagrama caixaIncognitaEmEdicao;

    private void posicionarCampoIncognita(CaixaDiagrama caixa) {
        caixaIncognitaEmEdicao = caixa;
        Rectangle r = caixa.limites;
        campoIncognita.setBounds(r.x + 8, r.y + 20, r.width - 16, 28);
        campoIncognita.setText(caixa.textoDigitadoIncognita);
        campoIncognita.setVisible(true);
        botaoConfirmar.setBounds(r.x - 4, r.y + r.height + 6, r.width + 8, 24);
        botaoConfirmar.setVisible(true);
        campoIncognita.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void confirmarIncognita() {
        if (caixaIncognitaEmEdicao == null) {
            return;
        }
        CaixaDiagrama caixa = caixaIncognitaEmEdicao;
        String digitado = campoIncognita.getText().trim();
        caixa.textoDigitadoIncognita = digitado;
        Integer valorDigitado = null;
        try {
            valorDigitado = Integer.valueOf(digitado);
        } catch (NumberFormatException ex) {
            valorDigitado = null;
        }
        int esperado = calcularValorIncognita();
        if (valorDigitado != null && valorDigitado.intValue() == esperado) {
            caixa.incognitaConfirmada = true;
            caixa.valorAtual = valorDigitado;
            caixa.dicaErro = "";
            campoIncognita.setVisible(false);
            botaoConfirmar.setVisible(false);
            caixaIncognitaEmEdicao = null;
            avaliarSucessoGlobal();
        } else {
            dispararErro(caixa, "Estou no lugar errado. Arraste-me para o local certo.");
            campoIncognita.requestFocusInWindow();
            campoIncognita.selectAll();
        }
        repaint();
    }

    private int calcularValorIncognita() {
        if (situacao.parte1.ehIncognita()) {
            return situacao.todo.valor - situacao.parte2.valor;
        }
        if (situacao.parte2.ehIncognita()) {
            return situacao.todo.valor - situacao.parte1.valor;
        }
        return situacao.parte1.valor + situacao.parte2.valor;
    }

    // ----------------------------------------------------------------
    // Sucesso: espera, pintura gradual de azul, "Próxima tarefa"
    // ----------------------------------------------------------------

    private void avaliarSucessoGlobal() {
        boolean tudoCorreto = caixaParte1.estaCorreta() && caixaParte2.estaCorreta() && caixaTodo.estaCorreta();
        if (tudoCorreto && !todasCorretas) {
            todasCorretas = true;
            agendarExibicaoSucesso();
        } else if (!tudoCorreto) {
            todasCorretas = false;
            desfazerSucessoSeNecessario();
        }
    }

    private void agendarExibicaoSucesso() {
        if (temporizadorEsperaSucesso != null && temporizadorEsperaSucesso.isRunning()) {
            temporizadorEsperaSucesso.stop();
        }
        temporizadorEsperaSucesso = new Timer(1200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                if (todasCorretas) {
                    iniciarAnimacaoSucesso();
                }
            }
        });
        temporizadorEsperaSucesso.setRepeats(false);
        temporizadorEsperaSucesso.start();
    }

    private void iniciarAnimacaoSucesso() {
        sucessoExibido = true;
        if (animadorSucesso != null && animadorSucesso.isRunning()) {
            animadorSucesso.stop();
        }
        animadorSucesso = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                progressoSucesso = Math.min(1f, progressoSucesso + 0.06f);
                repaint();
                if (progressoSucesso >= 1f) {
                    ((Timer) e.getSource()).stop();
                    mostrarProximaTarefa = true;
                    repaint();
                }
            }
        });
        animadorSucesso.start();
    }

    private void desfazerSucessoSeNecessario() {
        if (!sucessoExibido && (temporizadorEsperaSucesso == null || !temporizadorEsperaSucesso.isRunning())) {
            return;
        }
        if (temporizadorEsperaSucesso != null) {
            temporizadorEsperaSucesso.stop();
        }
        if (animadorSucesso != null) {
            animadorSucesso.stop();
        }
        sucessoExibido = false;
        mostrarProximaTarefa = false;
        progressoSucesso = 0f;
        repaint();
    }

    private void avancarProximaTarefa() {
        indiceSituacaoAtual++;
        carregarSituacaoAtual();
        repaint();
    }

    // ----------------------------------------------------------------
    // Pintura
    // ----------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (situacao == null) {
            g2.setColor(TemaComposicaoMedidas.TEXTO_SECUNDARIO);
            g2.setFont(TemaComposicaoMedidas.FONTE_ENUNCIADO);
            g2.drawString("Nenhuma situação de composição de medidas encontrada no log curado.", 30, 60);
            g2.dispose();
            return;
        }

        recalcularLayoutEnunciado(g2);
        desenharEnunciado(g2);
        desenharDiagrama(g2);
        desenharChipFlutuante(g2);

        g2.dispose();
    }

    private void desenharEnunciado(Graphics2D g2) {
        for (TokenVisual tv : tokensEnunciado) {
            if (tv.numero != null) {
                g2.setColor(TemaComposicaoMedidas.HOVER_NEUTRO);
                g2.fill(new RoundRectangle2D.Float(tv.bounds.x, tv.bounds.y, tv.bounds.width, tv.bounds.height, 8, 8));
                Stroke tracejado = new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1f, new float[]{3f, 3f}, 0f);
                Stroke original = g2.getStroke();
                g2.setColor(TemaComposicaoMedidas.BORDA_NEUTRA);
                g2.setStroke(tracejado);
                g2.draw(new RoundRectangle2D.Float(tv.bounds.x, tv.bounds.y, tv.bounds.width, tv.bounds.height, 8, 8));
                g2.setStroke(original);

                g2.setColor(TemaComposicaoMedidas.TEXTO);
                g2.setFont(TemaComposicaoMedidas.FONTE_NUMERO);
                FontMetrics fm = g2.getFontMetrics();
                int tx = tv.bounds.x + (tv.bounds.width - fm.stringWidth(tv.texto)) / 2;
                int ty = tv.bounds.y + (tv.bounds.height + fm.getAscent()) / 2 - 2;
                g2.drawString(tv.texto, tx, ty);
            } else {
                g2.setColor(TemaComposicaoMedidas.TEXTO);
                g2.setFont(TemaComposicaoMedidas.FONTE_ENUNCIADO);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(tv.texto, tv.bounds.x, tv.bounds.y + fm.getAscent());
            }
        }
    }

    private void desenharDiagrama(Graphics2D g2) {
        boolean pintarSucesso = progressoSucesso > 0f;

        desenharChave(g2);

        for (CaixaDiagrama caixa : caixas()) {
            if (caixa == caixaIncognitaEmEdicao && campoIncognita.isVisible()) {
                desenharRotulo(g2, caixa);
                continue; // a própria caixa é substituída pelo campo real
            }
            desenharCaixa(g2, caixa, pintarSucesso);
        }

        if (mostrarProximaTarefa) {
            g2.setColor(TemaComposicaoMedidas.SUCESSO);
            g2.setFont(TemaComposicaoMedidas.FONTE_LINK);
            String texto = "Próxima tarefa";
            g2.drawString(texto, areaDiagrama.x + 10, areaDiagrama.y + areaDiagrama.height - 8);
        }
    }

    private void desenharChave(Graphics2D g2) {
        int x1 = caixaParte1.limites.x + caixaParte1.limites.width;
        int y1 = caixaParte1.limites.y;
        int y2 = caixaParte2.limites.y + caixaParte2.limites.height;
        int xChave = x1 + 18;
        g2.setColor(TemaComposicaoMedidas.BORDA_NEUTRA);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawLine(x1 + 6, y1, xChave, y1);
        g2.drawLine(xChave, y1, xChave, y2);
        g2.drawLine(xChave, y2, x1 + 6, y2);
        int xMeio = (caixaParte1.limites.x + caixaParte1.limites.width + caixaTodo.limites.x) / 2;
        int yMeio = (y1 + y2) / 2;
        g2.drawLine(xChave, yMeio, xMeio, caixaTodo.limites.y + caixaTodo.limites.height / 2);
    }

    private void desenharRotulo(Graphics2D g2, CaixaDiagrama caixa) {
        g2.setColor(TemaComposicaoMedidas.TEXTO_SECUNDARIO);
        g2.setFont(TemaComposicaoMedidas.FONTE_ROTULO);
        FontMetrics fm = g2.getFontMetrics();
        int tx = caixa.limites.x + (caixa.limites.width - fm.stringWidth(caixa.rotulo)) / 2;
        g2.drawString(caixa.rotulo, tx, caixa.limites.y - 8);
    }

    private void desenharCaixa(Graphics2D g2, CaixaDiagrama caixa, boolean pintarSucesso) {
        int dx = deslocamentoTremor(caixa);
        Rectangle r = new Rectangle(caixa.limites);
        r.x += dx;

        boolean comErro = caixa.dicaErro != null && caixa.dicaErro.length() > 0 && !caixa.estaCorreta();
        boolean destacadoHover = caixa == caixaEmHover && arrastando;

        Color fundo = TemaComposicaoMedidas.CAIXA_NEUTRA_FUNDO;
        Color borda = TemaComposicaoMedidas.BORDA_NEUTRA;
        if (pintarSucesso) {
            fundo = interpolar(TemaComposicaoMedidas.CAIXA_NEUTRA_FUNDO, TemaComposicaoMedidas.SUCESSO_FUNDO, progressoSucesso);
            borda = interpolar(TemaComposicaoMedidas.BORDA_NEUTRA, TemaComposicaoMedidas.SUCESSO, progressoSucesso);
        } else if (comErro) {
            fundo = TemaComposicaoMedidas.ERRO_FUNDO;
            borda = TemaComposicaoMedidas.ERRO;
        } else if (destacadoHover) {
            fundo = TemaComposicaoMedidas.HOVER_NEUTRO;
        }

        g2.setColor(fundo);
        g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, RAIO_CAIXA, RAIO_CAIXA));
        g2.setColor(borda);
        g2.setStroke(new BasicStroke(1.6f));
        g2.draw(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, RAIO_CAIXA, RAIO_CAIXA));

        desenharRotulo(g2, caixa);

        if (caixa.valorAtual != null) {
            String texto = String.valueOf(caixa.valorAtual);
            g2.setColor(comErro ? TemaComposicaoMedidas.ERRO : TemaComposicaoMedidas.TEXTO);
            g2.setFont(TemaComposicaoMedidas.FONTE_NUMERO);
            FontMetrics fm = g2.getFontMetrics();
            int tx = r.x + (r.width - fm.stringWidth(texto)) / 2;
            int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
            g2.drawString(texto, tx, ty);
        } else if (caixa.ehIncognita && !caixa.incognitaConfirmada && caixa != caixaIncognitaEmEdicao) {
            g2.setColor(TemaComposicaoMedidas.TEXTO_SECUNDARIO);
            g2.setFont(TemaComposicaoMedidas.FONTE_NUMERO);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("?", r.x + (r.width - fm.stringWidth("?")) / 2, r.y + (r.height + fm.getAscent()) / 2 - 4);
        }

        if (comErro && caixa.dicaErro != null && caixa.dicaErro.length() > 0) {
            g2.setColor(TemaComposicaoMedidas.ERRO);
            g2.setFont(TemaComposicaoMedidas.FONTE_DICA);
            desenharTextoQuebrado(g2, caixa.dicaErro, r.x - 20, r.y + r.height + 8, r.width + 40);
        }
    }

    private void desenharTextoQuebrado(Graphics2D g2, String texto, int x, int y, int largura) {
        FontMetrics fm = g2.getFontMetrics();
        String[] palavras = texto.split(" ");
        StringBuilder linha = new StringBuilder();
        int yAtual = y;
        for (String palavra : palavras) {
            String tentativa = linha.length() == 0 ? palavra : linha + " " + palavra;
            if (fm.stringWidth(tentativa) > largura && linha.length() > 0) {
                g2.drawString(linha.toString(), x, yAtual);
                linha = new StringBuilder(palavra);
                yAtual += fm.getHeight();
            } else {
                linha = new StringBuilder(tentativa);
            }
        }
        if (linha.length() > 0) {
            g2.drawString(linha.toString(), x, yAtual);
        }
    }

    private void desenharChipFlutuante(Graphics2D g2) {
        if (!arrastando) {
            return;
        }
        int x = posicaoMouseAtual.x - LARGURA_CHIP / 2;
        int y = posicaoMouseAtual.y - ALTURA_CHIP / 2;
        g2.setColor(TemaComposicaoMedidas.CAIXA_NEUTRA_FUNDO);
        g2.fill(new RoundRectangle2D.Float(x, y, LARGURA_CHIP, ALTURA_CHIP, 10, 10));
        g2.setColor(TemaComposicaoMedidas.BORDA_NEUTRA);
        g2.setStroke(new BasicStroke(1.4f));
        g2.draw(new RoundRectangle2D.Float(x, y, LARGURA_CHIP, ALTURA_CHIP, 10, 10));

        String texto = incognitaArrastada ? "?" : String.valueOf(valorArrastado);
        g2.setColor(TemaComposicaoMedidas.TEXTO);
        g2.setFont(TemaComposicaoMedidas.FONTE_NUMERO);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x + (LARGURA_CHIP - fm.stringWidth(texto)) / 2,
                y + (ALTURA_CHIP + fm.getAscent()) / 2 - 4);
    }

    private static Color interpolar(Color a, Color b, float t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    {
        // clique em "Próxima tarefa": tratado via mouse listener adicional
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mostrarProximaTarefa) {
                    Rectangle areaLink = new Rectangle(areaDiagrama.x + 10, areaDiagrama.y + areaDiagrama.height - 22, 130, 20);
                    if (areaLink.contains(e.getPoint())) {
                        avancarProximaTarefa();
                    }
                }
            }
        });
    }
}
