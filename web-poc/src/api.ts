import type { AcaoDisponivel, ComandoPosicionar, EstadoAtividade, EstadoWeb, ResultadoAcao, ResultadoClassificacao } from "./contratos";

async function requisitar<T>(url: string, init?: RequestInit): Promise<T> {
  const resposta = await fetch(url, init);
  const corpo = (await resposta.json()) as T & { erro?: string };
  if (!resposta.ok) throw new Error(corpo.erro ?? `Falha HTTP ${resposta.status}`);
  return corpo;
}

export const api = {
  carregar: () => requisitar<EstadoAtividade>("/api/situacao"),
  executar: (acao: AcaoDisponivel) => requisitar<EstadoWeb>(acao.href, {
    method: acao.metodo,
    headers: acao.corpo ? { "Content-Type": "application/json" } : undefined,
    body: acao.corpo ? JSON.stringify(acao.corpo) : undefined
  }),
  classificar: (acao: AcaoDisponivel) => requisitar<ResultadoClassificacao>(acao.href, {
    method: acao.metodo, headers: { "Content-Type": "application/json" },
    body: JSON.stringify(acao.corpo ?? {})
  }),
  reiniciar: () => requisitar<EstadoAtividade>("/api/reiniciar", { method: "POST" }),
  posicionar: (comando: ComandoPosicionar) => requisitar<ResultadoAcao>("/api/acoes/posicionar", {
    method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(comando)
  })
};
