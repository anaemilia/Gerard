package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;

public interface RenderizadorDiagramaAditivo {
    CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores);

    /**
     * Cena do material concreto (grupos de quadradinhos) desta categoria —
     * mesmo modelo de figuras/conectores da cena abstrata, gerado pelo
     * mesmo renderizador, nunca por um componente de interface à parte.
     * {@code null} quando esta categoria ainda não tem o material concreto
     * verificado contra o desktop (ver GeradorCenaDiagramaAditivo.
     * direcaoDeslocamentoParaMaterialConcreto para o mesmo cuidado) — nunca
     * inventar uma geometria só para preencher todas as categorias.
     */
    default CenaDiagramaAditivo criarCenaMaterialConcreto(AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores, String chavePapelAlvo) {
        return null;
    }
}
