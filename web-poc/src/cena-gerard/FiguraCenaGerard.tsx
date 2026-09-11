import type { FiguraCena, InteracaoPermitidaFigura } from "../contratos";
import { coordenadaYDoRotulo, coordenadaYDoSubtitulo, coordenadaYDoValor } from "./geometriaSvg";

function LupaCenaGerard({ figura, aoAlternarEixo }: { figura: FiguraCena;
    aoAlternarEixo?: (figura: FiguraCena) => void }) {
  if (!figura.exibir_lupa) return null;
  // Mesmos textos do desktop (PaineisEixosRelacoes.obterDicaLupa/
  // ScaffoldingGraficoInteiros.obterDicaBotaoEsconder, chaves
  // ui.tooltip.integerAxis.reveal/hide em mensagens_pt.properties) — o
  // protocolo REVELAR_EIXO/OCULTAR_EIXO ainda não desenha o eixo em si no
  // web (só alterna o estado revelado/fechado, ver LEVANTAMENTO_ACOPLAMENTO_
  // MAIN_WEB_2026-08-31.md), por isso o rótulo não promete o widget completo.
  const rotulo = figura.lupa_habilitada ? "Ocultar eixo X" : "Ver o eixo x deste número relativo";
  return <g className={`scene-magnifier${figura.lupa_habilitada ? " scene-magnifier-enabled" : ""}`}
      transform={`translate(${figura.x + figura.largura + 12} ${figura.y + 4})`}
      role={aoAlternarEixo ? "button" : undefined} tabIndex={aoAlternarEixo ? 0 : undefined}
      aria-label={rotulo}
      onClick={aoAlternarEixo ? () => aoAlternarEixo(figura) : undefined}
      onKeyDown={aoAlternarEixo ? (evento) => {
        if (evento.key === "Enter" || evento.key === " ") {
          evento.preventDefault(); aoAlternarEixo(figura);
        }
      } : undefined}>
    <circle cx="8" cy="8" r="7"/><path d="M 13 13 L 19 19"/><circle cx="8" cy="8" r="2"/>
  </g>;
}

/**
 * Grupo de quadradinhos do material concreto (AG_EMCME): mesmo modelo de
 * figura das demais (id/x/y/largura/altura/rotulo/valor), desenhado como
 * grade de quadradinhos em vez de caixa com número — via foreignObject para
 * reaproveitar as mesmas classes CSS de MaterialConcretoQuadradinhos
 * (styles.css), em vez de reimplementar a grade em SVG puro. O gerador de
 * cena decide a contagem (figura.valor); este componente só materializa.
 */
function GrupoQuadradinhosCenaGerard({ figura, ehAlvo, ocupado, aoAjustar, textoAdicionar, textoRemover }: {
  figura: FiguraCena; ehAlvo: boolean; ocupado: boolean;
  aoAjustar?: (delta: 1 | -1) => void;
  textoAdicionar?: string; textoRemover?: string;
}) {
  const quantidade = figura.valor ?? 0;
  return <g className="scene-figure scene-figure-quadradinhos" data-figura-id={figura.id}>
    <rect x={figura.x} y={figura.y} width={figura.largura} height={figura.altura}
      rx={10} className="scene-quadradinhos-moldura" />
    <text x={figura.x + figura.largura / 2} y={coordenadaYDoRotulo(figura)}>{figura.rotulo}</text>
    <foreignObject x={figura.x + 6} y={figura.y + 6}
        width={Math.max(0, figura.largura - 12)} height={Math.max(0, figura.altura - 12)}>
      <div className="quadradinhos-grupo">
        <div className="quadradinhos-grade" aria-hidden="true">
          {Array.from({ length: quantidade }, (_, indice) => <span key={indice} className="quadradinho" />)}
        </div>
        {ehAlvo && aoAjustar && <div className="quadradinhos-controles">
          <button type="button" onClick={() => aoAjustar(-1)} disabled={ocupado || quantidade <= 0}
            aria-label={textoRemover}>−</button>
          <span className="quadradinhos-contagem">{quantidade}</span>
          <button type="button" onClick={() => aoAjustar(1)} disabled={ocupado}
            aria-label={textoAdicionar}>+</button>
        </div>}
      </div>
    </foreignObject>
  </g>;
}

export function FiguraCenaGerard({ figura, aoEditarValor, destacada, ocupado, aoAjustarQuadradinho,
    textoAdicionarQuadradinho, textoRemoverQuadradinho, aoAlternarEixo }: {
  figura: FiguraCena;
  aoEditarValor?: (figura: FiguraCena, interacao: InteracaoPermitidaFigura) => void;
  destacada?: boolean;
  ocupado?: boolean;
  aoAjustarQuadradinho?: (papelId: string, delta: 1 | -1) => void;
  textoAdicionarQuadradinho?: string;
  textoRemoverQuadradinho?: string;
  aoAlternarEixo?: (figura: FiguraCena) => void;
}) {
  if (figura.tipo === "GRUPO_QUADRADINHOS") {
    const interacaoAjustar = figura.interacoes_permitidas.find(
      (item) => item.tipo === "AJUSTAR_QUADRADINHO");
    return <GrupoQuadradinhosCenaGerard figura={figura} ehAlvo={Boolean(interacaoAjustar)}
      ocupado={Boolean(ocupado)}
      aoAjustar={interacaoAjustar && aoAjustarQuadradinho
        ? (delta) => aoAjustarQuadradinho(interacaoAjustar.papel_id, delta) : undefined}
      textoAdicionar={textoAdicionarQuadradinho} textoRemover={textoRemoverQuadradinho} />;
  }
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
    {figura.subtitulo && <text className="scene-figure-subtitle"
      x={figura.x + figura.largura / 2} y={coordenadaYDoSubtitulo(figura)}>{figura.subtitulo}</text>}
    <text className={figura.subtitulo ? "scene-figure-role" : undefined}
      x={figura.x + figura.largura / 2}
      y={conhecida || engatada ? coordenadaYDoValor(figura) : coordenadaYDoRotulo(figura)}>
      {conhecida ? figura.valor : engatada ? "?" : figura.rotulo}
    </text>
    {(conhecida || engatada) && <text className="scene-figure-role"
      x={figura.x + figura.largura / 2} y={coordenadaYDoRotulo(figura)}>{figura.rotulo}</text>}
    <LupaCenaGerard figura={figura} aoAlternarEixo={aoAlternarEixo} />
  </g>;
}
