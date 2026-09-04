import type { ElementoTexto } from "./contratos";

/**
 * Enunciado com os elementos vinculados a um papel semântico marcados e
 * arrastáveis — mesmo recurso do desktop (Main.java: elementosTexto +
 * vincularPapeisSemanticosAosElementosTexto + desenharMarcadoresFixosDoTexto),
 * agora com os tokens/posições calculados no servidor
 * (SegmentadorTextoSemantico, portátil). O marcador (borda tracejada +
 * leve fundo) é o mesmo para qualquer papel — corFundoDoPapel/
 * corBordaDoPapel/corTextoDoPapel no desktop hoje devolvem sempre a mesma
 * cor fixa (COR_SUPERFICIE/COR_BORDA/COR_TEXTO), não uma cor por papel.
 */
export function EnunciadoInterativo({ elementos }: {
  elementos: readonly ElementoTexto[];
}) {
  return <h1 id="enunciado">
    {elementos.map((elemento, indice) => {
      const espaco = indice > 0 ? " " : "";
      if (!elemento.papel_id) {
        return <span key={indice}>{espaco}{elemento.valor}</span>;
      }
      const papelId = elemento.papel_id;
      return <span key={indice}>{espaco}<span
        className="enunciado-elemento-semantico"
        draggable
        onDragStart={(evento) => {
          evento.dataTransfer.setData("text/plain", papelId);
          evento.dataTransfer.effectAllowed = "copy";
        }}
        aria-label={elemento.incognita ? "Incógnita — arraste até o diagrama" : "Valor conhecido"}
      >{elemento.valor}</span></span>;
    })}
  </h1>;
}
