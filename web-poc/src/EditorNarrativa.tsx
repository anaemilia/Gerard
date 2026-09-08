import { useEffect, useState } from "react";
import type { ElementoTexto } from "./contratos";

type Peca = ElementoTexto;
type Carga = { origem: "texto"; indice: number } | { origem: "saco"; peca: Peca };

/** Rascunho narrativo efêmero: nenhuma alteração é persistida ou interpretada no cliente. */
export function EditorNarrativa({ elementos, organizadores, modeloPalavraComum, aoFechar }: {
  elementos: readonly ElementoTexto[]; organizadores: readonly ElementoTexto[];
  modeloPalavraComum: ElementoTexto | null; aoFechar: () => void;
}) {
  const [pecas, setPecas] = useState<Peca[]>(() => elementos.map((p) => ({ ...p })));
  const [sacoComum, setSacoComum] = useState<Peca[]>([]);
  const [sacoOrganizadores, setSacoOrganizadores] = useState<Peca[]>(() => organizadores.map((p) => ({ ...p })));
  const [novaPalavra, setNovaPalavra] = useState("");

  useEffect(() => setPecas(elementos.map((p) => ({ ...p }))), [elementos]);
  useEffect(() => setSacoOrganizadores(organizadores.map((p) => ({ ...p }))), [organizadores]);

  function carga(evento: React.DragEvent): Carga | null {
    try { return JSON.parse(evento.dataTransfer.getData("application/x-gerard-palavra")) as Carga; }
    catch { return null; }
  }
  function gravarCarga(evento: React.DragEvent, valor: Carga) {
    evento.dataTransfer.effectAllowed = "move";
    evento.dataTransfer.setData("application/x-gerard-palavra", JSON.stringify(valor));
  }
  function inserir(c: Carga, indice: number) {
    if (c.origem === "texto") {
      setPecas((atuais) => {
        const copia = [...atuais]; const [movida] = copia.splice(c.indice, 1);
        copia.splice(c.indice < indice ? indice - 1 : indice, 0, movida); return copia;
      });
      return;
    }
    const valor = c.peca.valor.trim(); if (!valor) return;
    setPecas((atuais) => {
      const copia = [...atuais]; copia.splice(indice, 0, {
        ...c.peca, id: `rascunho.${Date.now()}.${indice}`, valor
      }); return copia;
    });
  }
  function retirar(indice: number) {
    const peca = pecas[indice];
    if (!peca?.manipulavel) return;
    setPecas((atuais) => atuais.filter((_, i) => i !== indice));
    if (peca.saco_destino === "COMUM" && !sacoComum.some((item) => item.valor === peca.valor)) {
      setSacoComum((atuais) => [...atuais, peca]);
    }
    if (peca.saco_destino === "ORGANIZADORES"
        && !sacoOrganizadores.some((item) => item.valor === peca.valor)) {
      setSacoOrganizadores((atuais) => [...atuais, peca]);
    }
  }
  function adicionarComum() {
    const valor = novaPalavra.trim(); if (!valor) return;
    if (modeloPalavraComum && !sacoComum.some((item) => item.valor === valor)) {
      setSacoComum((atuais) => [...atuais, {
        ...modeloPalavraComum,
        id: `rascunho.saco.${Date.now()}`,
        valor
      }]);
    }
    setNovaPalavra("");
  }

  return <div className="editor-narrativa">
    <div className="editor-narrativa-topo"><strong>Edite o enunciado</strong>
      <button type="button" onClick={aoFechar}>Concluir edição</button></div>
    <div className="linha-palavras" onDragOver={(e) => e.preventDefault()}
      onDrop={(e) => { e.preventDefault(); const c = carga(e); if (c) inserir(c, pecas.length); }}>
      {pecas.map((peca, indice) => <span key={peca.id + "." + indice}
        className={`peca-palavra peca-${peca.tipo.toLowerCase()}${!peca.manipulavel ? " peca-stopword" : ""}`}
        draggable={peca.manipulavel}
        onDragStart={(e) => gravarCarga(e, { origem: "texto", indice })}
        onDragOver={(e) => { if (peca.manipulavel) e.preventDefault(); }}
        onDrop={(e) => { e.preventDefault(); e.stopPropagation(); const c = carga(e); if (c) inserir(c, indice); }}>
        {peca.valor}{peca.manipulavel && <button type="button" aria-label={`Retirar ${peca.valor}`}
          onClick={() => retirar(indice)}>×</button>}
      </span>)}
    </div>
    <div className="sacos-palavras">
      <section><h2>Palavras comuns</h2><div className="entrada-palavra">
        <input value={novaPalavra} onChange={(e) => setNovaPalavra(e.target.value)}
          onKeyDown={(e) => { if (e.key === "Enter") adicionarComum(); }}
          placeholder="Nova palavra" disabled={!modeloPalavraComum} />
        <button type="button" onClick={adicionarComum} disabled={!modeloPalavraComum}>Adicionar</button></div>
        <div className="conteudo-saco">{sacoComum.map((peca, indice) => <button type="button"
          key={peca.id + "." + indice} draggable
          onDragStart={(e) => gravarCarga(e, { origem: "saco", peca })}
          onClick={() => inserir({ origem: "saco", peca }, pecas.length)}>{peca.valor}</button>)}</div></section>
      <section><h2>Candidatos a organizadores da informação</h2><div className="conteudo-saco">
        {sacoOrganizadores.map((peca, indice) => <button type="button"
          key={peca.id + "." + indice} draggable
          onDragStart={(e) => gravarCarga(e, { origem: "saco", peca })}
          onClick={() => inserir({ origem: "saco", peca }, pecas.length)}>{peca.valor}</button>)}</div></section>
    </div>
    <p className="nota-rascunho">Rascunho em memória. Stopwords permanecem no texto, mas não são peças independentes.</p>
  </div>;
}
