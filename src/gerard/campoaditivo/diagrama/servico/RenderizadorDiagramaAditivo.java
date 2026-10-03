package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.ArgumentosHistorinhaCena;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Textos da historinha desta categoria, no idioma recebido. Método polimórfico só de
     * renderização: não decide se há historinha nem qual ilustração (isso é do backend,
     * independente de idioma); apenas entrega à cena o texto correto como argumento. A fonte
     * é o trecho de texto curado da própria situação (fragmento_texto_1..6), que existe por
     * idioma; sem trechos curados no idioma pedido, a lista é vazia: nunca se inventa texto.
     */
    default ArgumentosHistorinhaCena argumentosHistorinha(SituacaoProblemaAditiva situacao, String idioma) {
        if (situacao == null || idioma == null || !idioma.equalsIgnoreCase(situacao.getCodigoIdioma())) {
            return ArgumentosHistorinhaCena.VAZIO;
        }
        List<String> trechos = new ArrayList<String>();
        String[] candidatos = {situacao.getFragmentoTexto1(), situacao.getFragmentoTexto2(),
                situacao.getFragmentoTexto3(), situacao.getFragmentoTexto4(),
                situacao.getFragmentoTexto5(), situacao.getFragmentoTexto6()};
        for (String trecho : candidatos) {
            if (trecho != null && !trecho.trim().isEmpty()) {
                trechos.add(trecho.trim());
            }
        }
        return new ArgumentosHistorinhaCena(idioma, trechos);
    }
}
