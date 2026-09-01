import type { ConectorCena } from "../contratos";
import { caminhoDoConector } from "./geometriaSvg";

export function ConectorCenaGerard({ conector, indice }: { conector: ConectorCena; indice: number }) {
  const temSeta = conector.tipo === "SETA" || conector.tipo === "SETA_CURVA";
  return <g className="scene-connector">
    <path d={caminhoDoConector(conector)} markerEnd={temSeta ? "url(#seta-cena-gerard)" : undefined} />
    {conector.legenda && <text x={(conector.x1 + conector.x2) / 2}
      y={(conector.y1 + conector.y2) / 2 - 8} data-conector={indice}>{conector.legenda}</text>}
  </g>;
}
