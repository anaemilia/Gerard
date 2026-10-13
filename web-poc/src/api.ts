import type { AcaoDisponivel, AreaAjudaContextual, EstadoWeb, IntencaoAjuda, ResultadoAcao,
  ResultadoAjudaContextual, ResultadoClassificacao, ResultadoEscolherSinal,
  ResultadoPosicionarConhecido, ResultadoQuadradinho, CadastroUsuarioWeb, EdicaoUsuarioWeb,
  ListaUsuariosWeb, PerfilUsuarioWeb, SessaoUsuarioWeb, SessaoAtualUsuarioWeb,
  ResultadoRelatoBug, LinkWeb, RaizApiWeb } from "./contratos";

/**
 * O cliente da API conhece UM endereço: a entrada. Tudo o mais — onde criar a tentativa, onde ficam o idioma, a ajuda
 * e os recursos de cada figura, como editar uma pessoa — vem em `_links` das respostas e é seguido daqui. Se o servidor
 * mudar o caminho de um recurso, nada neste arquivo muda.
 */
const ENTRADA = "/api/v1";

export class ErroApi extends Error {
  constructor(readonly status: number, readonly tipo: string, mensagem: string) {
    super(mensagem);
  }
}

/** O ETag da última leitura da tentativa: alterar a tentativa exige devolvê-lo em If-Match. */
let etagDaTentativa: string | null = null;
/** Onde reler a tentativa em curso (o link "self" do último estado): usado para ressincronizar no 412. */
let selfDaTentativa: LinkWeb | null = null;
let raiz: Promise<RaizApiWeb> | null = null;
let linksDaRaiz: RaizApiWeb["_links"] | null = null;

type Opcoes = { corpo?: unknown; cabecalhos?: Record<string, string>; corpoBruto?: string; condicional?: boolean };

function entrada(): Promise<RaizApiWeb> {
  raiz ??= fetch(ENTRADA).then(async (resposta) => {
    const doc = (await resposta.json()) as RaizApiWeb;
    linksDaRaiz = doc._links;
    return doc;
  });
  return raiz;
}

async function daRaiz(rel: keyof RaizApiWeb["_links"]): Promise<LinkWeb> {
  return (await entrada())._links[rel];
}

/** Segue um link: o método vem do próprio link; `condicional` envia o If-Match da tentativa. */
async function seguir<T>(link: LinkWeb, opcoes: Opcoes = {}): Promise<T> {
  const metodo = link.metodo ?? "GET";
  const cabecalhos = new Headers(opcoes.cabecalhos);
  let corpo: string | undefined = opcoes.corpoBruto;
  if (opcoes.corpo !== undefined) {
    cabecalhos.set("Content-Type", "application/json");
    corpo = JSON.stringify(opcoes.corpo);
  }
  if (opcoes.condicional && etagDaTentativa) cabecalhos.set("If-Match", etagDaTentativa);
  const resposta = await fetch(link.href, { method: metodo, headers: cabecalhos, body: corpo });
  const etag = resposta.headers.get("ETag");
  if (etag) etagDaTentativa = etag;
  const doc = (await resposta.json()) as T & { type?: string; detail?: string; erro?: string };
  if (!resposta.ok) {
    if (resposta.status === 412) await ressincronizar();
    throw new ErroApi(resposta.status, doc.type ?? "about:blank",
      doc.detail ?? doc.erro ?? `Falha HTTP ${resposta.status}`);
  }
  lembrarTentativa(doc);
  return doc;
}

/** Guarda o link "self" do estado que acabou de chegar (o estado vem na resposta ou aninhado em `estado`). */
function lembrarTentativa(doc: unknown): void {
  const candidato = doc as { tentativa_id?: string; _links?: Record<string, LinkWeb>; estado?: unknown };
  if (candidato.tentativa_id && candidato._links?.self) {
    selfDaTentativa = candidato._links.self;
  } else if (candidato.estado) {
    lembrarTentativa(candidato.estado);
  }
}

/** Outra aba alterou a tentativa: lê o estado de novo e avisa a tela, que passa a mostrar o que é verdade. */
async function ressincronizar(): Promise<void> {
  if (!selfDaTentativa) return;
  const resposta = await fetch(selfDaTentativa.href);
  const etag = resposta.headers.get("ETag");
  if (etag) etagDaTentativa = etag;
  if (resposta.ok) {
    window.dispatchEvent(new CustomEvent("gerard:estado-desatualizado", { detail: await resposta.json() }));
  }
}

