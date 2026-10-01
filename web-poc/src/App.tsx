import { useEffect, useReducer, useRef, useState } from "react";
import { api } from "./api";
import type { AcaoDisponivel, EstadoWeb, FiguraCena,
  IdiomaSituacaoWeb, InteracaoPermitidaFigura } from "./contratos";
import { BarraCategorias } from "./BarraCategorias";
import { EdicaoValorFigura } from "./EdicaoValorFigura";
import { EixoNumericoFigura } from "./EixoNumericoFigura";
import { AvisoPosicionamentoFigura } from "./AvisoPosicionamentoFigura";
import { EnunciadoInterativo } from "./EnunciadoInterativo";
import { IconeAjudaContextual, MenuAjudaContextual } from "./MenuAjudaContextual";
import { GeradorCenaGerard } from "./cena-gerard/GeradorCenaGerard";
import { ChatbotGerard } from "./ChatbotGerard";
import { estadoRepresentacoesInicial, reduzirEstadoRepresentacoes } from "./estadoRepresentacoes";
import { EstadoSemanticoExploratorio } from "./estadoSemanticoExploratorio";

// Ícones dos botões contextuais portados de Main.java (criarIconeRestaurar/
// criarIconeIdiomaSituacao) — mesma affordance convencional descrita lá:
// "seta circular para restaurar/refazer o estado", "A"/"文" com setas de
// troca para idioma (auditoria de acoplamento de Main, 2026-09-17).
function IconeRestaurar() {
  return <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
    <path d="M4 12a8 8 0 1 1 2.5 5.8" />
    <path d="M4 6v6h6" />
  </svg>;
}
function IconeIdioma() {
  return <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
    <text x="1" y="11" fontSize="9" fontWeight="700" stroke="none" fill="currentColor">A</text>
    <text x="13" y="11" fontSize="8" stroke="none" fill="currentColor">文</text>
    <path d="M7 20h12m-4-3 4 3-4 3" />
  </svg>;
}

// Selo de conclusão da modelagem (SeloConclusaoModelagem.java): marca de visto
// sempre visível + texto ui.completion.completed de mensagens_pt.properties
// ("Modelagem concluída"), que some sozinho por CSS depois de 4 s — o sucesso não
// depende só da cor. role="status" faz o leitor de tela anunciar o texto.
function SeloConclusao() {
  return <div className="selo-conclusao" role="status">
    <svg viewBox="0 0 44 44" aria-hidden="true" focusable="false">
      <circle cx="22" cy="22" r="20" />
      <path d="M12 23l7 7 13-14" />
    </svg>
    <span className="selo-conclusao-texto">Modelagem concluída</span>
  </div>;
}

// Porta descricaoBotaoIdiomaSituacao (Main.java): tooltip do botão de idioma
// da situação = ui.tooltip.problemLanguage + ": " + nome do idioma atual
// (mensagens_pt.properties), mesmo texto-base do desktop.
function descricaoBotaoIdiomaSituacao(idiomas: readonly IdiomaSituacaoWeb[]) {
  const base = "Alterar o idioma desta situação-problema";
  const atual = idiomas.find((idioma) => idioma.atual);
  return atual ? `${base}: ${atual.nome}` : base;
}

