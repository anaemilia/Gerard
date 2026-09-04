import type { ConectorCena, FiguraCena } from "../contratos";

/** Geometria técnica SVG; não contém categorias nem relações matemáticas. */
export function caminhoDoConector(c: ConectorCena) {
  if (c.tipo === "SETA_CURVA") {
    // Ponto de controle abaixo dos dois extremos (deslocamento fixo, não
    // proporcional à largura) — mesma curva de desenharSetaCurva em
    // RenderizadorSwingDiagramaAditivo.java, que faz o arco descer por
    // baixo das figuras que conecta, não curvar por cima delas.
    const cx = (c.x1 + c.x2) / 2;
    const cy = Math.max(c.y1, c.y2) + 96;
    return `M ${c.x1} ${c.y1} Q ${cx} ${cy} ${c.x2} ${c.y2}`;
  }
  if (c.tipo === "CHAVE_VERTICAL") {
    const meio = (c.y1 + c.y2) / 2;
    const chave = `M ${c.x1} ${c.y1} q 12 0 12 12 V ${meio - 12} q 0 12 12 12 q -12 0 -12 12 V ${c.y2 - 12} q 0 12 -12 12`;
    return c.x_alvo === undefined ? chave : `${chave} M ${c.x1 + 24} ${meio} L ${c.x_alvo} ${c.y_alvo}`;
  }
  if (c.tipo === "CHAVE_HORIZONTAL") {
    const meio = (c.x1 + c.x2) / 2;
    const chave = `M ${c.x1} ${c.y1} q 0 12 12 12 H ${meio - 12} q 12 0 12 12 q 0 -12 12 -12 H ${c.x2 - 12} q 12 0 12 -12`;
    return c.x_alvo === undefined ? chave : `${chave} M ${meio} ${c.y1 + 24} L ${c.x_alvo} ${c.y_alvo}`;
  }
  return `M ${c.x1} ${c.y1} L ${c.x2} ${c.y2}`;
}

export function coordenadaYDoRotulo(f: FiguraCena) {
  if (f.posicao_rotulo === "ACIMA") return f.y - 14;
  if (f.posicao_rotulo === "ABAIXO") return f.y + f.altura + 22;
  return f.y + f.altura / 2 + 4;
}

/** Abaixo do rótulo quando ele já ocupa a linha logo sob a figura (ABAIXO), senão logo sob a figura. */
export function coordenadaYDoSubtitulo(f: FiguraCena) {
  return f.posicao_rotulo === "ABAIXO" ? f.y + f.altura + 40 : f.y + f.altura + 24;
}