/** Uma ação publicada em `acoes_disponiveis`: o método e o href vêm dela; o corpo é o que ela sugere mais o que a tela soma. */
const executarAcao = <T>(acao: Pick<AcaoDisponivel, "metodo" | "href" | "corpo">, extra: object = {}) =>
  seguir<T>({ href: acao.href, metodo: acao.metodo }, { corpo: { ...(acao.corpo ?? {}), ...extra }, condicional: true });

export const api = {
  listarUsuarios: async () => seguir<ListaUsuariosWeb>(await daRaiz("usuarios")),
  cadastrarUsuario: async (cadastro: CadastroUsuarioWeb) =>
    seguir<PerfilUsuarioWeb>(await daRaiz("cadastrar_usuario"), { corpo: cadastro }),
  entrarUsuario: async (usuarioId: string) =>
    seguir<SessaoUsuarioWeb>(await daRaiz("entrar"), { corpo: { usuario_id: usuarioId } }),
  /** A pessoa traz o link de edição; a edição não carrega o id (ele está na URI). */
  atualizarUsuario: (perfil: PerfilUsuarioWeb, edicao: Omit<EdicaoUsuarioWeb, "usuario_id">) =>
    seguir<PerfilUsuarioWeb>(perfil._links.editar, { corpo: edicao }),
  consultarSessaoUsuario: async () => seguir<SessaoAtualUsuarioWeb>(await daRaiz("sessao_usuario")),
  carregar: async () => seguir<EstadoWeb>(await daRaiz("tentativas"), { corpo: {} }),
  executar: (acao: Pick<AcaoDisponivel, "metodo" | "href" | "corpo">) => {
    // Sorteios e escolhas: o corpo é o que a ação publicou (pode não haver).
    return seguir<EstadoWeb>({ href: acao.href, metodo: acao.metodo },
      { corpo: acao.corpo, condicional: true });
  },
  classificar: (acao: AcaoDisponivel) => executarAcao<ResultadoClassificacao>(acao),
  trocarIdiomaSituacao: (estado: EstadoWeb, codigo: string) =>
    seguir<EstadoWeb>(estado._links.idioma, { corpo: { codigo }, condicional: true }),
  posicionar: (acao: AcaoDisponivel, valor: number) => executarAcao<ResultadoAcao>(acao, { valor }),
  responderConfirmacaoValor: (link: LinkWeb, confirmou: boolean, valor: number) =>
    seguir<ResultadoAcao>(link, { corpo: { confirmou, valor }, condicional: true }),
  escolherOperacao: (acao: AcaoDisponivel, operacao: "SOMA" | "SUBTRACAO") =>
    executarAcao<ResultadoAcao>(acao, { operacao }),
  ajustarQuadradinho: (acao: AcaoDisponivel, delta: 1 | -1) => executarAcao<ResultadoQuadradinho>(acao, { delta }),
  soltarNumero: (link: LinkWeb, origemPapelId: string | null) =>
    seguir<ResultadoPosicionarConhecido>(link, { corpo: { origem_papel_id: origemPapelId }, condicional: true }),
  escolherSinal: (acao: AcaoDisponivel) => executarAcao<ResultadoEscolherSinal>(acao),
  alterarVisibilidadeEixo: (link: LinkWeb, visivel: boolean) =>
    seguir<ResultadoPosicionarConhecido>(link, { corpo: { visivel }, condicional: true }),
  projetarEixo: (link: LinkWeb, valor: number) =>
    seguir<{ valores: Record<string, number>; aceita?: boolean }>(link, { corpo: { valor }, condicional: true }),
  ajudaContextual: (link: LinkWeb, area: AreaAjudaContextual, intencao: IntencaoAjuda) =>
    seguir<ResultadoAjudaContextual>(link, { corpo: { area, intencao }, condicional: true }),
  relatarBug: async (dados: { descricao: string; situacao_id: string; categoria: string;
      representacoes: string; idioma_interface: string; idioma_situacao: string; enunciado: string }) =>
    seguir<ResultadoRelatoBug>(await daRaiz("relatos"), { corpo: dados }),
  enviarCuradoria: async (token: string, conteudoTsv: string) =>
    seguir<{ aceito: boolean; total_situacoes: number; total_validadas: number }>(
      await daRaiz("curadoria_situacoes"), {
        cabecalhos: { "Content-Type": "text/plain; charset=utf-8", Authorization: `Bearer ${token}` },
        corpoBruto: conteudoTsv
      }),
  /** Fechar a aba: avisa o servidor, que antecipa o envio do log. Só dá para usar se a entrada já foi lida. */
  sincronizarLog: () => {
    if (linksDaRaiz) navigator.sendBeacon(linksDaRaiz.sincronizacoes.href);
  }
};
