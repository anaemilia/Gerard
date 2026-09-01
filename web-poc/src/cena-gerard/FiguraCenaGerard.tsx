import type { FiguraCena } from "../contratos";
import { coordenadaYDoRotulo } from "./geometriaSvg";

function LupaCenaGerard({ figura }: { figura: FiguraCena }) {
  if (!figura.exibir_lupa) return null;
  return <g className={`scene-magnifier${figura.lupa_habilitada ? " scene-magnifier-enabled" : ""}`}
      transform={`translate(${figura.x + figura.largura + 12} ${figura.y + 4})`}
      aria-label="Eixo numérico em desenvolvimento">
    <circle cx="8" cy="8" r="7"/><path d="M 13 13 L 19 19"/><circle cx="8" cy="8" r="2"/>
  </g>;
}

export function FiguraCenaGerard({ figura }: { figura: FiguraCena }) {
  return <g className="scene-figure" data-figura-id={figura.id}>
    {figura.tipo === "ELIPSE"
      ? <ellipse cx={figura.x + figura.largura / 2} cy={figura.y + figura.altura / 2}
          rx={figura.largura / 2} ry={figura.altura / 2} />
      : <rect x={figura.x} y={figura.y} width={figura.largura} height={figura.altura}
          rx={figura.tipo === "RETANGULO_ARREDONDADO" ? 10 : 0} />}
    <text x={figura.x + figura.largura / 2} y={coordenadaYDoRotulo(figura)}>{figura.rotulo}</text>
    {figura.subtitulo && <text className="scene-figure-subtitle"
      x={figura.x + figura.largura / 2} y={figura.y + figura.altura + 24}>{figura.subtitulo}</text>}
    <LupaCenaGerard figura={figura} />
  </g>;
}
