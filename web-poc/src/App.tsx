import { useEffect, useReducer, useState } from "react";
import { api } from "./api";
import type { AcaoDisponivel, EstadoAtividade, EstadoWeb, FiguraCena,
  InteracaoPermitidaFigura } from "./contratos";
import { Diagrama } from "./Diagrama";
import { BarraCategorias } from "./BarraCategorias";
import { GeradorCenaGerard } from "./cena-gerard/GeradorCenaGerard";
import { estadoRepresentacoesInicial, reduzirEstadoRepresentacoes } from "./estadoRepresentacoes";

type Mensagem = { texto: string; tipo: "neutra" | "erro" | "sucesso" };

export default function App() {
  const [representacoes, enviarEventoRepresentacional] = useReducer(
    reduzirEstadoRepresentacoes, estadoRepresentacoesInicial);
  const estado = representacoes.snapshotServidor;
  const [ocupado, setOcupado] = useState(false);
  const [mensagem, setMensagem] = useState<Mensagem>({ texto: "Carregando situação…", tipo: "neutra" });

  useEffect(() => {
    api.carregar().then((e) => { receberSnapshot(e); setMensagem({ texto: "Preencha a incógnita e confirme.", tipo: "neutra" }); })
      .catch((erro: Error) => setMensagem({ texto: erro.message, tipo: "erro" }));
  }, []);

  function receberSnapshot(snapshot: EstadoWeb) {
    enviarEventoRepresentacional({ tipo: "SNAPSHOT_SERVIDOR_RECEBIDO", snapshot });
  }

  function acao(id: AcaoDisponivel["id"]): AcaoDisponivel | undefined {
    return estado?.acoes_disponiveis.find((item) => item.id === id);
  }

  async function sortear(id: "SORTEAR_MEDIDAS" | "SORTEAR_RELACOES") {
    const controle = acao(id);
    if (!controle) return;
    setOcupado(true);
    try {
      receberSnapshot(await api.executar(controle));
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
      receberSnapshot(resultado.estado);
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

  function iniciarEdicaoValor(figura: FiguraCena, interacao: InteracaoPermitidaFigura) {
    enviarEventoRepresentacional({ tipo: "EDICAO_VALOR_INICIADA",
      elementoId: figura.id, actionId: interacao.acao_id });
  }

  async function confirmarValorEditado() {
    if (!estado || !("modo" in estado) || !representacoes.elementoEmEdicao) return;
    const figura = estado.cena?.figuras.find(
      (item) => item.id === representacoes.elementoEmEdicao);
    const interacao = figura?.interacoes_permitidas.find(
      (item) => item.tipo === "EDITAR_VALOR");
    const controle = estado.acoes_disponiveis.find((item) =>
      item.id === interacao?.acao_id && item.corpo?.papel_id === interacao.papel_id);
    const texto = representacoes.valoresEmEdicao[representacoes.elementoEmEdicao] ?? "";
    const valor = Number(texto);
    if (!figura || !interacao || !controle || !Number.isInteger(valor)) {
      setMensagem({ texto: "Digite um número inteiro.", tipo: "erro" });
      return;
    }
    setOcupado(true);
    try {
      const resultado = await api.posicionar(controle, valor);
      receberSnapshot(resultado.estado);
      setMensagem(resultado.aceita
        ? { texto: "Valor aceito pelo domínio.", tipo: "sucesso" }
        : { texto: resultado.chave_mensagem ?? "Valor rejeitado pelo domínio.", tipo: "erro" });
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  const figuraEmEdicao = estado && "modo" in estado && representacoes.elementoEmEdicao
    ? estado.cena?.figuras.find((item) => item.id === representacoes.elementoEmEdicao)
    : undefined;

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
      <div className="workspace workspace-awaiting-category">
        <section className="diagram-panel" aria-label="Área do diagrama">
          {"modo" in estado ? estado.cena && <GeradorCenaGerard cena={estado.cena}
            posicoesEmEdicao={representacoes.posicoesEmEdicao}
            aoEditarValor={iniciarEdicaoValor} /> : <Diagrama estado={estado} />}
        </section>
        <aside className="response-panel" aria-label="Área complementar">
          {figuraEmEdicao && <div className="value-editor">
            <label htmlFor="valor-papel">{figuraEmEdicao.rotulo}</label>
            <input id="valor-papel" type="number" step="1" inputMode="numeric" autoFocus
              value={representacoes.valoresEmEdicao[figuraEmEdicao.id] ?? ""}
              onChange={(evento) => enviarEventoRepresentacional({
                tipo: "VALOR_EM_EDICAO_ALTERADO", elementoId: figuraEmEdicao.id,
                valor: evento.target.value })} disabled={ocupado} />
            <div className="dialog-actions"><button type="button"
              onClick={() => void confirmarValorEditado()} disabled={ocupado}>Confirmar</button>
              <button type="button" onClick={() => enviarEventoRepresentacional(
                { tipo: "RASCUNHO_DESCARTADO" })} disabled={ocupado}>Cancelar</button></div>
          </div>}
        </aside>
      </div>
      {"modo" in estado && estado.modo === "AGUARDANDO_CONFIRMACAO_CATEGORIA" &&
        <div className="modal-backdrop" role="presentation"><section className="confirmation-dialog" role="dialog" aria-modal="true" aria-labelledby="pergunta-categoria">
          <h2 id="pergunta-categoria">Confirme sua escolha</h2><p>{estado.questionamento}</p>
          <div className="dialog-actions"><button type="button" onClick={() => confirmarCategoria(true)} disabled={ocupado}>Sim</button>
            <button type="button" onClick={() => confirmarCategoria(false)} disabled={ocupado}>Não</button></div>
        </section></div>}
    </div>}
  </main>;
}
