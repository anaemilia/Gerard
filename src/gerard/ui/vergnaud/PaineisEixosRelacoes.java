package gerard.ui.vergnaud;

import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gerard.interacao.eixo.ControleVisibilidadeEixoPapel;

/**
 * Material concreto próprio para as categorias de Relações
 * (TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES) — item 4 do levantamento de
 * pendências de 2026-08-11 (TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md).
 * Um {@link ScaffoldingGraficoInteiros} por papel da categoria (3 no caso
 * das duas categorias de Relações), todos visíveis e manipuláveis ao mesmo
 * tempo, em vez de um único painel compartilhado mostrado sob demanda.
 *
 * Decisão da usuária (2026-08-16): "os dados e comportamento são iguais
 * (mostram números inteiros, positivos e negativos, com um ponto de
 * controle manipulável e consistência entre representações) — isso deve
 * ser a base da tomada de decisão sobre reaproveitar ou não, sempre usando
 * o princípio da localidade do conhecimento." Por isso esta classe cria
 * várias instâncias independentes da MESMA classe já existente
 * ({@link ScaffoldingGraficoInteiros}), em vez de duplicar a lógica de
 * eixo/sinal/ponto de controle/consistência em um widget novo.
 *
 * Deliberadamente fora do escopo desta classe: o mecanismo já existente e
 * validado de escolha de sinal sob demanda (um único
 * {@code ScaffoldingGraficoInteiros} acionado pelo menu de radio buttons
 * ao digitar/soltar um item, usado hoje por Comparação de Medidas e outras
 * categorias) continua exatamente como está, em Main.java, sem nenhuma
 * alteração — esta classe só acrescenta um coordenador novo e paralelo
 * para o caso de vários papéis sempre visíveis ao mesmo tempo, decisão
 * também da usuária (2026-08-16), para não arriscar regressão no fluxo já
 * auditado (telemetria de pesquisa, bloqueio de quantidade negativa).
 *
 * Não decide POR SI SÓ o valor inicial de cada papel — quem usa
 * (Main.TelaGerard) decide isso, usando os helpers que já existem para
 * interpretar o valor de um ElementoVergnaud.
 *
 * Visibilidade individual por papel (2026-08-17, revisão sobre a revisão do
 * mesmo dia): a usuária achou os 3 eixos aparecendo de uma vez, sempre
 * visíveis, poluído demais. Decisão: cada papel ganha um ícone de lupa
 * (perto do círculo/retângulo do elemento) — o eixo daquele papel só fica
 * visível e manipulável depois que a lupa é clicada ("uma amplificação da
 * visão do que é um número relativo e por que ele aparece com sinal"), um
 * de cada vez, independente dos outros papéis. Esta classe também passou a
 * desenhar e tratar clique nessas lupas ({@link #desenharLupas},
 * {@link #processarPressionamentoLupa}) — mesma classe que já desenha e
 * trata interação do eixo propriamente dito, localidade do conhecimento.
 * Fechar o painel pelo próprio botão "esconder" (já existente em
 * ScaffoldingGraficoInteiros) faz a lupa reaparecer — ver
 * {@link #encontrarComOcultacaoPorInteracao()}/{@link #ocultarRevelacao}.
 */
public final class PaineisEixosRelacoes {

    /** Tamanho (px) do ícone de lupa clicável perto de cada elemento. */
    private static final int TAMANHO_LUPA = 22;

    /** Um papel do diagrama com seu próprio eixo dos inteiros. */
    public static final class Painel {
        public final ElementoVergnaud elemento;
        public final ScaffoldingGraficoInteiros grafico = new ScaffoldingGraficoInteiros();
        public final ApresentadorGraficoInteiros apresentador = new ApresentadorGraficoInteiros(grafico);
        /**
         * Visibilidade individual deste papel — começa falso (só a lupa
         * aparece); vira true ao clicar na lupa, volta a falso ao clicar no
         * botão "esconder" do próprio painel. Ver Javadoc da classe.
         */
        private final ControleVisibilidadeEixoPapel controleVisibilidade =
                new ControleVisibilidadeEixoPapel();

        private Painel(ElementoVergnaud elemento) {
            this.elemento = elemento;
        }

        public boolean estaRevelado() {
            return controleVisibilidade.estaRevelado();
        }

        public ControleVisibilidadeEixoPapel.Estado getEstadoVisibilidade() {
            return controleVisibilidade.getEstado();
        }
    }

    private final List<Painel> paineis = new ArrayList<Painel>();
    private boolean ativo;

    public boolean estaAtivo() {
        return ativo;
    }

