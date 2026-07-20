package gerard.ui.enunciado;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Representa a área de título da categoria exibida acima do enunciado,
 * incluindo o desenho do texto e o cálculo da área de hit-test usada pelo
 * mouseover (tooltip com a descrição da categoria/subtipo).
 *
 * Extraído de Main.TelaGerard (métodos originais
 * desenharTextoCategoriaNaAreaDoEnunciado e pontoNoTituloCategoriaEnunciado)
 * como primeiro passo da Fase 1 do plano de refatoração — ver
 * PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * A lógica de desenho e de cálculo do retângulo é idêntica à original;
 * apenas os dados que antes vinham de campos/métodos de TelaGerard agora
 * são recebidos como parâmetros de desenhar(...).
 */
public final class AreaTituloCategoriaEnunciado {

    private Rectangle area;
    private String descricao = "";

    /** Limpa o estado (equivalente ao caso definicaoDiagramaAtual == null no original). */
    public void limpar() {
        area = null;
        descricao = "";
    }

    /**
     * Desenha o título da categoria (e, quando aplicável, uma linha de resumo
     * abaixo dele) e recalcula a área de hit-test para o tooltip.
     *
     * @param g2 contexto gráfico (cor e fonte serão alterados por este método,
     *           igual ao comportamento original)
     * @param margemX margem esquerda onde o texto começa
     * @param textoCategoria título da categoria (definicaoDiagramaAtual.getTitulo())
     * @param descricaoCategoria descrição da cena atual (cenaDiagramaAtual.getDescricao()), pode ser nula
     * @param descricaoSubtipo descrição do subtipo Vergnaud atual (obterDescricaoSubtipoAtual()), pode ser nula
     * @param corPrimariaEscura cor usada para o título (COR_PRIMARIA_ESCURA)
     * @param linhaResumoAbaixoDoTitulo texto da segunda linha (resumo de passos da
     *        transformação composta, ou texto de composição de medidas), ou null
     *        se nenhuma das duas condições originais se aplica
     */
    public void desenhar(Graphics2D g2, int margemX,
                          String textoCategoria,
                          String descricaoCategoria,
                          String descricaoSubtipo,
                          Color corPrimariaEscura,
                          String linhaResumoAbaixoDoTitulo) {

        int yTitulo = 76;

        g2.setColor(corPrimariaEscura);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fmTitulo = g2.getFontMetrics();
        g2.drawString(textoCategoria, margemX, yTitulo);

        if (linhaResumoAbaixoDoTitulo != null) {
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(new Color(92, 104, 108));
            g2.drawString(linhaResumoAbaixoDoTitulo, margemX, yTitulo + 14);
        }

        area = new Rectangle(
                margemX,
                yTitulo - fmTitulo.getAscent(),
                fmTitulo.stringWidth(textoCategoria),
                fmTitulo.getHeight()
        );

        if (descricaoCategoria == null) {
            descricaoCategoria = "";
        }
        if (descricaoSubtipo != null && descricaoSubtipo.trim().length() > 0) {
            descricao = descricaoCategoria + " " + descricaoSubtipo;
        } else {
            descricao = descricaoCategoria;
        }
    }

    /** Equivalente ao pontoNoTituloCategoriaEnunciado(x, y) original. */
    public boolean contemPonto(int x, int y) {
        return area != null && area.contains(x, y)
                && descricao != null && descricao.trim().length() > 0;
    }

    /** Descrição a exibir no tooltip quando contemPonto(...) retornar true. */
    public String getDescricao() {
        return descricao;
    }
}
