package gerard.interacao.texto;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import java.awt.FontMetrics;
import java.util.List;

/**
 * Resolve o elemento matemático selecionado no enunciado.
 *
 * A busca usa primeiro os marcadores semânticos já calculados. Como proteção
 * contra diferenças entre o contorno desenhado e a área de pickup, reconstrói
 * o marcador diretamente a partir do vínculo semântico do texto quando
 * necessário. Números e incógnita passam pelo mesmo caminho.
 */
public final class ResolvedorPickupElementoMatematicoTexto {

    private final PoliticaElementoMatematicoTexto politica;

    public ResolvedorPickupElementoMatematicoTexto(PoliticaElementoMatematicoTexto politica) {
        this.politica = politica == null ? new PoliticaElementoMatematicoTexto() : politica;
    }

    public MarcadorTexto encontrar(int x, int y,
            List<MarcadorTexto> marcadores,
            List<ElementoTextoMovel> elementos,
            FontMetrics metricas) {
        MarcadorTexto marcador = encontrarMarcadorMaisProximo(x, y, marcadores);
        if (marcador != null) {
            return normalizarIncognita(marcador);
        }
        return reconstruirAPartirDoTexto(x, y, elementos, metricas);
    }

    private MarcadorTexto encontrarMarcadorMaisProximo(int x, int y,
            List<MarcadorTexto> marcadores) {
        if (marcadores == null) {
            return null;
        }
        MarcadorTexto melhor = null;
        long melhorDistancia = Long.MAX_VALUE;
        for (int i = marcadores.size() - 1; i >= 0; i--) {
            MarcadorTexto candidato = marcadores.get(i);
            if (candidato == null || !candidato.contem(x, y)) {
                continue;
            }
            String valor = candidato.editavel ? "?" : candidato.valor;
            if (!politica.deveValidar(valor, candidato.chavePapel)) {
                continue;
            }
            long dx = x - (candidato.x + candidato.largura / 2);
            long dy = y - (candidato.y + candidato.altura / 2);
            long distancia = dx * dx + dy * dy;
            if (melhor == null || distancia < melhorDistancia) {
                melhor = candidato;
                melhorDistancia = distancia;
            }
        }
        return melhor;
    }

    private MarcadorTexto reconstruirAPartirDoTexto(int x, int y,
            List<ElementoTextoMovel> elementos,
            FontMetrics metricas) {
        if (elementos == null || metricas == null) {
            return null;
        }
        for (int i = elementos.size() - 1; i >= 0; i--) {
            ElementoTextoMovel elemento = elementos.get(i);
            if (elemento == null || !elemento.contem(x, y)
                    || !elemento.possuiVinculoSemantico()) {
                continue;
            }
            String valor = elemento.representaIncognitaOriginal()
                    ? "?" : elemento.getValorSemanticoAtual();
            String papel = elemento.getChavePapelSemantico();
            if (!politica.deveValidar(valor, papel)) {
                continue;
            }

            int inicio = Math.max(0, Math.min(
                    elemento.getInicioSemanticoAtual(), elemento.valor.length()));
            String prefixo = elemento.valor.substring(0, inicio);
            int xMarcador = elemento.x + metricas.stringWidth(prefixo) - 3;
            int largura = Math.max(metricas.stringWidth(valor), 8) + 8;
            int altura = Math.max(18, metricas.getHeight() - 5);
            int yMarcador = elemento.y - metricas.getAscent() + 3;
            MarcadorTexto reconstruido = new MarcadorTexto(
                    xMarcador, yMarcador, largura, altura,
                    valor, elemento.representaIncognitaOriginal(), papel);
            if (reconstruido.contem(x, y)) {
                return reconstruido;
            }
        }
        return null;
    }

    private MarcadorTexto normalizarIncognita(MarcadorTexto marcador) {
        if (marcador == null || !marcador.editavel || "?".equals(marcador.valor)) {
            return marcador;
        }
        return new MarcadorTexto(
                marcador.x, marcador.y, marcador.largura, marcador.altura,
                "?", true, marcador.chavePapel);
    }
}
