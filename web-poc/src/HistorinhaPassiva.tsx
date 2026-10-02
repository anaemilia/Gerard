import { useEffect, useState } from "react";
import type { HistorinhaAjudaVisualWeb } from "./contratos";

/**
 * Arquivos candidatos de uma historinha, em ordem de preferência. O texto da historinha
 * hoje está gravado no próprio GIF; por isso o idioma é resolvido por variante de
 * arquivo (`<referencia>.<idioma>.gif`, ex.: `.en.gif`) com volta para o arquivo base
 * (português) quando a variante não existe. Nenhuma frase é composta aqui.
 */
export function arquivosHistorinha(referencia: string, codigoIdioma: string | undefined) {
  const base = `/ajuda/${referencia}.gif`;
  const lingua = (codigoIdioma ?? "").split(/[-_]/)[0].toLowerCase();
  return !lingua || lingua === "pt" ? [base] : [`/ajuda/${referencia}.${lingua}.gif`, base];
}

/**
 * Historinha passiva (sem interação, semântica nem conhecimento próprio): exibe a primeira
 * entrada de `ajuda_visual`. O backend decide se existe e qual é (a da própria situação, se
 * houver); o cliente não escolhe entre alternativas, só renderiza a projeção da API.
 */
export function HistorinhaPassiva({ historinhas, codigoIdioma }: {
  historinhas: readonly HistorinhaAjudaVisualWeb[];
  codigoIdioma: string | undefined;
}) {
  const escolhida = historinhas[0];
  const [indice, setIndice] = useState(0);
  useEffect(() => { setIndice(0); }, [escolhida?.referencia, codigoIdioma]);
  if (!escolhida) return null;
  const arquivos = arquivosHistorinha(escolhida.referencia, codigoIdioma);
  return <div className="historinha-passiva" aria-label="Historinha">
    <img src={arquivos[Math.min(indice, arquivos.length - 1)]} alt="" aria-hidden="true"
      onError={() => setIndice((atual) => (atual + 1 < arquivos.length ? atual + 1 : atual))} />
  </div>;
}
