import type { AcaoDisponivel, AreaAjudaContextual, EstadoWeb, IntencaoAjuda, ResultadoAcao,
  ResultadoAjudaContextual, ResultadoClassificacao, ResultadoEscolherSinal,
  ResultadoPosicionarConhecido, ResultadoQuadradinho } from "./contratos";

async function requisitar<T>(url: string, init?: RequestInit): Promise<T> {
  const resposta = await fetch(url, init);
  const corpo = (await resposta.json()) as T & { erro?: string };
  if (!resposta.ok) throw new Error(corpo.erro ?? `Falha HTTP ${resposta.status}`);
  return corpo;
}

export const api = {
  carregar: () => requisitar<EstadoWeb>("/api/situacao"),
  executar: (acao: AcaoDisponivel) => requisitar<EstadoWeb>(acao.href, {
    method: acao.metodo,
    headers: acao.corpo ? { "Content-Type": "application/json" } : undefined,
    body: acao.corpo ? JSON.stringify(acao.corpo) : undefined
  }),
  classificar: (acao: AcaoDisponivel) => requisitar<ResultadoClassificacao>(acao.href, {
    method: acao.metodo, headers: { "Content-Type": "application/json" },
    body: JSON.stringify(acao.corpo ?? {})
  }),
  reiniciar: () => requisitar<EstadoWeb>("/api/reiniciar", { method: "POST" }),
  posicionar: (acao: AcaoDisponivel, valor: number) => requisitar<ResultadoAcao>(acao.href, {
    method: acao.metodo, headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ...(acao.corpo ?? {}), valor })
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
  posicionarConhecido: (acao: AcaoDisponivel, origemPapelId: string | null) =>
    requisitar<ResultadoPosicionarConhecido>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...(acao.corpo ?? {}), origem_papel_id: origemPapelId })
    }),
  engatarIncognita: (acao: AcaoDisponivel, origemPapelId: string | null) =>
    requisitar<ResultadoPosicionarConhecido>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...(acao.corpo ?? {}), origem_papel_id: origemPapelId })
    }),
  escolherSinal: (acao: AcaoDisponivel) =>
    requisitar<ResultadoEscolherSinal>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify(acao.corpo ?? {})
    }),
  revelarEixo: (papelId: string) =>
    requisitar<ResultadoPosicionarConhecido>("/api/acoes/revelar-eixo", {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ papel_id: papelId })
    }),
  ocultarEixo: (papelId: string) =>
    requisitar<ResultadoPosicionarConhecido>("/api/acoes/ocultar-eixo", {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ papel_id: papelId })
    }),
  ajudaContextual: (area: AreaAjudaContextual, intencao: IntencaoAjuda) =>
    requisitar<ResultadoAjudaContextual>("/api/acoes/ajuda-contextual", {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ area, intencao })
    })
};
