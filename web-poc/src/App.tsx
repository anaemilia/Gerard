import { useEffect, useReducer, useState } from "react";
import { api } from "./api";
import type { AcaoDisponivel, EstadoWeb, FiguraCena,
  InteracaoPermitidaFigura } from "./contratos";
import { BarraCategorias } from "./BarraCategorias";
import { EdicaoValorFigura } from "./EdicaoValorFigura";
import { EscolhaSinalFigura } from "./EscolhaSinalFigura";
import { EnunciadoInterativo } from "./EnunciadoInterativo";
import { MenuAjudaContextual } from "./MenuAjudaContextual";
import { GeradorCenaGerard } from "./cena-gerard/GeradorCenaGerard";
import { estadoRepresentacoesInicial, reduzirEstadoRepresentacoes } from "./estadoRepresentacoes";

export default function App() {
  const [representacoes, enviarEventoRepresentacional] = useReducer(
    reduzirEstadoRepresentacoes, estadoRepresentacoesInicial);
  const estado = representacoes.snapshotServidor;
  const [ocupado, setOcupado] = useState(false);
  const [mensagemOperacao, setMensagemOperacao] = useState<string | null>(null);
  const [dicaVisivel, setDicaVisivel] = useState(false);
  const [figuraDestacadaId, setFiguraDestacadaId] = useState<string | null>(null);
  const [avisoSinal, setAvisoSinal] = useState<{ figuraId: string; mensagem: string } | null>(null);

  useEffect(() => {
    api.carregar().then(receberSnapshot).catch((erro: Error) => console.error(erro));
  }, []);

  useEffect(() => {
    if (!avisoSinal) return;
    const temporizador = setTimeout(() => setAvisoSinal(null), 4000);
    return () => clearTimeout(temporizador);
  }, [avisoSinal]);

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
    } catch (erro) { console.error(erro); }
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
    } catch (erro) { console.error(erro); }
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

  function aoConfirmarDigitacao() {
    if (!representacoes.elementoEmEdicao) return;
    const texto = representacoes.valoresEmEdicao[representacoes.elementoEmEdicao] ?? "";
    if (!Number.isInteger(Number(texto))) return;
    enviarEventoRepresentacional({ tipo: "VALOR_PROPOSTO_PARA_CONFIRMACAO",
      elementoId: representacoes.elementoEmEdicao });
  }

  function aoNegarValor() {
    enviarEventoRepresentacional({ tipo: "CONFIRMACAO_NEGADA" });
  }

  async function aoConfirmarValor() {
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
      return;
    }
    setOcupado(true);
    try {
      const resultado = await api.posicionar(controle, valor);
      receberSnapshot(resultado.estado);
      enviarEventoRepresentacional({ tipo: "CONFIRMACAO_ENVIADA" });
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  async function ajustarQuadradinho(delta: 1 | -1) {
    const controle = estado?.acoes_disponiveis.find((item) => item.id === "AJUSTAR_QUADRADINHO");
    if (!controle) return;
    setOcupado(true);
    try {
      const resultado = await api.ajustarQuadradinho(controle, delta);
      receberSnapshot(resultado.estado);
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  function aoSoltarNoDiagrama(papelId: string, x: number, y: number, alvoFiguraId?: string | null) {
    // Se a atração magnética já identificou um alvo (a até 48px), a soltura
    // conta pra ele mesmo que o ponto exato do cursor não esteja em cima
    // (deveCentralizarAoSoltar, Main.java) — só cai no elementFromPoint puro
    // quando nenhum alvo estava em atração.
    const figuraId = alvoFiguraId
      ?? document.elementFromPoint(x, y)?.closest("[data-figura-id]")?.getAttribute("data-figura-id");
    const figura = estado?.cena?.figuras.find((item) => item.id === figuraId);
    // Soltar fora de qualquer figura só encerra o gesto, sem efeito — mesmo
    // invariante do desktop (gerard-ajuda-adaptativa): não é erro nem ação.
    if (figura) void aoSoltarElementoNoDiagrama(figura, papelId);
  }

  async function aoSoltarElementoNoDiagrama(figura: FiguraCena, papelId: string) {
    if (papelId !== figura.chave_papel_semantico) {
      return;
    }
    // Soltar o "?" só engata a incógnita na caixa (protocolo mouse-texto,
    // Main.java) — marcado no servidor (ver ConfirmacaoValorWeb/
    // engatarIncognita), não só no cliente: a digitação em si abre com o
    // duplo-clique subsequente sobre a caixa já engatada (ver
    // FiguraCenaGerard), nunca automaticamente ao soltar.
    const engatar = figura.interacoes_permitidas.find((item) => item.tipo === "ENGATAR_INCOGNITA");
    if (engatar) {
      setOcupado(true);
      try {
        const resultado = await api.engatarIncognita({
          id: "ENGATAR_INCOGNITA", metodo: "POST",
          href: "/api/acoes/engatar-incognita", corpo: { papel_id: engatar.papel_id }
        });
        receberSnapshot(resultado.estado);
      } catch (erro) { console.error(erro); }
      finally { setOcupado(false); }
      return;
    }
    const posicionar = figura.interacoes_permitidas.find((item) => item.tipo === "POSICIONAR_CONHECIDO");
    if (!posicionar) {
      return;
    }
    setOcupado(true);
    try {
      const resultado = await api.posicionarConhecido({
        id: "POSICIONAR_CONHECIDO", metodo: "POST",
        href: "/api/acoes/posicionar-conhecido", corpo: { papel_id: posicionar.papel_id }
      });
      receberSnapshot(resultado.estado);
    } catch (erro) { console.error(erro); }
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
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  async function escolherSinal(papelId: string, figuraId: string, sinal: "+" | "-") {
    const controle = estado?.acoes_disponiveis.find((item) =>
      item.id === "ESCOLHER_SINAL_NUMERO_RELATIVO"
      && item.corpo?.papel_id === papelId && item.corpo?.sinal === sinal);
    if (!controle) return;
    setOcupado(true);
    try {
      const resultado = await api.escolherSinal(controle);
      receberSnapshot(resultado.estado);
      setAvisoSinal(resultado.mensagem_sinal_divergente
        ? { figuraId, mensagem: resultado.mensagem_sinal_divergente } : null);
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  const figuraEmEdicao = estado && representacoes.elementoEmEdicao
    ? estado.cena?.figuras.find((item) => item.id === representacoes.elementoEmEdicao)
    : undefined;
  // Presente só nas categorias com modelagem ternária (Comparação/
  // Transformação de Medidas, Transformação de Relação) — ver
  // EstadoModelagemTernaria.papel_aguardando_sinal.
  const modelagemComSinal = estado && estado.modelagem
    && "papel_aguardando_sinal" in estado.modelagem
    ? estado.modelagem
    : undefined;
  const figuraAguardandoSinal = modelagemComSinal?.papel_aguardando_sinal
    ? estado?.cena?.figuras.find(
        (item) => item.chave_papel_semantico === modelagemComSinal.papel_aguardando_sinal)
    : undefined;
  const modelagemEscolhaOperacao = estado && estado.modelagem
    && "categoria" in estado.modelagem
    && (estado.modelagem.categoria === "COMPOSICAO_TRANSFORMACOES"
      || estado.modelagem.categoria === "COMPOSICAO_RELACOES")
    ? estado.modelagem
    : undefined;
  // Generalizado por categoria: qualquer modelagem que exponha
  // material_concreto_disponivel (hoje só Composição de Medidas) acende o
  // painel — nenhuma checagem de categoria hardcoded aqui, ver
  // GeradorCenaDiagramaAditivo.gerarMaterialConcreto no servidor.
  const modelagemMaterialConcreto = estado && estado.modelagem
    && "material_concreto_disponivel" in estado.modelagem
    && estado.modelagem.material_concreto_disponivel
    ? estado.modelagem
    : undefined;
  const cenaMaterialConcreto = modelagemMaterialConcreto ? estado?.cena_material_concreto : undefined;
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
          ? <EnunciadoInterativo key={estado.modelagem?.tentativa_id ?? estado.situacao_id}
              elementos={estado.elementos_texto}
              modelagemConcluida={estado.modelagem?.concluida === true}
              figuras={estado.cena?.figuras ?? []}
              organizadores={estado.vocabulario_texto?.candidatos_organizadores_informacao ?? []}
              modeloPalavraComum={estado.vocabulario_texto?.modelo_palavra_comum ?? null}
              aoSoltar={aoSoltarNoDiagrama} aoAtualizarAlvo={setFiguraDestacadaId} />
          : <h1 id="enunciado">{estado.enunciado}</h1>}
      </section>
      <div className="workspace workspace-awaiting-category">
        <section className={`diagram-panel${estado.modelagem?.concluida === true ? " diagram-panel-concluido" : ""}`} aria-label="Área do diagrama">
          <MenuAjudaContextual item={itemAjuda("VERGNAUD")} />
          {estado.cena && <GeradorCenaGerard cena={estado.cena}
            posicoesEmEdicao={representacoes.posicoesEmEdicao}
            aoEditarValor={iniciarEdicaoValor}
            figuraDestacadaId={figuraDestacadaId}
            seletorOperacao={modelagemEscolhaOperacao ? { modelagem: modelagemEscolhaOperacao,
              mensagemErro: mensagemOperacao, ocupado, aoEscolher: escolherOperacao } : undefined} />}
          {figuraEmEdicao && <EdicaoValorFigura figuraId={figuraEmEdicao.id}
            papelNome={figuraEmEdicao.rotulo} pergunta={estado.confirmacao_valor_papel}
            modo={representacoes.confirmando ? "confirmando" : "digitando"}
            valor={representacoes.valoresEmEdicao[figuraEmEdicao.id] ?? ""} ocupado={ocupado}
            aoAlterarValor={(valor) => enviarEventoRepresentacional({
              tipo: "VALOR_EM_EDICAO_ALTERADO", elementoId: figuraEmEdicao.id, valor })}
            aoConfirmarDigitacao={aoConfirmarDigitacao}
            aoConfirmarValor={() => void aoConfirmarValor()}
            aoNegarValor={aoNegarValor} />}
          {figuraAguardandoSinal && <EscolhaSinalFigura figuraId={figuraAguardandoSinal.id}
            papelNome={figuraAguardandoSinal.rotulo} ocupado={ocupado}
            mensagemDivergente={avisoSinal?.figuraId === figuraAguardandoSinal.id
              ? avisoSinal.mensagem : null}
            aoEscolherSinal={(sinal) => void escolherSinal(
              figuraAguardandoSinal.chave_papel_semantico, figuraAguardandoSinal.id, sinal)} />}
        </section>
        <aside className="response-panel" aria-label="Área complementar">
          <MenuAjudaContextual item={itemAjuda("COMPLEMENTAR")} />
          {cenaMaterialConcreto && <section className="material-concreto" aria-label="Material concreto">
            {modelagemMaterialConcreto?.material_concreto_texto &&
              <p className="material-concreto-aviso">{modelagemMaterialConcreto.material_concreto_texto}</p>}
            <GeradorCenaGerard cena={cenaMaterialConcreto} ocupado={ocupado}
              aoAjustarQuadradinho={(_papelId, delta) => void ajustarQuadradinho(delta)}
              textoAdicionarQuadradinho={modelagemMaterialConcreto?.material_concreto_texto_adicionar}
              textoRemoverQuadradinho={modelagemMaterialConcreto?.material_concreto_texto_remover} />
          </section>}
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
