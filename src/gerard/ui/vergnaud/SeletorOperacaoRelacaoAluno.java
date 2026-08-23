package gerard.ui.vergnaud;

import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
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
 */
public final class SeletorOperacaoRelacaoAluno {

    private static final int RAIO_BOTAO = 9;
    private static final int ESPACAMENTO_BOTOES = 92;
    private static final int LARGURA_EXPLICACAO = 360;
    private static final int DESLOCAMENTO_VERTICAL_PADRAO = RAIO_BOTAO * 6;
    private static final int DISTANCIA_VERTICAL_DA_SETA = RAIO_BOTAO * 4;
    private static final int ESPACO_BOTAO_SINAL = 4;
    private static final int ESPACO_ENTRE_SINAL_E_NOME = 1;
    private static final int ESPACO_ROTULO_EXPLICACAO = 10;

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
        return ativo && escolhaAluno != OpcaoOperacaoCuradoria.NAO_SELECIONADO
                && escolhaAluno == escolhaCorreta;
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
        return tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                || tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES
                || tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
    }

    /**
     * Ativa o seletor para a situação atual. Em Transformação de Relação, a
     * posição deriva da linha da seta entre a relação inicial e a final. Nas
     * demais categorias, preserva o centróide dos 3 primeiros elementos — os
     * 3 papéis da categoria, já com o deslocamento de centralização aplicado.
     * Em ambos os casos usa a geometria dos elementos que o diagrama já
     * possui, sem duplicar coordenadas de layout.
     */
    public void ativar(TipoSituacaoAditiva tipo, SituacaoProblemaAditiva situacao,
            List<ElementoVergnaud> elementos, ServicoLocalizacao localizacao) {
        desativar();
        if (!aplicavel(tipo) || situacao == null || elementos == null || elementos.size() < 3) {
            return;
        }
        ServicoLocalizacao loc = localizacao == null ? ServicoLocalizacao.getInstancia() : localizacao;
        escolhaCorreta = OpcaoOperacaoCuradoria.aPartirDoEstado(situacao.getOperacaoRelacao());
        if (!escolhaCorreta.isEscolhaValida()) {
            // Situação sem operação curada (base antiga) — nada a perguntar.
            escolhaCorreta = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
            return;
        }

        String chaveExplicacao = chaveExplicacao(tipo, escolhaCorreta);
        textoExplicacaoCorreta = chaveExplicacao == null ? ""
                : preencherPersonagensCurados(loc.texto(chaveExplicacao), situacao);

        ElementoVergnaud e0 = elementos.get(0);
        ElementoVergnaud e1 = elementos.get(1);
        ElementoVergnaud e2 = elementos.get(2);
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            // A seta liga a relação inicial à relação final. O seletor fica
            // centralizado e afastado a partir dessa geometria real.
            centroX = (centroX(e0) + centroX(e2)) / 2;
            int yDaSeta = (centroY(e0) + centroY(e2)) / 2;
            centroY = yDaSeta + DISTANCIA_VERTICAL_DA_SETA;
        } else {
            // Preserva o posicionamento já usado pelas demais categorias.
            centroX = (centroX(e0) + centroX(e1) + centroX(e2)) / 3;
            centroY = (centroY(e0) + centroY(e1) + centroY(e2)) / 3
                    + DESLOCAMENTO_VERTICAL_PADRAO;
        }

        areaSoma = new Rectangle(centroX - ESPACAMENTO_BOTOES / 2 - RAIO_BOTAO,
                centroY - RAIO_BOTAO, RAIO_BOTAO * 2, RAIO_BOTAO * 2);
        areaSubtracao = new Rectangle(centroX + ESPACAMENTO_BOTOES / 2 - RAIO_BOTAO,
                centroY - RAIO_BOTAO, RAIO_BOTAO * 2, RAIO_BOTAO * 2);
        ativo = true;
    }

    private static String textoOu(String valor) {
        return valor == null ? "" : valor;
    }

    private static String preencherPersonagensCurados(String modelo,
            SituacaoProblemaAditiva situacao) {
        String personagem1 = textoOu(situacao.getPersonagem1());
        String personagem2 = textoOu(situacao.getPersonagem2());
        String personagem3 = textoOu(situacao.getPersonagem3());
        return textoOu(modelo)
                .replace("{Personagem_1}", personagem1)
                .replace("{Personagem_2}", personagem2)
                .replace("{Personagem_3}", personagem3);
    }

    private static String chaveExplicacao(TipoSituacaoAditiva tipo, OpcaoOperacaoCuradoria operacao) {
        boolean soma = operacao == OpcaoOperacaoCuradoria.SOMA;
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return soma ? "operacao.explicacao.transformacaoRelacao.soma"
                    : "operacao.explicacao.transformacaoRelacao.subtracao";
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return soma ? "operacao.explicacao.composicaoRelacoes.soma"
                    : "operacao.explicacao.composicaoRelacoes.subtracao";
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return soma ? "operacao.explicacao.composicaoTransformacoes.soma"
                    : "operacao.explicacao.composicaoTransformacoes.subtracao";
        }
        return null;
    }

    private static int centroX(ElementoVergnaud e) {
        return e.x + e.largura / 2;
    }

    private static int centroY(ElementoVergnaud e) {
        return e.y + e.altura / 2;
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
