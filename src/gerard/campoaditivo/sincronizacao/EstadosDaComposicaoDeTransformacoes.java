package gerard.campoaditivo.sincronizacao;

import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Os estados que acompanham as transformações na Composição de Transformações. A relação estrutural da categoria liga só as
 * três transformações (resultante = 1 + 2); o diagrama tem também os estados, e eles seguem a mesma lógica da
 * Transformação de Medidas: o estado intermediário é o inicial mais a transformação 1, e o final é o inicial mais a
 * transformação resultante. Quando a pessoa explora, mudando uma transformação, os estados têm que acompanhar — e a mudança
 * só vale se cada estado continua no seu domínio (os estados são naturais: uma transformação que os deixaria negativos não é
 * aceita).
 */
public final class EstadosDaComposicaoDeTransformacoes {
    public static final String ESTADO_INICIAL = "papel.estadoInicial";
    public static final String ESTADO_INTERMEDIARIO = "papel.estadoIntermediario";
    public static final String ESTADO_FINAL = "papel.estadoFinal";

    private EstadosDaComposicaoDeTransformacoes() {
    }

    /** As chaves dos estados que acompanham as transformações (para a relação exploratória as publicar). */
    public static String[] chavesDosEstados() {
        return new String[] {ESTADO_INTERMEDIARIO, ESTADO_FINAL};
    }

    /**
     * @param estadoInicial o estado inicial atual (null se a situação não o tem)
     * @param transformacao1 a transformação 1 depois da mudança
     * @param transformacaoResultante a transformação resultante depois da mudança
     * @param temIntermediario se o estado intermediário está posicionado no diagrama (só então ele acompanha)
     * @return os estados recalculados, por chave; um mapa vazio se não há estado inicial; {@code null} se algum estado
     *         sairia do seu domínio
     */
    public static Map<String, Integer> recalcular(Integer estadoInicial, Integer transformacao1,
            Integer transformacaoResultante, boolean temIntermediario) {
        Map<String, Integer> estados = new LinkedHashMap<String, Integer>();
        if (estadoInicial == null) {
            return estados;
        }
        PublicadorEventoDominio semEventos = PublicadorEventoDominio.NENHUM;
        if (temIntermediario && transformacao1 != null) {
            int intermediario = estadoInicial.intValue() + transformacao1.intValue();
            PapelQuantitativo papel = FabricaPapeisComposicaoDeTransformacoes.estadoIntermediario(semEventos);
            if (!papel.aceita(new NumeroInteiro(intermediario))) {
                return null;
            }
            estados.put(ESTADO_INTERMEDIARIO, Integer.valueOf(intermediario));
        }
        if (transformacaoResultante != null) {
            int estadoFinal = estadoInicial.intValue() + transformacaoResultante.intValue();
            PapelQuantitativo papel = FabricaPapeisComposicaoDeTransformacoes.estadoFinal(semEventos);
            if (!papel.aceita(new NumeroInteiro(estadoFinal))) {
                return null;
            }
            estados.put(ESTADO_FINAL, Integer.valueOf(estadoFinal));
        }
        return estados;
    }
}
