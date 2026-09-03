package gerard.ui.vergnaud;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Seletor soma/subtração mostrado ao aluno perto da seta do diagrama, nas 3
 * categorias de Relações (Transformação de Relação, Composição de Relações,
 * Composição de Transformações) — item 22, 2026-08-18.
 *
 * Decisões da usuária que moldaram esta classe:
 * - "o radiobutton soma/subtração aparece para o aluno escolher como parte
 *   da resposta. Sendo avaliada se está certa ou errada" — não é decorativo,
 *   é parte do que o aluno precisa acertar.
 * - Cada caso é analisado pelo pesquisador, que informa explicitamente
 *   Personagem_1, Personagem_2 e Personagem_3 na curadoria. Esta classe NÃO
 *   associa personagens pela posição no diagrama nem infere quem recebe a
 *   transformação. Ela apenas substitui cada marcador nomeado pelo campo
 *   homônimo da situação curada. Cabe ao pesquisador preencher esses campos
 *   de forma que a frase gerada faça sentido para aquela situação.
 * - "coloque nas três, pois essa base bruta de situações curadas pode
 *   aumentar" — presente nas 3 categorias mesmo que hoje só duas tenham
 *   exemplo real de subtração.
 *
 * Só fica ativo quando a situação curada tem uma operação válida
 * (SituacaoProblemaAditiva.getOperacaoRelacao() resolve para SOMA ou
 * SUBTRACAO) — situações antigas, sem esse campo preenchido, não mostram o
 * seletor (nada a avaliar).
 *
 * Ao contrário do aviso de sinal divergente (que usa o mecanismo persistente
 * compartilhado de desenharAnotacaoMouseOver), esta classe desenha sua
 * própria explicação, logo abaixo dos dois botões — mais simples e sem
 * mexer na cadeia de prioridade de tooltips já existente, já bastante
 * carregada. Localidade do conhecimento: o widget cuida do próprio feedback.
 *
 * Duas operações distintas em Composição de Transformações (2026-08-23,
 * pedido explícito da usuária: "a operação está sendo feita entre as
 * tranformações. Acho que vai ter que diferenciar dois tipos de
 * operações"): transformação_1 [op] transformação_2 = transformação_
 * resultante, e estado_inicial [op] transformação_resultante = estado_final.
 * Cada operação usa sua PRÓPRIA instância desta classe — não há estado
 * compartilhado entre elas — diferenciadas pelo parâmetro
 * {@link AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor} passado a
 * {@link #ativar}. Nas outras duas
 * categorias (Transformação de Relação, Composição de Relações) só existe
 * uma operação; passar ENTRE_ESTADO_E_TRANSFORMACAO nelas é a no-op.
 */
public final class SeletorOperacaoRelacaoAluno {

    private static final int RAIO_BOTAO = 9;
    // Distância mínima para caber os dois rótulos ("Soma" e "Subtração", o
    // mais largo dos dois em Arial Bold 13) lado a lado sem se tocarem —
    // reduzido de 92 (espaço em excesso reportado pela usuária) para o
    // mínimo que ainda separa os textos.
    private static final int ESPACAMENTO_BOTOES = 70;
    private static final int LARGURA_EXPLICACAO = 360;
    private static final int ESPACO_BOTAO_SINAL = 4;
    private static final int ESPACO_ENTRE_SINAL_E_NOME = 1;
    private static final int ESPACO_ROTULO_EXPLICACAO = 10;
    // Deslocamento horizontal da haste/vertical da chave em relação ao x1
    // armazenado no conector — replica o "+18" fixo em
    // ConectorVergnaud.desenharChaveVertical (o traço vertical real da
    // chave, não a marca de início da haste horizontal). Sem isso, o ponto
    // usado para centralizar o seletor fica 18px à esquerda da linha
    // realmente desenhada, e o rótulo "Soma" acaba caindo em cima dela.
    private static final int DESLOCAMENTO_TRACO_CHAVE = 18;
    // O seletor fica ACIMA do segmento/haste, não em cima dele. A distância
    // precisa cobrir o círculo do botão (2×RAIO_BOTAO) MAIS as duas linhas
    // de rótulo desenhadas abaixo dele (sinal + nome da operação, ver
    // desenharBotao) — só descontar o raio deixava o texto "Soma"/
    // "Subtração" cruzando a linha (reportado pela usuária: "tem que subir
    // mais"). RAIO_BOTAO*7 cobre círculo + as duas linhas de texto em Arial
    // 13 + uma folga visível acima da linha.
    private static final int ELEVACAO_ACIMA_DO_SEGMENTO = RAIO_BOTAO * 7;
    // Distância horizontal do centro do seletor até a borda esquerda do
    // círculo inferior (transformação resultante), para a segunda operação
    // de Composição de Transformações — "do lado esquerdo do círculo
    // inferior". Precisa caber os dois botões + rótulos ("Subtração" é o
    // mais largo) sem tocar o círculo; primeira estimativa, sujeita a
    // ajuste por captura de tela como já ocorreu com ELEVACAO_ACIMA_DO_SEGMENTO.
    private static final int DESLOCAMENTO_ESQUERDA_ESTADO_TRANSFORMACAO = 110;

    private boolean ativo;
    private Rectangle areaSoma;
    private Rectangle areaSubtracao;
    private int centroX;
    private int centroY;
    private OpcaoOperacaoCuradoria escolhaCorreta = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
    private OpcaoOperacaoCuradoria escolhaAluno = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
    private String textoExplicacaoCorreta = "";
    private boolean mostrarExplicacao;

    public boolean estaAtivo() {
        return ativo;
    }

    public OpcaoOperacaoCuradoria obterEscolhaAluno() {
        return escolhaAluno;
    }

    public boolean respondeuCorretamente() {
        return ativo && AvaliacaoEscolhaOperacaoRelacao
                .respondeuCorretamente(escolhaAluno, escolhaCorreta);
    }

    public void desativar() {
        ativo = false;
        areaSoma = null;
        areaSubtracao = null;
        escolhaCorreta = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        escolhaAluno = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        textoExplicacaoCorreta = "";
        mostrarExplicacao = false;
    }

    public static boolean aplicavel(TipoSituacaoAditiva tipo) {
        return AvaliacaoEscolhaOperacaoRelacao.aplicavel(tipo);
    }

    /**
     * Ativa o seletor para a situação atual. A posição vem, sempre que
     * possível, do próprio segmento de reta que o diagrama já desenha em
     * direção ao papel "relação final" — em Transformação de Relação, a seta
     * entre relação inicial e final; em Composição de Relações, a haste da
     * chave vertical que liga as duas relações somadas até a relação final
     * (ver RenderizadorTransformacaoRelacao/RenderizadorComposicaoRelacoes).
     * Não há coordenada própria duplicada: o seletor lê a geometria que o
     * diagrama já calculou (elementos e conectores), a mesma fonte usada
     * pelo resto do diagrama e por reposicionar(int,int) — por isso a
     * posição acompanha corretamente redimensionamento de janela e qualquer
     * mudança futura de layout dos renderizadores.
     *
     * Composição de Transformações tem DUAS instâncias independentes desta
     * classe (ver Javadoc da classe): a de ENTRE_TRANSFORMACOES fica acima
     * dos dois círculos superiores (transformação_1 e transformação_2); a de
     * ENTRE_ESTADO_E_TRANSFORMACAO fica à esquerda do círculo inferior
     * (transformação resultante) — nenhuma das duas usa o segmento único das
     * outras categorias, já que os 3 conectores da cena ligam as figuras de
     * medida entre si, nenhum liga os papéis de transformação entre si.
     */
    public void ativar(TipoSituacaoAditiva tipo, SituacaoProblemaAditiva situacao,
            List<ElementoVergnaud> elementos, List<ConectorVergnaud> conectores,
            AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor papel, ServicoLocalizacao localizacao) {
        desativar();
        if (!aplicavel(tipo) || situacao == null || elementos == null || elementos.size() < 3) {
            return;
        }
        AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor papelEfetivo = papel == null
                ? AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES : papel;
        boolean papelEstadoTransformacao = papelEfetivo
                == AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO;
        ServicoLocalizacao loc = localizacao == null ? ServicoLocalizacao.getInstancia() : localizacao;
        escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(tipo, situacao, papelEfetivo);
        if (escolhaCorreta == OpcaoOperacaoCuradoria.NAO_SELECIONADO) {
            // Segunda operação fora de Composição de Transformações, ou
            // situação sem operação curada válida (base antiga) — nada a
            // perguntar.
            return;
        }

        String chaveExplicacao = AvaliacaoEscolhaOperacaoRelacao
                .chaveExplicacao(tipo, papelEfetivo, escolhaCorreta);
        textoExplicacaoCorreta = chaveExplicacao == null ? ""
                : AvaliacaoEscolhaOperacaoRelacao
                        .preencherPersonagensCurados(loc.texto(chaveExplicacao), situacao);

        ElementoVergnaud e0 = elementos.get(0);
        ElementoVergnaud e1 = elementos.get(1);
        ElementoVergnaud e2 = elementos.get(2);

        if (papelEstadoTransformacao) {
            // "radiobutton de operações entre estado inicial e transformação
            // do lado esquerdo do círculo inferior" — e2 é a transformação
            // resultante (círculo inferior, ver ordem de elementosVergnaud
            // em COMPOSICAO_TRANSFORMACOES).
            centroX = left(e2) - DESLOCAMENTO_ESQUERDA_ESTADO_TRANSFORMACAO;
            centroY = centroY(e2);
        } else {
            ConectorVergnaud conectorParaRelacaoFinal = (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                    || tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES)
                    && conectores != null && !conectores.isEmpty() ? conectores.get(0) : null;

            if (conectorParaRelacaoFinal != null) {
                // Ponto médio da seta (sem alvo, Transformação de Relação) ou
                // da haste da chave até a relação final (com alvo,
                // Composição de Relações) — mesmo segmento que o diagrama já
                // desenha.
                int meioX = (conectorParaRelacaoFinal.x1 + conectorParaRelacaoFinal.x2) / 2;
                int meioY = (conectorParaRelacaoFinal.y1 + conectorParaRelacaoFinal.y2) / 2;
                if (conectorParaRelacaoFinal.temAlvo()) {
                    // Chave vertical: o traço real fica DESLOCAMENTO_TRACO_CHAVE
                    // à direita do x1/x2 armazenado (ver desenharChaveVertical).
                    meioX += DESLOCAMENTO_TRACO_CHAVE;
                    centroX = (meioX + conectorParaRelacaoFinal.xAlvo) / 2;
                    centroY = (meioY + conectorParaRelacaoFinal.yAlvo) / 2;
                } else {
                    centroX = meioX;
                    centroY = meioY;
                }
                // Acima do segmento, não sobre ele.
                centroY -= ELEVACAO_ACIMA_DO_SEGMENTO;
            } else {
                // Composição de Transformações, primeira operação (entre as
                // duas transformações de entrada): "radiobutton de soma e
                // subtração entre transformação a cima dos dois círculos
                // superiores" — acima de e0/e1 (t1/t2), não no vão abaixo
                // deles.
                centroX = (centroX(e0) + centroX(e1)) / 2;
                centroY = Math.min(top(e0), top(e1)) - ELEVACAO_ACIMA_DO_SEGMENTO;
            }
        }

        areaSoma = new Rectangle(centroX - ESPACAMENTO_BOTOES / 2 - RAIO_BOTAO,
                centroY - RAIO_BOTAO, RAIO_BOTAO * 2, RAIO_BOTAO * 2);
        areaSubtracao = new Rectangle(centroX + ESPACAMENTO_BOTOES / 2 - RAIO_BOTAO,
                centroY - RAIO_BOTAO, RAIO_BOTAO * 2, RAIO_BOTAO * 2);
        ativo = true;
    }

    /**
     * Translada a posição já calculada por (dx,dy) — chamado quando a janela
     * é redimensionada e o diagrama inteiro (elementosVergnaud,
     * conectoresVergnaud) é deslocado sem ser reconstruído do zero. Sem
     * isso, o seletor ficava para trás, "fixo", enquanto o resto do
     * diagrama acompanhava a nova área — o mesmo padrão já usado para
     * ElementoVergnaud/ConectorVergnaud em reposicionarDiagramaVergnaudParaAreaAtua().
     */
    public void reposicionar(int dx, int dy) {
        if (!ativo || (dx == 0 && dy == 0)) {
            return;
        }
        centroX += dx;
        centroY += dy;
        if (areaSoma != null) {
            areaSoma.translate(dx, dy);
        }
        if (areaSubtracao != null) {
            areaSubtracao.translate(dx, dy);
        }
    }

    private static int centroX(ElementoVergnaud e) {
        return e.x + e.largura / 2;
    }

    private static int centroY(ElementoVergnaud e) {
        return e.y + e.altura / 2;
    }

    private static int left(ElementoVergnaud e) {
        return e.x;
    }

    private static int top(ElementoVergnaud e) {
        return e.y;
    }

    /**
     * Testa o clique contra os dois botões. Se atingir um deles, registra a
     * escolha, decide se mostra a explicação (só quando errado — quando
     * certo, fica silencioso, mesma convenção já usada para o sinal do
     * número relativo) e devolve true (consumiu o clique). Quem chama é
     * responsável por tocar o som de erro (ver
     * ScaffoldingFeedbackMultissensorialErro.emitirApenasSom) quando o
     * retorno indicar escolha errada — ver {@link #respondeuCorretamente()}.
     */
    public boolean processarPressionamento(int mouseX, int mouseY) {
        if (!ativo) {
            return false;
        }
        if (areaSoma != null && areaSoma.contains(mouseX, mouseY)) {
            registrarEscolha(OpcaoOperacaoCuradoria.SOMA);
            return true;
        }
        if (areaSubtracao != null && areaSubtracao.contains(mouseX, mouseY)) {
            registrarEscolha(OpcaoOperacaoCuradoria.SUBTRACAO);
            return true;
        }
        return false;
    }

    private void registrarEscolha(OpcaoOperacaoCuradoria escolha) {
        escolhaAluno = escolha;
        mostrarExplicacao = escolha != escolhaCorreta;
    }

    public boolean contem(int mouseX, int mouseY) {
        return ativo
                && ((areaSoma != null && areaSoma.contains(mouseX, mouseY))
                || (areaSubtracao != null && areaSubtracao.contains(mouseX, mouseY)));
    }

    public void desenhar(Graphics2D g2, ServicoLocalizacao localizacao) {
        if (!ativo || g2 == null || areaSoma == null || areaSubtracao == null) {
            return;
        }
        ServicoLocalizacao loc = localizacao == null ? ServicoLocalizacao.getInstancia() : localizacao;
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int baseRotuloSoma = desenharBotao(g, areaSoma,
                    loc.texto("curadoria.operacao.soma"),
                    escolhaAluno == OpcaoOperacaoCuradoria.SOMA);
            int baseRotuloSubtracao = desenharBotao(g, areaSubtracao,
                    loc.texto("curadoria.operacao.subtracao"),
                    escolhaAluno == OpcaoOperacaoCuradoria.SUBTRACAO);

            if (mostrarExplicacao && textoExplicacaoCorreta != null && !textoExplicacaoCorreta.isEmpty()) {
                int baseRotulos = Math.max(baseRotuloSoma, baseRotuloSubtracao);
                desenharExplicacao(g, centroX, baseRotulos + ESPACO_ROTULO_EXPLICACAO);
            }
        } finally {
            g.dispose();
        }
    }

    private int desenharBotao(Graphics2D g, Rectangle area, String rotulo, boolean selecionado) {
        boolean marcadoComoErrado = mostrarExplicacao && selecionado;
        Color corBorda = marcadoComoErrado ? gerard.ui.UITemaGerard.COR_ERRO
                : (selecionado ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_BORDA);
        g.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
        g.fillOval(area.x, area.y, area.width, area.height);
        g.setColor(corBorda);
        g.setStroke(new BasicStroke(selecionado ? 2.2f : 1.2f));
        g.drawOval(area.x, area.y, area.width, area.height);
        if (selecionado) {
            int folga = 4;
            g.fillOval(area.x + folga, area.y + folga, area.width - folga * 2, area.height - folga * 2);
        }

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
        FontMetrics fm = g.getFontMetrics();
        String sinal = primeiroToken(rotulo);
        String nomeOperacao = restanteDoRotulo(rotulo);
        int centroDoBotao = area.x + area.width / 2;
        int ySinal = area.y + area.height + fm.getAscent() + ESPACO_BOTAO_SINAL;
        int xSinal = centroDoBotao - fm.stringWidth(sinal) / 2;
        g.drawString(sinal, xSinal, ySinal);
        int yNome = ySinal + fm.getHeight() + ESPACO_ENTRE_SINAL_E_NOME;
        int xNome = centroDoBotao - fm.stringWidth(nomeOperacao) / 2;
        g.drawString(nomeOperacao, xNome, yNome);
        return yNome + fm.getDescent();
    }

    private static String primeiroToken(String rotulo) {
        String texto = rotulo == null ? "" : rotulo.trim();
        int separador = texto.indexOf(' ');
        return separador < 0 ? "" : texto.substring(0, separador);
    }

    private static String restanteDoRotulo(String rotulo) {
        String texto = rotulo == null ? "" : rotulo.trim();
        int separador = texto.indexOf(' ');
        return separador < 0 ? texto : texto.substring(separador + 1).trim();
    }

    private void desenharExplicacao(Graphics2D g, int x, int y) {
        Font fonte = new Font("Arial", Font.PLAIN, 12);
        g.setFont(fonte);
        FontMetrics fm = g.getFontMetrics();
        List<String> linhas = quebrarLinhas(textoExplicacaoCorreta, fm, LARGURA_EXPLICACAO);
        int alturaLinha = fm.getHeight();
        int alturaCaixa = alturaLinha * linhas.size() + 14;
        int larguraCaixa = LARGURA_EXPLICACAO + 16;
        int caixaX = x - larguraCaixa / 2;
        int caixaY = y;

        g.setColor(gerard.ui.UITemaGerard.COR_ERRO_FUNDO);
        g.fillRoundRect(caixaX, caixaY, larguraCaixa, alturaCaixa, 10, 10);
        g.setColor(gerard.ui.UITemaGerard.COR_ERRO);
        g.setStroke(new BasicStroke(1.1f));
        g.drawRoundRect(caixaX, caixaY, larguraCaixa, alturaCaixa, 10, 10);

        g.setColor(gerard.ui.UITemaGerard.COR_ERRO_TEXTO);
        int textoY = caixaY + fm.getAscent() + 7;
        for (String linha : linhas) {
            int textoX = x - fm.stringWidth(linha) / 2;
            g.drawString(linha, textoX, textoY);
            textoY += alturaLinha;
        }
    }

    private static List<String> quebrarLinhas(String texto, FontMetrics fm, int larguraMaxima) {
        List<String> linhas = new java.util.ArrayList<String>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : texto.split("\\s+")) {
            String candidata = atual.length() == 0 ? palavra : atual + " " + palavra;
            if (fm.stringWidth(candidata) > larguraMaxima && atual.length() > 0) {
                linhas.add(atual.toString());
                atual = new StringBuilder(palavra);
            } else {
                atual = new StringBuilder(candidata);
            }
        }
        if (atual.length() > 0) {
            linhas.add(atual.toString());
        }
        return linhas;
    }
}
