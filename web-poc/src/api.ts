import type { AcaoDisponivel, AreaAjudaContextual, EstadoWeb, IntencaoAjuda, ResultadoAcao,
  ResultadoAjudaContextual, ResultadoClassificacao, ResultadoEscolherSinal,
  ResultadoPosicionarConhecido, ResultadoQuadradinho, CadastroUsuarioWeb, EdicaoUsuarioWeb,
  ListaUsuariosWeb, PerfilUsuarioWeb, SessaoUsuarioWeb, SessaoAtualUsuarioWeb,
  ResultadoRelatoBug } from "./contratos";

/** Um erro da API: o {@code tipo} (type do problem+json) é o que o código decide; a mensagem é só para mostrar. */
export class ErroApi extends Error {
  constructor(readonly status: number, readonly tipo: string, mensagem: string) {
    super(mensagem);
  }
}

/** O ETag da última leitura da tentativa: alterar a tentativa exige devolvê-lo em If-Match. */
let etagDaTentativa: string | null = null;
const RECURSO_DA_TENTATIVA = /^\/api\/v1\/tentativas\/([^/]+)(\/.*)?$/;

function alteraATentativa(url: string, metodo: string): boolean {
  const m = RECURSO_DA_TENTATIVA.exec(url);
  // A projeção no eixo é um cálculo: não grava na tentativa e não exige nem avança a versão.
  return metodo !== "GET" && !!m && !!m[2] && !m[2].endsWith("/eixo/projecao");
}

async function requisitar<T>(url: string, init?: RequestInit): Promise<T> {
  const metodo = init?.method ?? "GET";
  const cabecalhos = new Headers(init?.headers);
  if (alteraATentativa(url, metodo) && etagDaTentativa) cabecalhos.set("If-Match", etagDaTentativa);
  const resposta = await fetch(url, { ...init, headers: cabecalhos });
  const etag = resposta.headers.get("ETag");
  if (etag && url.startsWith("/api/v1/tentativas")) etagDaTentativa = etag;
  const corpo = (await resposta.json()) as T & { type?: string; detail?: string; erro?: string };
  if (!resposta.ok) {
    if (resposta.status === 412) await ressincronizar(url);
    throw new ErroApi(resposta.status, corpo.type ?? "about:blank",
      corpo.detail ?? corpo.erro ?? `Falha HTTP ${resposta.status}`);
  }
  return corpo;
}

/** Outra aba alterou a tentativa: lê o estado de novo e avisa a tela, que passa a mostrar o que é verdade. */
async function ressincronizar(url: string): Promise<void> {
  const m = RECURSO_DA_TENTATIVA.exec(url);
  if (!m) return;
  const resposta = await fetch(`/api/v1/tentativas/${m[1]}`);
  const etag = resposta.headers.get("ETag");
  if (etag) etagDaTentativa = etag;
  if (resposta.ok) {
    window.dispatchEvent(new CustomEvent("gerard:estado-desatualizado", { detail: await resposta.json() }));
  }
}

const JSON_HEADERS = { "Content-Type": "application/json" };
const papel = (id: string) => encodeURIComponent(id);
/** A tentativa como recurso (ver RotasTentativa no servidor): tudo o que se faz nela mora sob esta URI. */
const tentativa = (id: string) => `/api/v1/tentativas/${encodeURIComponent(id)}`;

