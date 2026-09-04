import { useEffect, useState } from "react";

/**
 * Tip ancorada na própria figura do diagrama — não um painel à parte. Dois
 * passos, mesmo protocolo do desktop (editarNumeroNatural/
 * confirmarValorIncognitaAceito, Main.java): "digitando" é o campo de texto
 * aberto pelo duplo-clique na incógnita já engatada (protocolo mouse-texto);
 * "confirmando" é a pergunta de confirmação (ui.question.valueMismatch,
 * resolvida no servidor — confirmacao_valor_papel — nunca hardcoded aqui)
 * com Sim/Não em radiobutton, que ao "Não" volta a pedir o valor em vez de
 * propagar uma resposta que o próprio usuário disse não ter certeza.
 */
export function EdicaoValorFigura({ figuraId, papelNome, pergunta, modo, valor, ocupado,
  aoAlterarValor, aoConfirmarDigitacao, aoConfirmarValor, aoNegarValor }: {
  figuraId: string;
  papelNome: string;
  pergunta: string | null | undefined;
  modo: "digitando" | "confirmando";
  valor: string;
  ocupado: boolean;
  aoAlterarValor: (valor: string) => void;
  aoConfirmarDigitacao: () => void;
  aoConfirmarValor: () => void;
  aoNegarValor: () => void;
}) {
  const [retangulo, setRetangulo] = useState<DOMRect | null>(null);

  useEffect(() => {
    const elemento = document.querySelector(`[data-figura-id="${figuraId}"]`);
    setRetangulo(elemento?.getBoundingClientRect() ?? null);
  }, [figuraId, modo]);

  if (!retangulo) return null;
  const estilo = { left: retangulo.right + 10, top: retangulo.top };

  if (modo === "digitando") {
    return <div className="valor-figura-tip" style={estilo} role="dialog" aria-label={papelNome}>
      <label htmlFor={`valor-${figuraId}`}>{papelNome}</label>
      <input id={`valor-${figuraId}`} type="number" step="1" inputMode="numeric" autoFocus
        value={valor} disabled={ocupado}
        onChange={(evento) => aoAlterarValor(evento.target.value)}
        onKeyDown={(evento) => { if (evento.key === "Enter") aoConfirmarDigitacao(); }} />
    </div>;
  }

  return <div className="valor-figura-tip" style={estilo} role="dialog" aria-label={papelNome}>
    <p>{pergunta}</p>
    <label className="valor-figura-opcao">
      <input type="radio" name={`confirmar-valor-${figuraId}`} disabled={ocupado}
        onChange={aoConfirmarValor} /> Sim
    </label>
    <label className="valor-figura-opcao">
      <input type="radio" name={`confirmar-valor-${figuraId}`} disabled={ocupado}
        onChange={aoNegarValor} /> Não
    </label>
  </div>;
}