    /** Painéis atualmente geridos (vazio se {@link #estaAtivo()} for falso). */
    public List<Painel> obterPaineis() {
        return Collections.unmodifiableList(paineis);
    }

    /**
     * Cria um painel por elemento número relativo (2026-08-18: regra da
     * usuária generalizada do item 4 — "todo número relativo ou
     * transformação carrega uma lupa. Essa é a regra", não uma lista fixa
     * de categorias). O critério é o descritor semântico {@code
     * elemento.exibirLupa}, publicado pela cena e só materializado aqui —
     * não a forma geométrica do elemento. Elementos não nulos sem o
     * descritor (ex.: as âncoras invisíveis de medida em Composição de
     * Transformações) são silenciosamente ignorados.
     *
     * Não define valor nem posição — isso é responsabilidade de quem
     * chama, logo em seguida, via {@code painel.apresentador.mostrar/
     * registrarEscolha} e {@code painel.grafico.definirPosicaoInicial}.
     * Sem efeito se já ativo, para não recriar nem reposicionar painéis
     * que o usuário já pode ter arrastado.
     */
    public void ativar(List<ElementoVergnaud> elementos) {
        if (ativo) {
            return;
        }
        paineis.clear();
        if (elementos != null) {
            for (ElementoVergnaud elemento : elementos) {
                if (elemento != null && elemento.exibirLupa) {
                    paineis.add(new Painel(elemento));
                }
            }
        }
        ativo = true;
    }

    /** Esconde e libera todos os painéis — chamado ao trocar de situação/categoria. */
    public void desativar() {
        for (Painel painel : paineis) {
            painel.grafico.ocultar();
        }
        paineis.clear();
        ativo = false;
    }

