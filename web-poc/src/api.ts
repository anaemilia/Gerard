import type { AcaoDisponivel, EstadoWeb, ResultadoAcao, ResultadoClassificacao, ResultadoPosicionarConhecido, ResultadoQuadradinho } from "./contratos";

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
  posicionarConhecido: (acao: AcaoDisponivel) =>
    requisitar<ResultadoPosicionarConhecido>(acao.href, {
      method: acao.metodo, headers: { "Content-Type": "application/json" },
      body: JSON.stringify(acao.corpo ?? {})
    })
};
