import type { FiguraCena, InteracaoPermitidaFigura } from "../contratos";
import { coordenadaYDoRotulo, coordenadaYDoSubtitulo } from "./geometriaSvg";

function LupaCenaGerard({ figura }: { figura: FiguraCena }) {
  if (!figura.exibir_lupa) return null;
  return <g className={`scene-magnifier${figura.lupa_habilitada ? " scene-magnifier-enabled" : ""}`}
      transform={`translate(${figura.x + figura.largura + 12} ${figura.y + 4})`}
      aria-label="Eixo numérico em desenvolvimento">
    <circle cx="8" cy="8" r="7"/><path d="M 13 13 L 19 19"/><circle cx="8" cy="8" r="2"/>
  </g>;
}

export function FiguraCenaGerard({ figura, aoEditarValor, destacada }: {
  figura: FiguraCena;
  aoEditarValor?: (figura: FiguraCena, interacao: InteracaoPermitidaFigura) => void;
  destacada?: boolean;
}) {
  const interacao = figura.interacoes_permitidas.find(
    (item) => item.tipo === "EDITAR_VALOR");
  const editavel = Boolean(interacao && aoEditarValor);
  const iniciarEdicao = () => {
    if (interacao && aoEditarValor) aoEditarValor(figura, interacao);
  };
  // data-figura-id também é o alvo do arraste customizado do enunciado
  // (App.aoSoltarNoDiagrama usa elementFromPoint + closest('[data-figura-id]')).
  return <g className={`scene-figure${editavel ? " scene-figure-editable" : ""}${destacada ? " scene-figure-destacada" : ""}`}
      data-figura-id={figura.id} data-editavel={editavel || undefined}
      role={editavel ? "button" : undefined} tabIndex={editavel ? 0 : undefined}
      aria-label={editavel ? `Editar ${figura.rotulo}` : undefined}
      onClick={editavel ? iniciarEdicao : undefined}
      onKeyDown={editavel ? (evento) => {
        if (evento.key === "Enter" || evento.key === " ") {
          evento.preventDefault(); iniciarEdicao();
        }
      } : undefined}>
    {figura.tipo === "ELIPSE"
      ? <ellipse cx={figura.x + figura.largura / 2} cy={figura.y + figura.altura / 2}
          rx={figura.largura / 2} ry={figura.altura / 2} />
      : <rect x={figura.x} y={figura.y} width={figura.largura} height={figura.altura}
          rx={figura.tipo === "RETANGULO_ARREDONDADO" ? 10 : 0} />}
    <text x={figura.x + figura.largura / 2} y={coordenadaYDoRotulo(figura)}>
      {figura.conhecido && figura.valor !== null ? figura.valor : figura.rotulo}
    </text>
    {figura.subtitulo && <text className="scene-figure-subtitle"
      x={figura.x + figura.largura / 2} y={coordenadaYDoSubtitulo(figura)}>{figura.subtitulo}</text>}
    <LupaCenaGerard figura={figura} />
  </g>;
}
