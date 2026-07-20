package gerard.interacao.texto;

import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.awt.Rectangle;

/**
 * Descritor imutável de um elemento matemático imerso no texto.
 *
 * O descritor separa o conteúdo semântico da projeção visual do enunciado.
 * Assim, número, número sinalizado, decimal ou incógnita permanecem no texto,
 * enquanto um proxy temporário pode ser arrastado até outra representação.
 */
public final class ElementoMatematicoImersoTexto {
    private final String tokenId;
    private final String valorExibido;
    private final String valorSemanticoOriginal;
    private final String chavePapelSemantico;
    private final boolean editavel;
    private final TipoElementoMatematicoTexto tipo;
    private final Rectangle areaOrigem;

    public ElementoMatematicoImersoTexto(
            String tokenId,
            String valorExibido,
            String valorSemanticoOriginal,
            String chavePapelSemantico,
            boolean editavel,
            TipoElementoMatematicoTexto tipo,
            Rectangle areaOrigem) {
        this.tokenId = limpar(tokenId);
        this.valorExibido = limpar(valorExibido);
        this.valorSemanticoOriginal = limpar(valorSemanticoOriginal);
        this.chavePapelSemantico = limpar(chavePapelSemantico);
        this.editavel = editavel;
        this.tipo = tipo == null ? TipoElementoMatematicoTexto.OUTRO : tipo;
        this.areaOrigem = areaOrigem == null ? new Rectangle() : new Rectangle(areaOrigem);
    }

    public static ElementoMatematicoImersoTexto aPartirDe(MarcadorTexto marcador) {
        if (marcador == null) {
            return null;
        }
        String valor = limpar(marcador.valor);
        boolean incognita = SimboloDesconhecido.eh(valor) || marcador.editavel;
        TipoElementoMatematicoTexto tipo = incognita
                ? TipoElementoMatematicoTexto.INCOGNITA
                : TipoElementoMatematicoTexto.NUMERO;
        String original = incognita ? "?" : valor;
        String papel = limpar(marcador.chavePapel);
        String token = papel + "|" + original + "|" + marcador.x + "|" + marcador.y;
        return new ElementoMatematicoImersoTexto(
                token,
                valor,
                original,
                papel,
                marcador.editavel,
                tipo,
                new Rectangle(marcador.x, marcador.y, marcador.largura, marcador.altura));
    }

    public String getTokenId() { return tokenId; }
    public String getValorExibido() { return valorExibido; }
    public String getValorSemanticoOriginal() { return valorSemanticoOriginal; }
    public String getChavePapelSemantico() { return chavePapelSemantico; }
    public boolean isEditavel() { return editavel; }
    public TipoElementoMatematicoTexto getTipo() { return tipo; }
    public Rectangle getAreaOrigem() { return new Rectangle(areaOrigem); }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