export const api = {
  listarUsuarios: () => requisitar<ListaUsuariosWeb>("/api/v1/usuarios"),
  cadastrarUsuario: (cadastro: CadastroUsuarioWeb) => requisitar<PerfilUsuarioWeb>("/api/v1/usuarios", {
    method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(cadastro)
  }),
  entrarUsuario: (usuarioId: string) => requisitar<SessaoUsuarioWeb>("/api/v1/sessao/usuario", {
    method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ usuario_id: usuarioId })
  }),
  atualizarUsuario: (edicao: EdicaoUsuarioWeb) => requisitar<PerfilUsuarioWeb>(`/api/v1/usuarios/${encodeURIComponent(edicao.usuario_id)}`, {
    method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(edicao)
  }),
  consultarSessaoUsuario: () => requisitar<SessaoAtualUsuarioWeb>("/api/v1/sessao/usuario"),
  carregar: () => requisitar<EstadoWeb>("/api/v1/tentativas", {
    method: "POST", headers: JSON_HEADERS, body: "{}"
  }),
  executar: (acao: Pick<AcaoDisponivel, "metodo" | "href" | "corpo">) => requisitar<EstadoWeb>(acao.href, {
    method: acao.metodo,
    headers: acao.corpo ? { "Content-Type": "application/json" } : undefined,
    body: acao.corpo ? JSON.stringify(acao.corpo) : undefined
  }),
  classificar: (acao: AcaoDisponivel) => requisitar<ResultadoClassificacao>(acao.href, {
    method: acao.metodo, headers: { "Content-Type": "application/json" },
    body: JSON.stringify(acao.corpo ?? {})
  }),
  trocarIdiomaSituacao: (tentativaId: string, codigo: string) => requisitar<EstadoWeb>(`${tentativa(tentativaId)}/idioma`, {
    method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ codigo })
  }),
  posicionar: (acao: AcaoDisponivel, valor: number) => requisitar<ResultadoAcao>(acao.href, {
    method: acao.metodo, headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ...(acao.corpo ?? {}), valor })
  }),
  responderConfirmacaoValor: (tentativaId: string, papelId: string, confirmou: boolean, valor: number) =>
    requisitar<ResultadoAcao>(`${tentativa(tentativaId)}/papeis/${papel(papelId)}/valor/confirmacao`, {
      method: "POST", headers: JSON_HEADERS, body: JSON.stringify({ confirmou, valor })
    }),
  escolherOperacao: (acao: AcaoDisponivel, operacao: "SOMA" | "SUBTRACAO") =>
    requisitar<ResultadoAcao>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...(acao.corpo ?? {}), operacao })
    }),
  ajustarQuadradinho: (acao: AcaoDisponivel, delta: 1 | -1) =>
    requisitar<ResultadoQuadradinho>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...(acao.corpo ?? {}), delta })
    }),
  soltarNumero: (tentativaId: string, papelAlvoId: string, origemPapelId: string | null) =>
    requisitar<ResultadoPosicionarConhecido>(`${tentativa(tentativaId)}/papeis/${papel(papelAlvoId)}/posicionamento`, {
      method: "POST", headers: JSON_HEADERS, body: JSON.stringify({ origem_papel_id: origemPapelId })
    }),
  escolherSinal: (acao: AcaoDisponivel) =>
    requisitar<ResultadoEscolherSinal>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify(acao.corpo ?? {})
    }),
  alterarVisibilidadeEixo: (tentativaId: string, papelId: string, visivel: boolean) =>
    requisitar<ResultadoPosicionarConhecido>(`${tentativa(tentativaId)}/papeis/${papel(papelId)}/eixo/visibilidade`, {
      method: "PUT", headers: JSON_HEADERS, body: JSON.stringify({ visivel })
    }),
  projetarEixo: (tentativaId: string, papelId: string, valor: number) =>
    requisitar<{ valores: Record<string, number>; aceita?: boolean }>(
      `${tentativa(tentativaId)}/papeis/${papel(papelId)}/eixo/projecao`, {
        method: "POST", headers: JSON_HEADERS, body: JSON.stringify({ valor })
      }),
  ajudaContextual: (tentativaId: string, area: AreaAjudaContextual, intencao: IntencaoAjuda) =>
    requisitar<ResultadoAjudaContextual>(`${tentativa(tentativaId)}/ajudas`, {
      method: "POST", headers: JSON_HEADERS, body: JSON.stringify({ area, intencao })
    }),
  relatarBug: (dados: { descricao: string; situacao_id: string; categoria: string;
      representacoes: string; idioma_interface: string; idioma_situacao: string; enunciado: string }) =>
    requisitar<ResultadoRelatoBug>("/api/v1/relatos", {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify(dados)
    }),
  enviarCuradoria: (token: string, conteudoTsv: string) =>
    requisitar<{ aceito: boolean; total_situacoes: number; total_validadas: number }>(
      "/api/curadoria/situacoes", {
        method: "POST",
        headers: { "Content-Type": "text/plain; charset=utf-8", "X-Curadoria-Token": token },
        body: conteudoTsv
      })
};
