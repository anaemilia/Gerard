package gerard.campoaditivo.diagrama.servico;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

/**
 * Duração de um laço da animação da historinha, lida do próprio GIF (soma dos atrasos dos quadros),
 * para que o cronograma das legendas acompanhe a animação real em qualquer plataforma. Resultado em
 * cache; referência desconhecida ou GIF ilegível = 0 (sem legendas, nunca um valor inventado).
 */
public final class DuracaoAnimacaoHistorinha {
    private static final String RAIZ_RECURSOS = "/gerard/recursos/ajuda/";
    private static final Map<String, Double> CACHE = new ConcurrentHashMap<String, Double>();

    private DuracaoAnimacaoHistorinha() {
    }

    public static double segundos(String referencia) {
        if (referencia == null || referencia.trim().isEmpty()) {
            return 0.0;
        }
        Double conhecido = CACHE.get(referencia);
        if (conhecido == null) {
            conhecido = Double.valueOf(ler(referencia));
            CACHE.put(referencia, conhecido);
        }
        return conhecido.doubleValue();
    }

    private static double ler(String referencia) {
        try (InputStream recurso = DuracaoAnimacaoHistorinha.class.getResourceAsStream(
                RAIZ_RECURSOS + referencia + ".gif")) {
            if (recurso == null) {
                return 0.0;
            }
            Iterator<ImageReader> leitores = ImageIO.getImageReadersByFormatName("gif");
            if (!leitores.hasNext()) {
                return 0.0;
            }
            ImageReader leitor = leitores.next();
            try (ImageInputStream entrada = ImageIO.createImageInputStream(recurso)) {
                leitor.setInput(entrada, false);
                int quadros = leitor.getNumImages(true);
                long centesimos = 0;
                for (int i = 0; i < quadros; i++) {
                    IIOMetadata metadados = leitor.getImageMetadata(i);
                    Node raiz = metadados.getAsTree("javax_imageio_gif_image_1.0");
                    for (Node no = raiz.getFirstChild(); no != null; no = no.getNextSibling()) {
                        if ("GraphicControlExtension".equals(no.getNodeName())) {
                            centesimos += Long.parseLong(
                                    no.getAttributes().getNamedItem("delayTime").getNodeValue());
                        }
                    }
                }
                return centesimos / 100.0;
            } finally {
                leitor.dispose();
            }
        } catch (Exception ilegivel) {
            return 0.0;
        }
    }
}
