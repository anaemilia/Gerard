import { useEffect, useRef, useState } from "react";
import type { ElementoTexto, FiguraCena } from "./contratos";
import { EditorNarrativa } from "./EditorNarrativa";

// Mesmos valores de Main.java: DISTANCIA_REALCE_ALVO (linha ~979) e o fator
// 0.35 de aplicarAtracaoMagnetica — estilo de interação fixo em
// EstiloInteracao.SNAP_TO_TARGET desde 2026-07-28 (Main.java:814-817), o
// único estilo já ativo no app, então é o que a web porta.
const DISTANCIA_REALCE_ALVO_PX = 48;
const FATOR_ATRACAO = 0.35;

/**
 * Enunciado com os elementos vinculados a um papel semântico marcados e
 * arrastáveis — mesmo recurso do desktop (Main.java: elementosTexto +
 * vincularPapeisSemanticosAosElementosTexto + desenharMarcadoresFixosDoTexto),
 * agora com os tokens/posições calculados no servidor
 * (SegmentadorTextoSemantico, portátil). O marcador (borda tracejada +
 * leve fundo) é o mesmo para qualquer papel — corFundoDoPapel/
 * corBordaDoPapel/corTextoDoPapel no desktop hoje devolvem sempre a mesma
 * cor fixa (COR_SUPERFICIE/COR_BORDA/COR_TEXTO), não uma cor por papel.
 *
 * Arraste por mouse customizado (mousedown/mousemove/mouseup +
 * elementFromPoint) em vez do HTML5 drag-and-drop nativo — o nativo
 * (draggable/dragstart/drop) se mostrou pouco confiável em teste manual
 * (arrastar não disparava nada). Soltar fora de qualquer figura só encerra
 * o gesto sem efeito (mesmo invariante do desktop: "soltar fora de
 * qualquer elemento encerra um gesto, mas não constitui automaticamente
 * uma ação instrumental", ver gerard-ajuda-adaptativa).
 *
 * Atração magnética (atualizarRealceAlvoProximidade/aplicarAtracaoMagnetica,
 * Main.java): enquanto o centro da cópia flutuante está a até 48px da caixa
 * cuja chave_papel_semantico bate com o papel arrastado, a cópia é puxada
 * 35% da distância restante a cada movimento (não salta direto) e a caixa
 * ganha o realce SNAP (ver .scene-figure-destacada). Ao soltar dentro dessa
 * distância, a soltura conta para essa caixa mesmo que o ponto exato do
 * cursor não esteja sobre ela (deveCentralizarAoSoltar do desktop).
 */