export default function App() {
  const [representacoes, enviarEventoRepresentacional] = useReducer(
    reduzirEstadoRepresentacoes, estadoRepresentacoesInicial);
  const estado = representacoes.snapshotServidor;
  const [ocupado, setOcupado] = useState(false);
  const [mensagemOperacao, setMensagemOperacao] = useState<string | null>(null);
  const [dicaVisivel, setDicaVisivel] = useState(false);
  const [figuraDestacadaId, setFiguraDestacadaId] = useState<string | null>(null);
  const [avisoSinal, setAvisoSinal] = useState<{ figuraId: string; mensagem: string } | null>(null);
  const [avisoPosicionamento, setAvisoPosicionamento] =
    useState<{ figuraId: string; mensagem: string } | null>(null);
  const [chatAberto, setChatAberto] = useState(false);
  const [explicacaoCategoriaVista, setExplicacaoCategoriaVista] = useState<string | null>(null);
  const [menuIdiomaAberto, setMenuIdiomaAberto] = useState(false);
  const [historinhaVisivel, setHistorinhaVisivel] = useState(false);
  const [atividadeIniciada, setAtividadeIniciada] = useState(false);
  // Projeção exploratória da reta: o servidor (relação estrutural do
  // domínio) calcula o dependente; o cliente só exibe. Nada é gravado.
  const [estadoExploratorio, setEstadoExploratorio] = useState<{
    papelId: string; novoValor: number; valores: Readonly<Record<string, number>>;
  } | null>(null);
  const sequenciaProjecaoEixo = useRef(0);
  const ultimaProjecaoAceita = useRef<{
    papelId: string; novoValor: number; valores: Readonly<Record<string, number>>;
  } | null>(null);
  const difusorExploratorio = useRef<EstadoSemanticoExploratorio | null>(null);
  const [snapshotPropostaRejeitada, setSnapshotPropostaRejeitada] = useState<EstadoWeb | null>(null);
  // Valor rejeitado aguardando a resposta Sim/Não: a resposta é uma
  // tentativa própria registrada pelo servidor (decisão de 2026-09-28).
  const [propostaRejeitada, setPropostaRejeitada] =
    useState<{ papelId: string; valor: number } | null>(null);
  const [sinalPendenteIncognita, setSinalPendenteIncognita] =
    useState<{ figuraId: string; papelId: string } | null>(null);

  useEffect(() => {
    api.carregar().then(receberSnapshot).catch((erro: Error) => console.error(erro));
  }, []);

  const tentativaAtualId = estado?.modelagem?.tentativa_id ?? estado?.situacao_id;
  useEffect(() => {
    setEstadoExploratorio(null);
    ultimaProjecaoAceita.current = null;
    setHistorinhaVisivel(false);
  }, [tentativaAtualId]);

  useEffect(() => {
    const relacao = estado?.cena?.relacao_exploratoria;
    const concluida = estado?.modelagem && "concluida" in estado.modelagem
      && estado.modelagem.concluida;
    if (!relacao || !concluida || !estado.cena) {
      difusorExploratorio.current = null;
      return;
    }
    const valores = Object.fromEntries(estado.cena.figuras
      .filter((figura) => figura.valor !== null)
      .map((figura) => [figura.chave_papel_semantico, figura.valor as number]));
    const difusor = new EstadoSemanticoExploratorio(relacao, valores);
    difusorExploratorio.current = difusor;
    return difusor.observarSnapshot((mudanca, snapshot) => {
      setEstadoExploratorio({ papelId: mudanca.papelId,
        novoValor: mudanca.novoValor, valores: snapshot });
    });
  }, [estado]);

  useEffect(() => {
    if (!avisoSinal) return;
    const temporizador = setTimeout(() => setAvisoSinal(null), 4000);
    return () => clearTimeout(temporizador);
  }, [avisoSinal]);

  useEffect(() => {
    if (!avisoPosicionamento) return;
    const temporizador = setTimeout(() => setAvisoPosicionamento(null), 4000);
    return () => clearTimeout(temporizador);
  }, [avisoPosicionamento]);

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
      setAtividadeIniciada(true);
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  async function restaurarDiagrama() {
    const controle = acao("REINICIAR_TENTATIVA");
    if (!controle) return;
    setOcupado(true);
    try {
      receberSnapshot(await api.reiniciar());
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  async function trocarIdioma(codigo: string) {
    setMenuIdiomaAberto(false);
    setOcupado(true);
    try {
      receberSnapshot(await api.trocarIdiomaSituacao(codigo));
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
    if (!atividadeIniciada) return;
    const controle = acoesCategoria().find((item) => item.corpo?.categoria === categoria);
    if (controle) void executarClassificacao(controle);
  }

  function confirmarCategoria(concordou: boolean) {
    const controle = estado?.acoes_disponiveis.find((item) =>
      item.id === "CONFIRMAR_CATEGORIA_DIVERGENTE" && item.corpo?.concordou === concordou);
    if (controle) void executarClassificacao(controle);
  }

  function iniciarEdicaoValor(figura: FiguraCena, interacao: InteracaoPermitidaFigura) {
    setSnapshotPropostaRejeitada(null);
    setPropostaRejeitada(null);
    setSinalPendenteIncognita(null);
    const modelagem = estado?.modelagem;
    const papeis = modelagem && "parte1" in modelagem
      ? [modelagem.parte1, modelagem.parte2, modelagem.todo]
      : modelagem && "papeis" in modelagem ? modelagem.papeis : undefined;
    const papel = papeis?.find((item) => item.id === interacao.papel_id);
    const rascunho = papel && "rascunho_material_concreto" in papel
      ? (papel as { rascunho_material_concreto?: number }).rascunho_material_concreto : undefined;
    const valorInicial = papel?.conhecido && papel.valor !== null ? String(papel.valor)
      : rascunho !== undefined && rascunho !== null ? String(rascunho) : undefined;
    enviarEventoRepresentacional({ tipo: "EDICAO_VALOR_INICIADA",
      elementoId: figura.id, actionId: interacao.acao_id, valorInicial });
  }

  async function enviarPropostaValor(valor: number) {
    if (!estado || !representacoes.elementoEmEdicao) return;
    const figura = estado.cena?.figuras.find(
      (item) => item.id === representacoes.elementoEmEdicao);
    const interacao = figura?.interacoes_permitidas.find(
      (item) => item.tipo === "EDITAR_VALOR");
    const controle = estado.acoes_disponiveis.find((item) =>
      item.id === interacao?.acao_id && item.corpo?.papel_id === interacao.papel_id);
    if (!figura || !interacao || !controle || !Number.isInteger(valor)) return;
    setOcupado(true);
    try {
      const resultado = await api.posicionar(controle, valor);
      if (resultado.aceita) {
        receberSnapshot(resultado.estado);
        setSinalPendenteIncognita(null);
        enviarEventoRepresentacional({ tipo: "CONFIRMACAO_ENVIADA" });
      } else if (resultado.limite_atingido) {
        // Limite de rejeições atingido: como no desktop
        // (processarLimiteTentativasAtingido), não há nova pergunta; o
        // snapshot do servidor traz a escalada (material concreto).
        setSinalPendenteIncognita(null);
        setHistorinhaVisivel(true);
        receberSnapshot(resultado.estado);
        enviarEventoRepresentacional({ tipo: "CONFIRMACAO_ENVIADA" });
      } else if (!resultado.chave_mensagem) {
        // Após a conclusão (azul) a modificação é exploratória: a
        // IncognitaQuantitativa não constitui tentativa e o servidor não
        // devolve pergunta. Só volta a pedir o valor, sem Sim/Não.
        setSinalPendenteIncognita(null);
      } else {
        setSinalPendenteIncognita(null);
        setSnapshotPropostaRejeitada(resultado.estado);
        setPropostaRejeitada({ papelId: interacao.papel_id, valor });
        enviarEventoRepresentacional({ tipo: "VALOR_PROPOSTO_PARA_CONFIRMACAO",
          elementoId: representacoes.elementoEmEdicao });
      }
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  function aoConfirmarDigitacao() {
    if (!estado || !representacoes.elementoEmEdicao) return;
    const figura = estado.cena?.figuras.find(
      (item) => item.id === representacoes.elementoEmEdicao);
    const texto = representacoes.valoresEmEdicao[representacoes.elementoEmEdicao] ?? "";
    const magnitude = Number(texto);
    if (!figura || !Number.isInteger(magnitude)) return;
    if (figura.requer_representacao_de_sinal) {
      setSinalPendenteIncognita({
        figuraId: figura.id,
        papelId: figura.chave_papel_semantico,
      });
      return;
    }
    void enviarPropostaValor(magnitude);
  }

  function escolherSinalIncognita(sinal: "+" | "-") {
    if (!sinalPendenteIncognita) return;
    const texto = representacoes.valoresEmEdicao[sinalPendenteIncognita.figuraId] ?? "";
    const magnitude = Math.abs(Number(texto));
    if (!Number.isInteger(magnitude)) return;
    void enviarPropostaValor(sinal === "-" ? -magnitude : magnitude);
  }

  async function responderConfirmacao(confirmou: boolean) {
    const proposta = propostaRejeitada;
    const snapshotRejeitado = snapshotPropostaRejeitada;
    setPropostaRejeitada(null);
    setSnapshotPropostaRejeitada(null);
    setSinalPendenteIncognita(null);
    let limiteAtingido = false;
    let estadoServidor: EstadoWeb | null = null;
    if (proposta) {
      setOcupado(true);
      try {
        const resultado = await api.responderConfirmacaoValor(
          proposta.papelId, confirmou, proposta.valor);
        limiteAtingido = Boolean(resultado.limite_atingido);
        if (limiteAtingido) setHistorinhaVisivel(true);
        estadoServidor = resultado.estado;
      } catch (erro) { console.error(erro); }
      finally { setOcupado(false); }
    }
    if (confirmou || limiteAtingido) {
      const snapshot = estadoServidor ?? snapshotRejeitado;
      if (snapshot) receberSnapshot(snapshot);
      enviarEventoRepresentacional({ tipo: "CONFIRMACAO_ENVIADA" });
    } else {
      enviarEventoRepresentacional({ tipo: "CONFIRMACAO_NEGADA" });
    }
  }

  function aoNegarValor() {
    void responderConfirmacao(false);
  }

  function aoConfirmarValor() {
    void responderConfirmacao(true);
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
    // A compatibilidade semântica entre o que foi arrastado (papelId) e o
    // papel da figura-alvo não é mais decidida aqui — o servidor valida
    // (AvaliadorOrigemDestinoWeb, mesma regra de
    // ScaffoldingQuestionamento.avaliarPosicionamento do desktop, que não é
    // igualdade estrita: papéis genéricos de família podem ocupar papéis
    // específicos). Isso evita que o cliente seja a única fonte de verdade
    // e que uma soltura incorreta simplesmente não chegue a lugar nenhum.

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
        }, papelId);
        receberSnapshot(resultado.estado);
        if (resultado.aceita) {
          const figuraEngatada = resultado.estado.cena?.figuras.find(
            (item) => item.id === figura.id);
          const editar = figuraEngatada?.interacoes_permitidas.find(
            (item) => item.tipo === "EDITAR_VALOR");
          if (figuraEngatada && editar) iniciarEdicaoValor(figuraEngatada, editar);
        }
        setAvisoPosicionamento(resultado.aceita ? null
          : { figuraId: figura.id, mensagem: resultado.chave_mensagem ?? "" });
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
      }, papelId);
      receberSnapshot(resultado.estado);
      setAvisoPosicionamento(resultado.aceita ? null
        : { figuraId: figura.id, mensagem: resultado.chave_mensagem ?? "" });
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  async function escolherOperacao(operacao: "SOMA" | "SUBTRACAO") {
    // A ação vive em estado.modelagem.acoes_disponiveis (a modelagem tem sua
    // própria lista, distinta da lista de classificação em estado.acoes_disponiveis)
    // — bug real encontrado por protocolo de mouse: clicar Soma/Subtração
    // nunca disparava requisição nenhuma, em nenhuma categoria (auditoria de
    // acoplamento de Main/web, 2026-09-18).
    const controle = estado?.modelagem?.acoes_disponiveis
      .find((item) => item.id === "ESCOLHER_OPERACAO_RELACAO");
    if (!controle) return;
    setOcupado(true);
    try {
      const resultado = await api.escolherOperacao(controle, operacao);
      receberSnapshot(resultado.estado);
      // Regra 1: nenhum texto inventado. Sem explicação curada vinda do
      // servidor, nada é exibido (o desktop só emite o som de erro).
      setMensagemOperacao(resultado.aceita ? null : resultado.chave_mensagem);
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

  async function projetarEixo(papelId: string, valor: number) {
    if (difusorExploratorio.current) {
      difusorExploratorio.current.alterarPapel(papelId, valor);
      return;
    }
    const sequencia = ++sequenciaProjecaoEixo.current;
    try {
      const resultado = await api.projetarEixo(papelId, valor);
      if (sequencia === sequenciaProjecaoEixo.current) {
        if (resultado.aceita === false) {
          // Domínio recusou (ex.: medida negativa): volta à última projeção aceita.
          setEstadoExploratorio(ultimaProjecaoAceita.current);
        } else {
          const aceita = { papelId, novoValor: valor, valores: resultado.valores };
          ultimaProjecaoAceita.current = aceita;
          setEstadoExploratorio(aceita);
        }
      }
    } catch (erro) { console.error(erro); }
  }

  async function alternarEixo(figura: FiguraCena) {
    const interacao = figura.interacoes_permitidas.find(
      (item) => item.tipo === (figura.lupa_habilitada ? "OCULTAR_EIXO" : "REVELAR_EIXO"));
    if (!interacao) return;
    setOcupado(true);
    try {
      const resultado = figura.lupa_habilitada
        ? await api.ocultarEixo(interacao.papel_id)
        : await api.revelarEixo(interacao.papel_id);
      receberSnapshot(resultado.estado);
    } catch (erro) { console.error(erro); }
    finally { setOcupado(false); }
  }

  // Generalizado por capacidade: as cinco categorias que possuem número
  // relativo ou transformação expõem papel_aguardando_sinal; Composição de
  // Medidas é a única das seis sem papel que necessite representação de sinal.
  const modelagemComSinal = estado && estado.modelagem
    && "papel_aguardando_sinal" in estado.modelagem
    ? estado.modelagem
    : undefined;

  const cenaExibida = (() => {
    if (!estado?.cena) return estado?.cena;
    const papelPendente = sinalPendenteIncognita?.papelId
      ?? modelagemComSinal?.papel_aguardando_sinal;
    const magnitudeDigitada = sinalPendenteIncognita
      ? Number(representacoes.valoresEmEdicao[sinalPendenteIncognita.figuraId] ?? "")
      : null;
    const magnitudePendente = Number.isInteger(magnitudeDigitada)
      ? Math.abs(magnitudeDigitada!)
      : modelagemComSinal?.magnitude_aguardando_sinal;
    const cenaComMagnitude = papelPendente && magnitudePendente !== null
        && magnitudePendente !== undefined
      ? { ...estado.cena, figuras: estado.cena.figuras.map((figura) =>
          figura.chave_papel_semantico === papelPendente
            ? { ...figura, valor: magnitudePendente, conhecido: true, engatada: false }
            : figura) }
      : estado.cena;
    if (!estadoExploratorio) return cenaComMagnitude;
    const origem = cenaComMagnitude.figuras.find(
      (figura) => figura.chave_papel_semantico === estadoExploratorio.papelId);
    if (!origem || origem.valor === null) return cenaComMagnitude;
    // Cada projeção interessada recolhe do broadcast os papéis que materializa.
    // As projeções relacionais vieram calculadas pelo domínio no snapshot;
    // nenhum componente React escolhe destinatários ou reproduz fórmulas.
    const valoresExplorados = new Map<string, number>(
      Object.entries(estadoExploratorio.valores));
    return {
      ...cenaComMagnitude,
      figuras: cenaComMagnitude.figuras.map((figura) => {
        const valor = valoresExplorados.get(figura.chave_papel_semantico);
        return valor === undefined ? figura : { ...figura, valor };
      }),
      elementos_texto: cenaComMagnitude.elementos_texto?.map((elemento) => {
        const valor = elemento.papel_id ? valoresExplorados.get(elemento.papel_id) : undefined;
        return valor === undefined ? elemento : { ...elemento, valor: String(valor) };
      }),
    };
  })();

  const figuraEmEdicao = estado && representacoes.elementoEmEdicao
    ? estado.cena?.figuras.find((item) => item.id === representacoes.elementoEmEdicao)
    : undefined;
  // Generalizado por capacidade, não por categoria: qualquer modelagem que
  // exponha um dos dois campos de escolha de operação (formatos distintos
  // hoje só em Composição de Transformações/Relações, ver
  // AcoesDisponiveisAtividadeWeb.escolhaOperacaoRelacao no servidor) acende
  // o seletor — sem comparar nome de categoria (auditoria de acoplamento de
  // Main/web, 2026-09-18).
  const modelagemEscolhaOperacao = estado && estado.modelagem
    && ("escolha_operacao" in estado.modelagem
      || "escolha_entre_transformacoes" in estado.modelagem)
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
      aoAbrirChat={() => setChatAberto(true)}
      podeSortearMedidas={Boolean(acao("SORTEAR_MEDIDAS"))}
      podeSortearRelacoes={Boolean(acao("SORTEAR_RELACOES"))}
      aoSortearMedidas={() => sortear("SORTEAR_MEDIDAS")}
      aoSortearRelacoes={() => sortear("SORTEAR_RELACOES")}
      // O snapshot inicial só habilita os sorteios (dfef18a): sem situação
      // exibida, escolher categoria classificaria um enunciado invisível.
      categoriasHabilitadas={atividadeIniciada
        ? acoesCategoria().map((item) => String(item.corpo?.categoria)) : []}
      categoriaSelecionada={atividadeIniciada && estado ? estado.categoria_selecionada : null}
      aoEscolherCategoria={escolherCategoria}
      contextoRelatoBug={atividadeIniciada && estado ? { situacaoId: estado.situacao_id,
        categoria: estado.categoria, enunciado: estado.enunciado } : null} />
    <ChatbotGerard aberto={chatAberto} aoFechar={() => setChatAberto(false)} />
    {atividadeIniciada && estado && <div className="activity-area">
      <section className="statement-panel" aria-labelledby="enunciado">
        {estado.dica_proximo_passo
          ? <div className="help-mark-wrap" onMouseEnter={() => setDicaVisivel(true)}
              onMouseLeave={() => setDicaVisivel(false)}
              onFocus={() => setDicaVisivel(true)}
              onBlur={(evento) => { if (!evento.currentTarget.contains(evento.relatedTarget as Node)) setDicaVisivel(false); }}>
              <button type="button" className="help-mark" aria-expanded={dicaVisivel}
                aria-label="Qual é o próximo passo?"><IconeAjudaContextual /></button>
              {dicaVisivel && <p className="help-tip" role="status">{estado.dica_proximo_passo}</p>}
            </div>
          : itemAjuda("TEXTO")
            ? <MenuAjudaContextual item={itemAjuda("TEXTO")} />
            : <span className="help-mark" aria-hidden="true"><IconeAjudaContextual /></span>}
        <div className="help-mark-wrap language-switch-wrap">
          <button type="button" className="help-mark" aria-expanded={menuIdiomaAberto}
            aria-label={descricaoBotaoIdiomaSituacao(estado.idiomas_situacao)}
            title={descricaoBotaoIdiomaSituacao(estado.idiomas_situacao)}
            onClick={() => setMenuIdiomaAberto((aberto) => !aberto)} disabled={ocupado}>
            <IconeIdioma />
          </button>
          {menuIdiomaAberto && <div className="help-menu language-switch-menu" role="dialog" aria-label="Idioma da situação-problema">
            {estado.idiomas_situacao.length > 1
              ? <div className="help-menu-opcoes">
                  {estado.idiomas_situacao.map((idioma) => <button key={idioma.codigo} type="button"
                    disabled={idioma.atual || ocupado} onClick={() => void trocarIdioma(idioma.codigo)}>
                    {idioma.atual ? "✓ " : ""}{idioma.nome}
                  </button>)}
                </div>
              : <p className="help-tip" role="status">
                  Esta situação-problema não possui outras versões linguísticas validadas.
                </p>}
          </div>}
        </div>
        {cenaExibida?.elementos_texto
          ? <EnunciadoInterativo key={estado.modelagem?.tentativa_id ?? estado.situacao_id}
              elementos={cenaExibida.elementos_texto}
              permiteEditarNarrativa={cenaExibida.permite_editar_narrativa === true}
              figuras={cenaExibida.figuras}
              organizadores={cenaExibida.vocabulario_texto?.candidatos_organizadores_informacao ?? []}
              modeloPalavraComum={cenaExibida.vocabulario_texto?.modelo_palavra_comum ?? null}
              aoSoltar={aoSoltarNoDiagrama} aoAtualizarAlvo={setFiguraDestacadaId} />
          : <h1 id="enunciado">{estado.enunciado}</h1>}
      </section>
      <div className="workspace workspace-awaiting-category">
        <section className={`diagram-panel${estado.modelagem?.concluida === true ? " diagram-panel-concluido" : ""}`} aria-label="Área do diagrama">
          <MenuAjudaContextual item={itemAjuda("VERGNAUD")} />
          {acao("REINICIAR_TENTATIVA") && <span className="help-mark-wrap diagram-restore-wrap">
            <button type="button" className="help-mark"
              aria-label="Limpar a área do diagrama e recomeçar a modelagem"
              title="Limpar a área do diagrama e recomeçar a modelagem"
              disabled={ocupado} onClick={() => void restaurarDiagrama()}>
              <IconeRestaurar />
            </button>
          </span>}
          {cenaExibida && <GeradorCenaGerard cena={cenaExibida}
            posicoesEmEdicao={representacoes.posicoesEmEdicao}
            aoEditarValor={iniciarEdicaoValor}
            figuraDestacadaId={figuraDestacadaId}
            aoAlternarEixo={(figura) => void alternarEixo(figura)}
            seletorSinal={sinalPendenteIncognita || modelagemComSinal?.papel_aguardando_sinal ? {
              papelId: sinalPendenteIncognita?.papelId
                ?? modelagemComSinal!.papel_aguardando_sinal!,
              mensagemDivergente: avisoSinal?.mensagem ?? null,
              aoEscolher: (papelId, figuraId, sinal) => sinalPendenteIncognita
                ? escolherSinalIncognita(sinal)
                : void escolherSinal(papelId, figuraId, sinal),
            } : undefined}
            seletorOperacao={modelagemEscolhaOperacao ? { modelagem: modelagemEscolhaOperacao,
              mensagemErro: mensagemOperacao, ocupado, aoEscolher: escolherOperacao } : undefined} />}
          {figuraEmEdicao && !sinalPendenteIncognita && <EdicaoValorFigura figuraId={figuraEmEdicao.id}
            papelNome={figuraEmEdicao.rotulo} pergunta={estado.confirmacao_valor_papel}
            modo={representacoes.confirmando ? "confirmando" : "digitando"}
            requerSinal={figuraEmEdicao.requer_representacao_de_sinal}
            valor={representacoes.valoresEmEdicao[figuraEmEdicao.id] ?? ""} ocupado={ocupado}
            aoAlterarValor={(valor) => enviarEventoRepresentacional({
              tipo: "VALOR_EM_EDICAO_ALTERADO", elementoId: figuraEmEdicao.id, valor })}
            aoConfirmarDigitacao={() => void aoConfirmarDigitacao()}
            aoConfirmarValor={aoConfirmarValor}
            aoNegarValor={aoNegarValor} />}
          {avisoPosicionamento && <AvisoPosicionamentoFigura
            figuraId={avisoPosicionamento.figuraId} mensagem={avisoPosicionamento.mensagem} />}
          {estado.cena?.figuras.filter((figura) => figura.lupa_habilitada && figura.eixo)
            .map((figura) => {
              const valorExibido = cenaExibida?.figuras
                .find((figuraExibida) => figuraExibida.id === figura.id)?.valor
                ?? figura.eixo!.valor;
              return <EixoNumericoFigura key={figura.id} figuraId={figura.id}
                papelNome={figura.rotulo} eixo={{ ...figura.eixo!, valor: valorExibido }}
                ocupado={ocupado} editavel={figura.eixo!.valor !== null}
                aoPrevisualizarValor={(valor) =>
                  projetarEixo(figura.chave_papel_semantico, valor)}
                aoFechar={() => void alternarEixo(figura)} />;
            })}
          {estado.modelagem?.concluida === true
            && <SeloConclusao key={estado.modelagem?.tentativa_id ?? estado.situacao_id} />}
        </section>
        <aside className="response-panel" aria-label="Área complementar">
          <MenuAjudaContextual item={itemAjuda("COMPLEMENTAR")} />
          {historinhaVisivel && modelagemComSinal &&
            <div className="historinha-passiva" aria-label="Historinha">
              <img src="/ajuda/composicao_transformacoes/01_joao_bilas_historinha.gif"
                alt="" aria-hidden="true" />
            </div>}
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
          <h2 id="pergunta-categoria">{estado.questionamento_titulo}</h2><p>{estado.questionamento}</p>
          <div className="dialog-actions"><button type="button" onClick={() => confirmarCategoria(true)} disabled={ocupado}>{estado.questionamento_sim}</button>
            <button type="button" onClick={() => confirmarCategoria(false)} disabled={ocupado}>{estado.questionamento_nao}</button></div>
        </section></div>}
      {estado.modo === "REEXPLICACAO_CATEGORIA" && explicacaoCategoriaVista !== estado.situacao_id &&
        <div className="modal-backdrop" role="presentation"><section className="confirmation-dialog" role="dialog" aria-modal="true" aria-labelledby="titulo-reexplicacao-categoria">
          <h2 id="titulo-reexplicacao-categoria">{estado.explicacao_categoria_titulo}</h2>
          <p>{estado.explicacao_categoria_intro}</p>
          <p><strong>{estado.explicacao_categoria_rotulo}</strong> — {estado.explicacao_categoria_definicao}</p>
          <div className="dialog-actions">
            <button type="button" onClick={() => setExplicacaoCategoriaVista(estado.situacao_id)}>
              {estado.explicacao_categoria_fechar}
            </button>
          </div>
        </section></div>}
    </div>}
  </main>;
}
