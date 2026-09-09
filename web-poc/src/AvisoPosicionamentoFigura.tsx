import { useEffect, useState } from "react";

/**
 * Tip ancorada na própria figura do diagrama (mesmo padrão de
 * EscolhaSinalFigura/EdicaoValorFigura) — aparece quando o servidor rejeita
 * a soltura por incompatibilidade semântica entre o elemento arrastado do
 * enunciado (origem) e o papel da figura-alvo (destino). Mesma avaliação de
 * ScaffoldingQuestionamento.avaliarPosicionamento (Main.java) e mesma
 * mensagem (ui.question.semanticMismatch) — o servidor já resolve o texto;
 * este componente só materializa.
 */
export function AvisoPosicionamentoFigura({ figuraId, mensagem }: {
  figuraId: string;
  mensagem: string;
}) {
  const [retangulo, setRetangulo] = useState<DOMRect | null>(null);

  useEffect(() => {
    const elemento = document.querySelector(`[data-figura-id="${figuraId}"]`);
    setRetangulo(elemento?.getBoundingClientRect() ?? null);
  }, [figuraId]);

  if (!retangulo) return null;
  const estilo = { left: retangulo.right + 10, top: retangulo.top };

  return <div className="valor-figura-tip" style={estilo} role="status">
    <p>{mensagem}</p>
  </div>;
}
