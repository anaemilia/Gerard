import type { EstadoWeb } from "./contratos";

export type PosicaoVisual = Readonly<{ x: number; y: number }>;

/**
 * Rascunho efêmero das representações. Não contém regras matemáticas e nunca
 * é persistido: um novo snapshot do servidor sempre volta a ser a autoridade.
 */
export type EstadoRepresentacoes = Readonly<{
  snapshotServidor: EstadoWeb | null;
  posicoesEmEdicao: Readonly<Record<string, PosicaoVisual>>;
  valoresEmEdicao: Readonly<Record<string, string>>;
  elementoEmEdicao: string | null;
  acaoPendente: string | null;
}>;

export type EventoRepresentacional =
  | Readonly<{ tipo: "SNAPSHOT_SERVIDOR_RECEBIDO"; snapshot: EstadoWeb }>
  | Readonly<{ tipo: "POSICAO_VISUAL_ALTERADA"; elementoId: string; posicao: PosicaoVisual }>
  | Readonly<{ tipo: "EDICAO_VALOR_INICIADA"; elementoId: string; actionId: string }>
  | Readonly<{ tipo: "VALOR_EM_EDICAO_ALTERADO"; elementoId: string; valor: string }>
  | Readonly<{ tipo: "RASCUNHO_DESCARTADO" }>;

export const estadoRepresentacoesInicial: EstadoRepresentacoes = {
  snapshotServidor: null,
  posicoesEmEdicao: {},
  valoresEmEdicao: {},
  elementoEmEdicao: null,
  acaoPendente: null
};

export function reduzirEstadoRepresentacoes(
  estado: EstadoRepresentacoes,
  evento: EventoRepresentacional
): EstadoRepresentacoes {
  switch (evento.tipo) {
    case "SNAPSHOT_SERVIDOR_RECEBIDO":
      return {
        snapshotServidor: evento.snapshot,
        posicoesEmEdicao: {},
        valoresEmEdicao: {},
        elementoEmEdicao: null,
        acaoPendente: null
      };
    case "POSICAO_VISUAL_ALTERADA":
      return {
        ...estado,
        posicoesEmEdicao: {
          ...estado.posicoesEmEdicao,
          [evento.elementoId]: evento.posicao
        }
      };
    case "EDICAO_VALOR_INICIADA":
      return { ...estado, elementoEmEdicao: evento.elementoId,
        acaoPendente: evento.actionId };
    case "VALOR_EM_EDICAO_ALTERADO":
      return { ...estado, valoresEmEdicao: { ...estado.valoresEmEdicao,
        [evento.elementoId]: evento.valor } };
    case "RASCUNHO_DESCARTADO":
      return { ...estado, posicoesEmEdicao: {}, valoresEmEdicao: {},
        elementoEmEdicao: null, acaoPendente: null };
  }
}
