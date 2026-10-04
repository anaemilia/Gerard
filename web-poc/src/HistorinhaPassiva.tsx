import { useEffect, useRef, useState } from "react";
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
 * Legenda ativa DENTRO da animação: mede o tempo desde que o GIF carregou (e recomeça a cada laço)
 * e mostra o texto cujo intervalo, projetado pelo backend, contém o instante. Não compõe texto.
 */
function LegendaDaAnimacao({ legendas, duracao, inicio }: {
  legendas: NonNullable<HistorinhaAjudaVisualWeb["legendas"]>; duracao: number; inicio: number;
}) {
  const [agora, setAgora] = useState(0);
  useEffect(() => {
    const relogio = window.setInterval(() => setAgora(performance.now()), 100);
    return () => window.clearInterval(relogio);
  }, []);
  if (duracao <= 0) return null;
  const t = (((agora || performance.now()) - inicio) / 1000) % duracao;
  const ativa = legendas.find((l) => t >= l.inicio_s && t < l.fim_s);
  return ativa ? <p className="historinha-legenda" role="status">{ativa.texto}</p> : null;
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
  const [carregada, setCarregada] = useState(0);
  const inicioLaco = useRef(performance.now());
  useEffect(() => { setIndice(0); }, [escolhida?.referencia, escolhida?.texto, codigoIdioma]);
  if (!escolhida) return null;
  if (escolhida.tipo === "HISTORINHA_TEXTUAL") {
    return <div className="historinha-passiva historinha-textual" aria-label="Historinha">
      <p>{escolhida.texto}</p>
    </div>;
  }
  const arquivos = arquivosHistorinha(escolhida.referencia ?? "", codigoIdioma);
  const legendas = escolhida.legendas ?? [];
  return <div className="historinha-passiva" aria-label="Historinha">
    <div className="historinha-animacao">
      <img src={arquivos[Math.min(indice, arquivos.length - 1)]} alt="" aria-hidden="true"
        onLoad={() => { inicioLaco.current = performance.now(); setCarregada((n) => n + 1); }}
        onError={() => setIndice((atual) => (atual + 1 < arquivos.length ? atual + 1 : atual))} />
      {legendas.length > 0 && <LegendaDaAnimacao key={carregada} legendas={legendas}
        duracao={escolhida.duracao_s ?? 0} inicio={inicioLaco.current} />}
    </div>
  </div>;
}
