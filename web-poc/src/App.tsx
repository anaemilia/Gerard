import { useEffect, useState, type FormEvent } from "react";
import { api } from "./api";
import type { AcaoDisponivel, EstadoAtividade, EstadoWeb } from "./contratos";
import { Diagrama } from "./Diagrama";
import { BarraCategorias } from "./BarraCategorias";

type Mensagem = { texto: string; tipo: "neutra" | "erro" | "sucesso" };

export default function App() {
  const [estado, setEstado] = useState<EstadoWeb | null>(null);
  const [valor, setValor] = useState("");
  const [ocupado, setOcupado] = useState(false);
  const [mensagem, setMensagem] = useState<Mensagem>({ texto: "Carregando situação…", tipo: "neutra" });

  useEffect(() => {
    api.carregar().then((e) => { setEstado(e); setMensagem({ texto: "Preencha a incógnita e confirme.", tipo: "neutra" }); })
      .catch((erro: Error) => setMensagem({ texto: erro.message, tipo: "erro" }));
  }, []);

  function acao(id: AcaoDisponivel["id"]): AcaoDisponivel | undefined {
    return estado?.acoes_disponiveis.find((item) => item.id === id);
  }

  async function sortear(id: "SORTEAR_MEDIDAS" | "SORTEAR_RELACOES") {
    const controle = acao(id);
    if (!controle) return;
    setOcupado(true);
    try {
      setEstado(await api.executar(controle));
      setValor("");
      setMensagem({ texto: "Escolha a categoria correspondente à situação.", tipo: "neutra" });
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  function acoesCategoria() {
    return estado?.acoes_disponiveis.filter((item) => item.id === "ESCOLHER_CATEGORIA") ?? [];
  }

  async function executarClassificacao(controle: AcaoDisponivel) {
    setOcupado(true);
    try {
      const resultado = await api.classificar(controle);
      setEstado(resultado.estado);
      setMensagem(resultado.correta
        ? { texto: "Categoria aceita pelo domínio.", tipo: "sucesso" }
        : { texto: resultado.desfecho === "REEXPLICAR_CATEGORIA_APOS_LIMITE"
            ? `A categoria correta é ${resultado.estado.categoria_revelada}.`
            : "A escolha precisa ser confirmada.", tipo: "erro" });
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  function escolherCategoria(categoria: string) {
    const controle = acoesCategoria().find((item) => item.corpo?.categoria === categoria);
    if (controle) void executarClassificacao(controle);
  }

  function confirmarCategoria(concordou: boolean) {
    const controle = estado?.acoes_disponiveis.find((item) =>
      item.id === "CONFIRMAR_CATEGORIA_DIVERGENTE" && item.corpo?.concordou === concordou);
    if (controle) void executarClassificacao(controle);
  }

  async function submeter(evento: FormEvent) {
    evento.preventDefault();
    const numero = Number(valor);
    if (!Number.isInteger(numero) || numero < 0 || !estado || "modo" in estado) {
      setMensagem({ texto: "Informe um número natural.", tipo: "erro" }); return;
    }
    setOcupado(true);
    try {
      const resultado = await api.posicionar({ papel_id: estado.todo.id, valor: numero });
      setEstado(resultado.estado);
      setMensagem(resultado.aceita
        ? { texto: `Resposta aceita pelo domínio. Ação ${resultado.action_id}.`, tipo: "sucesso" }
        : { texto: `Resposta rejeitada pelo domínio: ${resultado.diagnostico}.`, tipo: "erro" });
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  async function reiniciar() {
    setOcupado(true);
    try { setEstado(await api.reiniciar()); setValor(""); setMensagem({ texto: "Tentativa reiniciada.", tipo: "neutra" }); }
    finally { setOcupado(false); }
  }

  return <main className="app-shell">
    <BarraCategorias ocupado={ocupado}
      podeSortearMedidas={Boolean(acao("SORTEAR_MEDIDAS"))}
      podeSortearRelacoes={Boolean(acao("SORTEAR_RELACOES"))}
      aoSortearMedidas={() => sortear("SORTEAR_MEDIDAS")}
      aoSortearRelacoes={() => sortear("SORTEAR_RELACOES")}
      categoriasHabilitadas={acoesCategoria().map((item) => String(item.corpo?.categoria))}
      categoriaSelecionada={estado && "modo" in estado ? estado.categoria_selecionada : null}
      aoEscolherCategoria={escolherCategoria} />
    {estado && <div className="activity-area">
      <section className="statement-panel" aria-labelledby="enunciado">
        <span className="help-mark" aria-hidden="true">?</span>
        <h1 id="enunciado">{estado.enunciado}</h1>
      </section>
      {!("modo" in estado) ? <div className="workspace">
        <section className="diagram-panel" aria-label="Área do diagrama"><Diagrama estado={estado} /></section>
        <aside className="response-panel" aria-label="Área de resposta">
          <div><p className="eyebrow">Composição de medidas</p><h2>Complete o diagrama</h2>
            <p className="relation">{estado.relacao}</p></div>
          <form className="answer-form" onSubmit={submeter}><label htmlFor="valor-todo">Valor do Todo</label>
            <div className="answer-controls"><input id="valor-todo" type="number" min="0" step="1" value={valor}
              onChange={(e) => setValor(e.target.value)} disabled={ocupado || estado.concluida} required />
              <button type="submit" disabled={ocupado || estado.concluida}>Confirmar</button></div></form>
          <p className={`message message-${mensagem.tipo}`} role="status" aria-live="polite">{mensagem.texto}</p>
          <div className="response-footer"><span className={`status ${estado.concluida ? "status-success" : ""}`}>
            {estado.concluida ? "Concluída" : "Em andamento"}</span>
            <button className="secondary" type="button" onClick={reiniciar} disabled={ocupado}>Reiniciar</button></div>
        </aside>
      </div> : <div className="workspace workspace-awaiting-category">
        <section className="diagram-panel" aria-label="Área do diagrama" />
        <aside className="response-panel" aria-label="Área complementar" />
      </div>}
      {"modo" in estado && estado.modo === "AGUARDANDO_CONFIRMACAO_CATEGORIA" &&
        <div className="modal-backdrop" role="presentation"><section className="confirmation-dialog" role="dialog" aria-modal="true" aria-labelledby="pergunta-categoria">
          <h2 id="pergunta-categoria">Confirme sua escolha</h2><p>{estado.questionamento}</p>
          <div className="dialog-actions"><button type="button" onClick={() => confirmarCategoria(true)} disabled={ocupado}>Sim</button>
            <button type="button" onClick={() => confirmarCategoria(false)} disabled={ocupado}>Não</button></div>
        </section></div>}
    </div>}
  </main>;
}
