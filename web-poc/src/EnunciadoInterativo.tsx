import { useRef, useState } from "react";
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
 *
 * Arraste por mouse customizado (mousedown/mousemove/mouseup +
 * elementFromPoint) em vez do HTML5 drag-and-drop nativo — o nativo
 * (draggable/dragstart/drop) se mostrou pouco confiável em teste manual
 * (arrastar não disparava nada). Soltar fora de qualquer figura só encerra
 * o gesto sem efeito (mesmo invariante do desktop: "soltar fora de
 * qualquer elemento encerra um gesto, mas não constitui automaticamente
 * uma ação instrumental", ver gerard-ajuda-adaptativa).
 */
export function EnunciadoInterativo({ elementos, aoSoltar }: {
  elementos: readonly ElementoTexto[];
  aoSoltar: (papelId: string, x: number, y: number) => void;
}) {
  const [arrastando, setArrastando] = useState<string | null>(null);
  const posicaoRef = useRef({ x: 0, y: 0 });

  function iniciarArraste(evento: React.MouseEvent, papelId: string) {
    evento.preventDefault();
    setArrastando(papelId);
    posicaoRef.current = { x: evento.clientX, y: evento.clientY };

    function aoMover(e: MouseEvent) {
      posicaoRef.current = { x: e.clientX, y: e.clientY };
    }
    function aoSoltarMouse(e: MouseEvent) {
      window.removeEventListener("mousemove", aoMover);
      window.removeEventListener("mouseup", aoSoltarMouse);
      setArrastando(null);
      aoSoltar(papelId, e.clientX, e.clientY);
    }
    window.addEventListener("mousemove", aoMover);
    window.addEventListener("mouseup", aoSoltarMouse);
  }

  return <h1 id="enunciado">
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
  </h1>;
}
