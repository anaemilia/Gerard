package gerard.dominio.campoaditivo;

import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;

/**
 * Descrição abstrata de como um PapelQuantitativo se apresenta visualmente —
 * forma e chave de rótulo, nunca pixels, cor ou fonte. Quem transforma isto
 * em desenho é a camada de representação (fora deste pacote); este objeto só
 * responde "que forma sou, que texto me identifica", nunca "como me desenho"
 * — é essa distinção que mantém o domínio livre de AWT/Swing (achado V2 da
 * auditoria Domain Model First) e ainda assim "sabendo" sua representação
 * gráfica, como a skill pede.
 *
 * Reaproveita TipoFiguraDiagrama (gerard.campoaditivo.diagrama.modelo), que
 * já não depende de AWT/Swing hoje — nenhuma duplicação de enum.
 */
public final class RepresentacaoGraficaPapel {

    private final TipoFiguraDiagrama forma;
    private final String chaveRotulo;

    public RepresentacaoGraficaPapel(TipoFiguraDiagrama forma, String chaveRotulo) {
        if (forma == null) {
            throw new IllegalArgumentException("forma não pode ser nula");
        }
        this.forma = forma;
        this.chaveRotulo = chaveRotulo == null ? "" : chaveRotulo;
    }

    public TipoFiguraDiagrama getForma() { return forma; }
    public String getChaveRotulo() { return chaveRotulo; }
}
