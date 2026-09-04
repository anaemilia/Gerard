import type { FiguraCena, InteracaoPermitidaFigura } from "../contratos";
import { coordenadaYDoRotulo, coordenadaYDoSubtitulo, coordenadaYDoValor } from "./geometriaSvg";

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
  // figura.engatada vem do servidor (ver engatarIncognita/projetarCena) —
  // "?" já arrastado do enunciado até aqui (protocolo mouse-texto) — só
  // então o duplo-clique abre a digitação.
  const engatada = figura.engatada;
  const interacao = figura.interacoes_permitidas.find(
    (item) => item.tipo === "EDITAR_VALOR");
  const editavel = Boolean(interacao && aoEditarValor && engatada);
  const iniciarEdicao = () => {
    if (interacao && aoEditarValor) aoEditarValor(figura, interacao);
  };
  const conhecida = figura.conhecido && figura.valor !== null;
  // data-figura-id também é o alvo do arraste customizado do enunciado
  // (App.aoSoltarNoDiagrama usa elementFromPoint + closest('[data-figura-id]')).
  return <g className={`scene-figure${editavel ? " scene-figure-editable" : ""}${destacada ? " scene-figure-destacada" : ""}`}
      data-figura-id={figura.id} data-editavel={editavel || undefined}
      role={editavel ? "button" : undefined} tabIndex={editavel ? 0 : undefined}
      aria-label={editavel ? `Digitar valor de ${figura.rotulo}` : undefined}
      onDoubleClick={editavel ? iniciarEdicao : undefined}
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
    {/* O valor (ou o "?" já engatado), quando presente, é sempre centralizado
        na própria figura — nunca segue posicao_rotulo (ACIMA/ABAIXO) — mesmo
        invariante de ElementoVergnaud.desenhar (Main.java): textoEditavel se
        centraliza na caixa independente de rotulosAcima, que só governa o
        rótulo/papel. */}
    <text x={figura.x + figura.largura / 2}
      y={conhecida || engatada ? coordenadaYDoValor(figura) : coordenadaYDoRotulo(figura)}>
      {conhecida ? figura.valor : engatada ? "?" : figura.rotulo}
    </text>
    {figura.subtitulo && <text className="scene-figure-subtitle"
      x={figura.x + figura.largura / 2} y={coordenadaYDoSubtitulo(figura)}>{figura.subtitulo}</text>}
    <LupaCenaGerard figura={figura} />
  </g>;
}
