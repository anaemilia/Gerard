import { useEffect, useState } from "react";

/**
 * Tip ancorada na própria figura do diagrama (mesmo padrão de
 * EdicaoValorFigura) — aparece quando um papel conhecido que precisa de
 * representação de sinal (PapelQuantitativo.necessitaRepresentacaoDeSinal,
 * catalogado por CatalogoNecessidadeRepresentacaoDeSinal) é revelado
 * (arrastado até o diagrama) mas ainda não teve o sinal escolhido — mesmo
 * mecanismo do desktop (ScaffoldingNumeroRelativo.mostrarMenuEscolhaSinal /
 * MenuSinalNumeroRelativo, Main.java): duas opções em radiobutton, com os
 * mesmos rótulos literais do desktop ("positivo (+)" / "negativo (-)").
 *
 * A escolha do estudante é sempre aplicada (nunca bloqueada por divergir do
 * sinal curado, ver ServicoAtividadeWebComSinal) — o servidor só devolve um
 * aviso não-bloqueante (mensagemDivergente, mesma chave
 * ui.tooltip.relativeSign.confirm do desktop) quando isso acontece, exibido
 * na mesma posição por alguns instantes (ver App.tsx).
 */
export function EscolhaSinalFigura({ figuraId, papelNome, ocupado, mensagemDivergente,
  aoEscolherSinal }: {
  figuraId: string;
  papelNome: string;
  ocupado: boolean;
  mensagemDivergente: string | null;
  aoEscolherSinal: (sinal: "+" | "-") => void;
}) {
  const [retangulo, setRetangulo] = useState<DOMRect | null>(null);

  useEffect(() => {
    const elemento = document.querySelector(`[data-figura-id="${figuraId}"]`);
    setRetangulo(elemento?.getBoundingClientRect() ?? null);
  }, [figuraId]);

  if (!retangulo) return null;
  const estilo = { left: retangulo.right + 10, top: retangulo.top };

  if (mensagemDivergente) {
    return <div className="valor-figura-tip" style={estilo} role="status" aria-label={papelNome}>
      <p>{mensagemDivergente}</p>
    </div>;
  }

  return <div className="valor-figura-tip" style={estilo} role="dialog" aria-label={papelNome}>
    <p>{papelNome}</p>
    <label className="valor-figura-opcao">
      <input type="radio" name={`sinal-${figuraId}`} disabled={ocupado}
        onChange={() => aoEscolherSinal("+")} /> positivo&nbsp;&nbsp;(+)
    </label>
    <label className="valor-figura-opcao">
      <input type="radio" name={`sinal-${figuraId}`} disabled={ocupado}
        onChange={() => aoEscolherSinal("-")} /> negativo&nbsp;&nbsp;(-)
    </label>
  </div>;
}