    public boolean estaArrastando() {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.estaArrastando()) {
                return true;
            }
        }
        return false;
    }

    /** Painel cujo ponto de controle ou painel está sendo arrastado agora, se houver. */
    public Painel encontrarArrastando() {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.estaArrastando()) {
                return painel;
            }
        }
        return null;
    }

    /** Painel cujo valor mudou por interação e ainda não foi consumido, se houver. */
    public Painel encontrarComAlteracaoPorInteracao() {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.houveAlteracaoValorPorInteracao()) {
                return painel;
            }
        }
        return null;
    }

    /** Painel cujo botão "esconder" acabou de ser clicado, se houver — usado para reexibir a lupa dele. */
    public Painel encontrarComOcultacaoPorInteracao() {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.foiOcultadoPorInteracao()) {
                return painel;
            }
        }
        return null;
    }

    /** Volta o papel ao estado "só lupa visível" — chamado depois que o próprio painel se escondeu. */
    public void ocultarRevelacao(Painel painel) {
        if (painel != null) {
            painel.controleVisibilidade.ocultar();
        }
    }

    public boolean contemPontoControle(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.contemPontoControle(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    /** Verdadeiro se o ponto está dentro da área visual de qualquer painel visível. */
    public boolean contemAlgumPainel(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.obterAreaVisualPainel().contains(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    /** Dica do botão de esconder — igual em qualquer instância, texto fixo localizado. */
    public String obterDicaBotaoEsconder() {
        return paineis.isEmpty() ? "" : paineis.get(0).grafico.obterDicaBotaoEsconder();
    }

    /** Dica do ponto de controle — igual em qualquer instância, texto fixo localizado. */
    public String obterDicaPontoControle() {
        return paineis.isEmpty() ? "" : paineis.get(0).grafico.obterDicaPontoControle();
    }

    public boolean contemBotaoEsconder(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.contemBotaoEsconder(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Natureza da interação no primeiro painel que reconhecer o ponto —
     * mesma semântica de {@link ScaffoldingGraficoInteiros#identificarNaturezaInteracao}.
     * Só considera papéis já revelados — um papel ainda fechado (só lupa) não
     * tem eixo desenhado, não há o que identificar.
     */
    public ScaffoldingGraficoInteiros.NaturezaInteracao identificarNaturezaInteracao(
            int mouseX, int mouseY, int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        for (Painel painel : paineis) {
            if (!painel.estaRevelado()) {
                continue;
            }
            ScaffoldingGraficoInteiros.NaturezaInteracao natureza =
                    painel.grafico.identificarNaturezaInteracao(
                            mouseX, mouseY, larguraTela, alturaTela, areaDiagrama);
            if (natureza != ScaffoldingGraficoInteiros.NaturezaInteracao.NENHUMA) {
                return natureza;
            }
        }
        return ScaffoldingGraficoInteiros.NaturezaInteracao.NENHUMA;
    }

    /** Repassa o pressionamento ao primeiro painel revelado que o consumir. */
    public boolean processarPressionamento(
            int mouseX, int mouseY, int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.processarPressionamento(
                    mouseX, mouseY, larguraTela, alturaTela, areaDiagrama)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Testa o clique contra a lupa de cada papel ainda não revelado; a
     * primeira que reconhecer o ponto revela seu painel e é devolvida (quem
     * chama, em geral, ainda precisa posicionar/semear o valor do painel
     * recém-revelado — ver Main.prepararPainelEixoRelacao). {@code null} se
     * nenhuma lupa foi atingida.
     */
    public Painel processarPressionamentoLupa(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (painel.controleVisibilidade.podeRevelar()
                    && obterAreaLupa(painel).contains(mouseX, mouseY)) {
                painel.controleVisibilidade.revelar();
                return painel;
            }
        }
        return null;
    }

    public void arrastarPara(int mouseX, int mouseY, int larguraTela, int alturaTela) {
        Painel arrastando = encontrarArrastando();
        if (arrastando != null) {
            arrastando.grafico.arrastarPara(mouseX, mouseY, larguraTela, alturaTela);
        }
    }

    public void finalizarArraste() {
        for (Painel painel : paineis) {
            painel.grafico.finalizarArraste();
        }
    }

    public void desenhar(Graphics2D g2, int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado()) {
                painel.grafico.desenhar(g2, larguraTela, alturaTela, areaDiagrama);
            }
        }
    }

    public void desenharPontosControleEmPrimeiroPlano(Graphics2D g2) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado() && painel.grafico.estaArrastandoPontoControle()) {
                painel.grafico.desenharPontoControleEmPrimeiroPlano(g2);
            }
        }
    }

    public void atualizarFocoBotaoEsconder(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (painel.estaRevelado()) {
                painel.grafico.atualizarFocoBotaoEsconder(mouseX, mouseY);
            }
        }
    }

    public void limparFocoBotaoEsconder() {
        for (Painel painel : paineis) {
            painel.grafico.limparFocoBotaoEsconder();
        }
    }

    /** Área clicável da lupa de um papel — ancorada no canto superior direito do elemento. */
    private Rectangle obterAreaLupa(Painel painel) {
        ElementoVergnaud elemento = painel.elemento;
        int x = elemento.x + elemento.largura - TAMANHO_LUPA + 6;
        int y = elemento.y - TAMANHO_LUPA / 2 - 2;
        return new Rectangle(x, y, TAMANHO_LUPA, TAMANHO_LUPA);
    }

    /** Desenha a lupa de cada papel ainda não revelado — os já revelados mostram o eixo em vez dela. */
    public void desenharLupas(Graphics2D g2) {
        if (g2 == null) {
            return;
        }
        for (Painel painel : paineis) {
            if (!painel.estaRevelado()) {
                desenharLupa(g2, obterAreaLupa(painel));
            }
        }
    }

    /** Verdadeiro se o ponto está sobre a lupa de algum papel ainda não revelado — usado para cursor/tooltip. */
    public boolean contemLupa(int mouseX, int mouseY) {
        for (Painel painel : paineis) {
            if (!painel.estaRevelado() && obterAreaLupa(painel).contains(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    /** Dica da lupa — igual em qualquer papel, texto fixo localizado. */
    public String obterDicaLupa() {
        return gerard.i18n.ServicoLocalizacao.getInstancia().texto("ui.tooltip.integerAxis.reveal");
    }

    /**
     * Ícone simples de lupa (círculo + cabo), traço fino, mesmo espírito
     * visual neutro dos outros ícones do app (ver Main.prepararTracoIconeCategoria).
     */
    private void desenharLupa(Graphics2D g2, Rectangle area) {
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
            g.fillOval(area.x, area.y, area.width, area.height);
            g.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            Stroke tracoOriginal = g.getStroke();
            g.setStroke(new BasicStroke(1.1f));
            g.drawOval(area.x, area.y, area.width, area.height);

            g.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = area.x + area.width / 2 - 2;
            int cy = area.y + area.height / 2 - 2;
            int raio = area.width / 2 - 7;
            g.drawOval(cx - raio, cy - raio, raio * 2, raio * 2);
            int caboX1 = cx + (int) (raio * 0.7);
            int caboY1 = cy + (int) (raio * 0.7);
            g.drawLine(caboX1, caboY1, caboX1 + 4, caboY1 + 4);
            g.setStroke(tracoOriginal);
        } finally {
            g.dispose();
        }
    }
}
