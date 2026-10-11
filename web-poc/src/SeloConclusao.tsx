import { useEffect, useRef, useState } from "react";
import type { AcaoConclusao, ConclusaoWeb } from "./contratos";

// Selo de conclusão da modelagem e tip de decisão (SeloConclusaoModelagem / TipConclusaoModelagem /
// FluxoFeedbackConclusaoModelagem, desktop). Os textos (ui.completion.*), a pausa e as ações HTTP vêm do estado
// publicado pela API; quem decide o que cada escolha faz é o domínio (EscolhaConclusaoModelagem). Aqui só se realiza:
// depois da pausa aparece o link "Próxima tarefa"; o mouse sobre ele abre o tip com Sim/Não; sair do tip o fecha
// após um instante; Sim e o clique no link iniciam nova situação; Não esconde selo e tip.
const ATRASO_FECHAR_TIP_MS = 350;

type Fase = "pausa" | "decisao" | "encerrado";

export function SeloConclusao({ conclusao, ocupado, aoEscolher }: Readonly<{
  conclusao: ConclusaoWeb; ocupado: boolean; aoEscolher: (acao: AcaoConclusao) => Promise<void>;
}>) {
  const [fase, setFase] = useState<Fase>("pausa");
  const [tipAberto, setTipAberto] = useState(false);
  const temporizadorFechar = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    const temporizador = setTimeout(
      () => setFase((atual) => (atual === "pausa" ? "decisao" : atual)), conclusao.atraso_decisao_ms);
    return () => clearTimeout(temporizador);
  }, [conclusao.atraso_decisao_ms]);
  useEffect(() => () => { if (temporizadorFechar.current) clearTimeout(temporizadorFechar.current); }, []);

  function acaoDe(tipo: AcaoConclusao["tipo"]) { return conclusao.acoes.find((a) => a.tipo === tipo); }
  function abrirTip() {
    if (temporizadorFechar.current) { clearTimeout(temporizadorFechar.current); temporizadorFechar.current = null; }
    setTipAberto(true);
  }
  function agendarFecharTip() {
    if (temporizadorFechar.current) clearTimeout(temporizadorFechar.current);
    temporizadorFechar.current = setTimeout(() => setTipAberto(false), ATRASO_FECHAR_TIP_MS);
  }
  async function escolher(tipo: AcaoConclusao["tipo"]) {
    const acao = acaoDe(tipo);
    if (!acao || ocupado) return;
    setTipAberto(false);
    if (!acao.inicia_nova_situacao) setFase("encerrado");
    await aoEscolher(acao);
  }

  return <div className="selo-conclusao" role="status">
    <svg viewBox="0 0 44 44" aria-hidden="true" focusable="false">
      <circle cx="22" cy="22" r="20" />
      <path d="M12 23l7 7 13-14" />
    </svg>
    {fase !== "encerrado" && <span className="selo-conclusao-texto">{conclusao.textos.selo}</span>}
    {fase === "decisao" && <div className="selo-conclusao-decisao" onMouseEnter={abrirTip} onMouseLeave={agendarFecharTip}>
      {tipAberto && <div className="tip-conclusao" role="group" aria-label={conclusao.textos.pergunta}
        onMouseEnter={abrirTip} onMouseLeave={agendarFecharTip}>
        <p className="tip-conclusao-pergunta">{conclusao.textos.pergunta}</p>
        <label className="tip-conclusao-opcao">
          <input type="radio" name="decisao-conclusao" disabled={ocupado}
            onChange={() => void escolher("CONCLUSAO_SIM")} />
          {conclusao.textos.sim}
        </label>
        <label className="tip-conclusao-opcao">
          <input type="radio" name="decisao-conclusao" disabled={ocupado}
            onChange={() => void escolher("CONCLUSAO_NAO")} />
          {conclusao.textos.nao}
        </label>
      </div>}
      <button type="button" className="selo-conclusao-proxima" disabled={ocupado}
        onFocus={abrirTip} onBlur={agendarFecharTip}
        onClick={() => void escolher("CONCLUSAO_PROXIMA_TAREFA")}>{conclusao.textos.proxima_tarefa}</button>
    </div>}
  </div>;
}
