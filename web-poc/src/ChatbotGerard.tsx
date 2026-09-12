import { useState } from "react";
import { interpretarMensagem, mensagemComunicabilidade, type AreaComunicabilidade } from "./mensagensComunicabilidade";

type Linha = { autor: "Gérard" | "Você"; texto: string };

export function IconeChat() {
  return <svg viewBox="0 0 34 26" aria-hidden="true"><path d="M3 3h23a6 6 0 0 1 0 16H11c-2 2-4 3-7 4 2-2 2-4 2-5a7 7 0 0 1-3-6V9a6 6 0 0 1 6-6"/><circle cx="12" cy="11" r="1.5"/><circle cx="18" cy="11" r="1.5"/><circle cx="24" cy="11" r="1.5"/></svg>;
}

export function ChatbotGerard({ aberto, aoFechar }: { aberto: boolean; aoFechar: () => void }) {
  const [area, setArea] = useState<AreaComunicabilidade>("TEXTO");
  const [entrada, setEntrada] = useState("");
  const [linhas, setLinhas] = useState<Linha[]>([
    { autor: "Gérard", texto: "Escreva sua dúvida ou escolha a área em que precisa de ajuda." }
  ]);
  if (!aberto) return null;
  function escolherArea(novaArea: AreaComunicabilidade) {
    setArea(novaArea);
    setLinhas((atuais) => [...atuais, { autor: "Gérard", texto: mensagemComunicabilidade(novaArea, "DUVIDA") }]);
  }
  function enviar() {
    const texto = entrada.trim(); if (!texto) return;
    const resultado = interpretarMensagem(texto, area);
    setArea(resultado.area); setEntrada("");
    setLinhas((atuais) => [...atuais, { autor: "Você", texto }, { autor: "Gérard", texto: resultado.resposta }]);
  }
  return <div className="chat-backdrop" role="presentation" onMouseDown={(e) => { if (e.target === e.currentTarget) aoFechar(); }}>
    <section className="chat-dialog" role="dialog" aria-modal="true" aria-labelledby="chat-titulo">
      <header><h2 id="chat-titulo">Como posso ajudar?</h2><button type="button" onClick={aoFechar} aria-label="Fechar">×</button></header>
      <div className="chat-body"><img src="/gerard_vergnaud.png" alt="Foto de Gérard Vergnaud" />
        <div className="chat-history" aria-live="polite">{linhas.map((linha, i) => <p key={i}><strong>{linha.autor}:</strong> {linha.texto}</p>)}</div>
      </div>
      <div className="chat-areas">
        <button type="button" onClick={() => escolherArea("TEXTO")}>Texto</button>
        <button type="button" onClick={() => escolherArea("VERGNAUD")}>Diagrama de Vergnaud</button>
        <button type="button" onClick={() => escolherArea("COMPLEMENTAR")}>Diagrama complementar</button>
      </div>
      <form className="chat-input" onSubmit={(e) => { e.preventDefault(); enviar(); }}><input value={entrada} onChange={(e) => setEntrada(e.target.value)} aria-label="Digite sua dúvida" autoFocus/><button type="submit">Enviar</button></form>
    </section>
  </div>;
}
