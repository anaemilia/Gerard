package gerard.campoaditivo.transformacao.processo;

import gerard.i18n.ServicoLocalizacao;

/** Resolve os rótulos do processo exclusivamente pela localização ativa. */
public final class RotulosProcessoTransformacao {

    public String obterEtapa(EstadoProcessoTransformacao estado,
            ServicoLocalizacao localizacao) {
        ServicoLocalizacao loc = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        if (estado == null || !estado.isConhecido(1)
                || estado.getTipoProcesso() == TipoProcessoTransformacao.NEUTRA) {
            return "";
        }
        return estado.getTipoProcesso() == TipoProcessoTransformacao.RETIRADA
                ? loc.texto("ui.transformationBoard.out")
                : loc.texto("ui.transformationBoard.in");
    }
}
