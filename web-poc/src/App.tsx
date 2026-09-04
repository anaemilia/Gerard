import { useEffect, useReducer, useState } from "react";
import { api } from "./api";
import type { AcaoDisponivel, EstadoWeb, FiguraCena,
  InteracaoPermitidaFigura } from "./contratos";
import { BarraCategorias } from "./BarraCategorias";
import { EnunciadoInterativo } from "./EnunciadoInterativo";
import { MaterialConcretoQuadradinhos } from "./MaterialConcretoQuadradinhos";
import { MenuAjudaContextual } from "./MenuAjudaContextual";
import { GeradorCenaGerard } from "./cena-gerard/GeradorCenaGerard";
import { estadoRepresentacoesInicial, reduzirEstadoRepresentacoes } from "./estadoRepresentacoes";

type Mensagem = { texto: string; tipo: "neutra" | "erro" | "sucesso" };

export default function App() {
  const [representacoes, enviarEventoRepresentacional] = useReducer(
    reduzirEstadoRepresentacoes, estadoRepresentacoesInicial);
  const estado = representacoes.snapshotServidor;
  const [ocupado, setOcupado] = useState(false);
  const [mensagem, setMensagem] = useState<Mensagem>({ texto: "Carregando situação…", tipo: "neutra" });
  const [mensagemOperacao, setMensagemOperacao] = useState<string | null>(null);
  const [dicaVisivel, setDicaVisivel] = useState(false);

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
    const modelagem = estado?.modelagem;
    const papeis = modelagem && "parte1" in modelagem
      ? [modelagem.parte1, modelagem.parte2, modelagem.todo]
      : modelagem && "papeis" in modelagem ? modelagem.papeis : undefined;
    const papel = papeis?.find((item) => item.id === interacao.papel_id);
    const valorInicial = papel?.conhecido && papel.valor !== null ? String(papel.valor) : undefined;
    enviarEventoRepresentacional({ tipo: "EDICAO_VALOR_INICIADA",
      elementoId: figura.id, actionId: interacao.acao_id, valorInicial });
  }

  async function confirmarValorEditado() {
    if (!estado || !representacoes.elementoEmEdicao) return;
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

  async function ajustarQuadradinho(delta: 1 | -1) {
    const controle = estado?.acoes_disponiveis.find((item) => item.id === "AJUSTAR_QUADRADINHO");
    if (!controle) return;
    setOcupado(true);
    try {
      const resultado = await api.ajustarQuadradinho(controle, delta);
      receberSnapshot(resultado.estado);
      if (resultado.limite_atingido) {
        setMensagem({ texto: resultado.chave_mensagem ?? "Limite atingido.", tipo: "erro" });
      }
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  function aoSoltarNoDiagrama(papelId: string, x: number, y: number) {
    const alvo = document.elementFromPoint(x, y)?.closest("[data-figura-id]");
    const figuraId = alvo?.getAttribute("data-figura-id");
    const figura = estado?.cena?.figuras.find((item) => item.id === figuraId);
    // Soltar fora de qualquer figura só encerra o gesto, sem efeito — mesmo
    // invariante do desktop (gerard-ajuda-adaptativa): não é erro nem ação.
    if (figura) void aoSoltarElementoNoDiagrama(figura, papelId);
  }

  async function aoSoltarElementoNoDiagrama(figura: FiguraCena, papelId: string) {
    // Erro só ao soltar, nunca durante o arrasto (protocolo de mouse, ver
    // gerard-scaffolding-interacao): soltar em cima da caixa errada é
    // feedback de erro, não silêncio.
    if (papelId !== figura.chave_papel_semantico) {
      setMensagem({ texto: "Esse elemento não pertence a essa caixa.", tipo: "erro" });
      return;
    }
    const editar = figura.interacoes_permitidas.find((item) => item.tipo === "EDITAR_VALOR");
    if (editar) {
      iniciarEdicaoValor(figura, editar);
      return;
    }
    const posicionar = figura.interacoes_permitidas.find((item) => item.tipo === "POSICIONAR_CONHECIDO");
    if (!posicionar) {
      // Duas razões distintas pra não ter a ação: o valor já foi posicionado
      // (normal, neutro) ou esta situação não tem nenhuma atividade
      // implementada pra essa categoria (achado ao testar: acontece com
      // Transformação de Relação sem dado rico curado — a caixa fica sem
      // nenhuma interação desde o início, não é "já posicionado").
      setMensagem(figura.conhecido
        ? { texto: "Esse valor já está posicionado.", tipo: "neutra" }
        : { texto: "Esta situação ainda não tem atividade implementada nesta categoria.", tipo: "erro" });
      return;
    }
    setOcupado(true);
    try {
      const resultado = await api.posicionarConhecido({
        id: "POSICIONAR_CONHECIDO", metodo: "POST",
        href: "/api/acoes/posicionar-conhecido", corpo: { papel_id: posicionar.papel_id }
      });
      receberSnapshot(resultado.estado);
      setMensagem(resultado.aceita
        ? { texto: "Valor posicionado.", tipo: "neutra" }
        : { texto: "Não foi possível posicionar esse valor.", tipo: "erro" });
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  async function escolherOperacao(operacao: "SOMA" | "SUBTRACAO") {
    const controle = estado?.acoes_disponiveis.find((item) => item.id === "ESCOLHER_OPERACAO_RELACAO");
    if (!controle) return;
    setOcupado(true);
    try {
      const resultado = await api.escolherOperacao(controle, operacao);
      receberSnapshot(resultado.estado);
      setMensagemOperacao(resultado.aceita ? null : (resultado.chave_mensagem ?? "Operação incorreta."));
    } catch (erro) { setMensagem({ texto: (erro as Error).message, tipo: "erro" }); }
    finally { setOcupado(false); }
  }

  const figuraEmEdicao = estado && representacoes.elementoEmEdicao
    ? estado.cena?.figuras.find((item) => item.id === representacoes.elementoEmEdicao)
    : undefined;
  const modelagemEscolhaOperacao = estado && estado.modelagem
    && "categoria" in estado.modelagem
    && (estado.modelagem.categoria === "COMPOSICAO_TRANSFORMACOES"
      || estado.modelagem.categoria === "COMPOSICAO_RELACOES")
    ? estado.modelagem
    : undefined;
  const modelagemMaterialConcreto = estado && estado.modelagem
    && "categoria" in estado.modelagem
    && estado.modelagem.categoria === "COMPOSICAO_MEDIDAS"
    && estado.modelagem.material_concreto_disponivel
    ? estado.modelagem
    : undefined;
  function itemAjuda(area: "TEXTO" | "VERGNAUD" | "COMPLEMENTAR") {
    return estado?.ajuda_contextual?.find((item) => item.area === area);
  }

  return <main className="app-shell">
    <BarraCategorias ocupado={ocupado}
      podeSortearMedidas={Boolean(acao("SORTEAR_MEDIDAS"))}
      podeSortearRelacoes={Boolean(acao("SORTEAR_RELACOES"))}
      aoSortearMedidas={() => sortear("SORTEAR_MEDIDAS")}
      aoSortearRelacoes={() => sortear("SORTEAR_RELACOES")}
      categoriasHabilitadas={acoesCategoria().map((item) => String(item.corpo?.categoria))}
      categoriaSelecionada={estado ? estado.categoria_selecionada : null}
      aoEscolherCategoria={escolherCategoria} />
    {mensagem.texto && <p className={`message message-${mensagem.tipo}`} role={mensagem.tipo === "erro" ? "alert" : "status"}>
      {mensagem.texto}
    </p>}
    {estado && <div className="activity-area">
      <section className="statement-panel" aria-labelledby="enunciado">
        {estado.dica_proximo_passo
          ? <div className="help-mark-wrap" onMouseEnter={() => setDicaVisivel(true)}
              onMouseLeave={() => setDicaVisivel(false)}
              onFocus={() => setDicaVisivel(true)}
              onBlur={(evento) => { if (!evento.currentTarget.contains(evento.relatedTarget as Node)) setDicaVisivel(false); }}>
              <button type="button" className="help-mark" aria-expanded={dicaVisivel}
                aria-label="Qual é o próximo passo?">?</button>
              {dicaVisivel && <p className="help-tip" role="status">{estado.dica_proximo_passo}</p>}
            </div>
          : itemAjuda("TEXTO")
            ? <MenuAjudaContextual item={itemAjuda("TEXTO")} />
            : <span className="help-mark" aria-hidden="true">?</span>}
        {estado.elementos_texto
          ? <EnunciadoInterativo elementos={estado.elementos_texto} aoSoltar={aoSoltarNoDiagrama} />
          : <h1 id="enunciado">{estado.enunciado}</h1>}
      </section>
      <div className="workspace workspace-awaiting-category">
        <section className="diagram-panel" aria-label="Área do diagrama">
          <MenuAjudaContextual item={itemAjuda("VERGNAUD")} />
          {estado.cena && <GeradorCenaGerard cena={estado.cena}
            posicoesEmEdicao={representacoes.posicoesEmEdicao}
            aoEditarValor={iniciarEdicaoValor}
            seletorOperacao={modelagemEscolhaOperacao ? { modelagem: modelagemEscolhaOperacao,
              mensagemErro: mensagemOperacao, ocupado, aoEscolher: escolherOperacao } : undefined} />}
        </section>
        <aside className="response-panel" aria-label="Área complementar">
          <MenuAjudaContextual item={itemAjuda("COMPLEMENTAR")} />
          {modelagemMaterialConcreto && <MaterialConcretoQuadradinhos
            modelagem={modelagemMaterialConcreto} ocupado={ocupado}
            aoAjustar={(delta) => void ajustarQuadradinho(delta)} />}
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
      {estado.modo === "AGUARDANDO_CONFIRMACAO_CATEGORIA" &&
        <div className="modal-backdrop" role="presentation"><section className="confirmation-dialog" role="dialog" aria-modal="true" aria-labelledby="pergunta-categoria">
          <h2 id="pergunta-categoria">Confirme sua escolha</h2><p>{estado.questionamento}</p>
          <div className="dialog-actions"><button type="button" onClick={() => confirmarCategoria(true)} disabled={ocupado}>Sim</button>
            <button type="button" onClick={() => confirmarCategoria(false)} disabled={ocupado}>Não</button></div>
        </section></div>}
    </div>}
  </main>;
}
