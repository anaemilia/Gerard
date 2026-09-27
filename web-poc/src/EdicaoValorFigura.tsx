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
  requerSinal, aoAlterarValor, aoConfirmarDigitacao, aoConfirmarValor, aoNegarValor }: {
  figuraId: string;
  papelNome: string;
  pergunta: string | null | undefined;
  modo: "digitando" | "confirmando";
  valor: string;
  ocupado: boolean;
  requerSinal: boolean;
  aoAlterarValor: (valor: string) => void;
  aoConfirmarDigitacao: () => void;
  aoConfirmarValor: () => void;
  aoNegarValor: () => void;
}) {
  const [retangulo, setRetangulo] = useState<DOMRect | null>(null);
  const [sinal, setSinal] = useState<"+" | "-">(valor.startsWith("-") ? "-" : "+");

  useEffect(() => {
    const elemento = document.querySelector(`[data-figura-id="${figuraId}"]`);
    setRetangulo(elemento?.getBoundingClientRect() ?? null);
  }, [figuraId, modo]);

  if (!retangulo) return null;
  const estilo = { left: retangulo.right + 10, top: retangulo.top };

  if (modo === "digitando") {
    return <div className="valor-figura-tip" style={estilo} role="dialog" aria-label={papelNome}>
      <label htmlFor={`valor-${figuraId}`}>{papelNome}</label>
      <div className="valor-figura-entrada">
        {requerSinal && <fieldset className="valor-figura-sinal">
          <legend>Sinal</legend>
          <label className="valor-figura-opcao">
            <input type="radio" name={`sinal-edicao-${figuraId}`} value="+" checked={sinal === "+"}
              disabled={ocupado} onChange={() => {
                setSinal("+");
                if (valor !== "") aoAlterarValor(String(Math.abs(Number(valor))));
              }} /> positivo&nbsp;(+)
          </label>
          <label className="valor-figura-opcao">
            <input type="radio" name={`sinal-edicao-${figuraId}`} value="-" checked={sinal === "-"}
              disabled={ocupado} onChange={() => {
                setSinal("-");
                if (valor !== "") aoAlterarValor(`-${Math.abs(Number(valor))}`);
              }} /> negativo&nbsp;(-)
          </label>
        </fieldset>}
        <input id={`valor-${figuraId}`} type="number" min={requerSinal ? 0 : undefined}
          step="1" inputMode="numeric" autoFocus value={requerSinal ? valor.replace(/^-/, "") : valor}
          disabled={ocupado} onChange={(evento) => {
            const magnitude = evento.target.value;
            aoAlterarValor(requerSinal && sinal === "-" && magnitude !== "" ? `-${magnitude}` : magnitude);
          }} onKeyDown={(evento) => { if (evento.key === "Enter") aoConfirmarDigitacao(); }} />
      </div>
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