export function EnunciadoInterativo({ elementos, figuras, organizadores = [], modeloPalavraComum = null,
  permiteEditarNarrativa, aoSoltar, aoAtualizarAlvo }: {
  permiteEditarNarrativa: boolean;
  elementos: readonly ElementoTexto[];
  figuras: readonly FiguraCena[];
  organizadores?: readonly ElementoTexto[];
  modeloPalavraComum?: ElementoTexto | null;
  aoSoltar: (papelId: string, x: number, y: number, alvoFiguraId?: string | null) => void;
  aoAtualizarAlvo?: (figuraId: string | null) => void;
}) {
  const [arrastando, setArrastando] = useState<string | null>(null);
  const [editandoNarrativa, setEditandoNarrativa] = useState(false);
  const ghostRef = useRef<HTMLDivElement>(null);
  const posicaoGhostRef = useRef({ x: 0, y: 0 });
  const alvoAtualRef = useRef<string | null>(null);

  useEffect(() => {
    if (!permiteEditarNarrativa) setEditandoNarrativa(false);
  }, [permiteEditarNarrativa]);

  function definirAlvo(figuraId: string | null) {
    if (alvoAtualRef.current !== figuraId) {
      alvoAtualRef.current = figuraId;
      aoAtualizarAlvo?.(figuraId);
    }
  }

  function atualizarPosicao(papelId: string, cursorX: number, cursorY: number) {
    const posicaoCursor = { x: cursorX + 12, y: cursorY + 12 };
    const figuraAlvo = figuras.find((item) => item.chave_papel_semantico === papelId);
    const elementoAlvo = figuraAlvo
      ? document.querySelector<SVGGElement>(`[data-figura-id="${figuraAlvo.id}"]`)
      : null;
    let proximo = false;

    if (elementoAlvo) {
      const retanguloAlvo = elementoAlvo.getBoundingClientRect();
      const larguraGhost = ghostRef.current?.offsetWidth ?? 0;
      const alturaGhost = ghostRef.current?.offsetHeight ?? 0;
      const centroX = posicaoGhostRef.current.x + larguraGhost / 2;
      const centroY = posicaoGhostRef.current.y + alturaGhost / 2;
      const dx = centroX < retanguloAlvo.left ? retanguloAlvo.left - centroX
        : centroX > retanguloAlvo.right ? centroX - retanguloAlvo.right : 0;
      const dy = centroY < retanguloAlvo.top ? retanguloAlvo.top - centroY
        : centroY > retanguloAlvo.bottom ? centroY - retanguloAlvo.bottom : 0;
      proximo = Math.sqrt(dx * dx + dy * dy) <= DISTANCIA_REALCE_ALVO_PX;

      if (proximo) {
        const destinoX = retanguloAlvo.left + (retanguloAlvo.width - larguraGhost) / 2;
        const destinoY = retanguloAlvo.top + (retanguloAlvo.height - alturaGhost) / 2;
        posicaoGhostRef.current = {
          x: posicaoGhostRef.current.x + (destinoX - posicaoGhostRef.current.x) * FATOR_ATRACAO,
          y: posicaoGhostRef.current.y + (destinoY - posicaoGhostRef.current.y) * FATOR_ATRACAO
        };
      }
    }
    if (!proximo) {
      posicaoGhostRef.current = posicaoCursor;
    }
    if (ghostRef.current) {
      ghostRef.current.style.transform =
        `translate(${posicaoGhostRef.current.x}px, ${posicaoGhostRef.current.y}px)`;
    }
    definirAlvo(proximo && figuraAlvo ? figuraAlvo.id : null);
  }

  function iniciarArraste(evento: React.MouseEvent, papelId: string) {
    evento.preventDefault();
    setArrastando(papelId);
    posicaoGhostRef.current = { x: evento.clientX + 12, y: evento.clientY + 12 };
    const x0 = evento.clientX, y0 = evento.clientY;
    // O fantasma só existe no DOM depois do próximo commit (setArrastando é
    // assíncrono), então a 1ª posição precisa de requestAnimationFrame — mas
    // esse callback pode disparar DEPOIS do mouseup se o gesto for muito
    // rápido (arraste sintético de automação, ou um clique real muito
    // curto): sem cancelar, ele reativaria o realce já limpo com a posição
    // inicial do arraste. cancelAnimationFrame no soltar evita a corrida.
    const frameInicial = requestAnimationFrame(() => atualizarPosicao(papelId, x0, y0));

    function aoMover(e: MouseEvent) {
      atualizarPosicao(papelId, e.clientX, e.clientY);
    }
    function aoSoltarMouse(e: MouseEvent) {
      cancelAnimationFrame(frameInicial);
      window.removeEventListener("mousemove", aoMover);
      window.removeEventListener("mouseup", aoSoltarMouse);
      setArrastando(null);
      const alvoNoMomento = alvoAtualRef.current;
      definirAlvo(null);
      aoSoltar(papelId, e.clientX, e.clientY, alvoNoMomento);
    }
    window.addEventListener("mousemove", aoMover);
    window.addEventListener("mouseup", aoSoltarMouse);
  }

  const valorArrastando = arrastando
    ? elementos.find((elemento) => elemento.papel_id === arrastando)?.valor
    : undefined;

  if (permiteEditarNarrativa && editandoNarrativa) {
    return <EditorNarrativa elementos={elementos} organizadores={organizadores}
      modeloPalavraComum={modeloPalavraComum}
      aoFechar={() => setEditandoNarrativa(false)} />;
  }

  return <>
    {permiteEditarNarrativa && <button type="button" className="botao-editar-narrativa"
      aria-label="Editar texto" title="Editar texto"
      onClick={() => setEditandoNarrativa(true)}>
      <svg viewBox="0 0 16 16" aria-hidden="true" focusable="false">
        <path d="M2 4V2h12v2 M8 2v12 M5 14h6" />
      </svg>
    </button>}
    <h1 id="enunciado">
      {elementos.map((elemento, indice) => {
        const espaco = indice > 0 ? " " : "";
        if (!elemento.papel_id) {
          return <span key={indice}>{espaco}{elemento.valor}</span>;
        }
        const papelId = elemento.papel_id;
        return <span key={indice}>{espaco}<span
          className={`enunciado-elemento-semantico${arrastando === papelId ? " enunciado-elemento-arrastando" : ""}`}
          onMouseDown={(evento) => iniciarArraste(evento, papelId)}
          aria-label={elemento.incognita ? "Incógnita — arraste até o diagrama" : "Valor conhecido — arraste até o diagrama"}
        >{elemento.valor}</span></span>;
      })}
    </h1>
    {/* Cópia flutuante que segue o cursor durante o arraste — mesmo padrão
        visual do desktop (converterElementoTextoEmItemDiagrama/
        ItemTextoArrastavel, Main.java): o marcador de origem fica esmaecido
        (enunciado-elemento-arrastando) enquanto esta cópia sólida acompanha
        o mouse (ou é puxada pela atração magnética) até ser solta.
        pointer-events:none é essencial: sem isso, o elementFromPoint em
        App.tsx pegaria o próprio fantasma como alvo do drop em vez da caixa
        do diagrama por baixo dele. */}
    {arrastando && <div ref={ghostRef} className="enunciado-ghost-arraste" aria-hidden="true">
      {valorArrastando}
    </div>}
  </>;
}
