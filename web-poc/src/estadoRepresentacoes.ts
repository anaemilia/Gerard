import type { EstadoWeb } from "./contratos";

export type PosicaoVisual = Readonly<{ x: number; y: number }>;

/**
 * Rascunho efêmero das representações. Não contém regras matemáticas e nunca
 * é persistido: um novo snapshot do servidor sempre volta a ser a autoridade.
 * O "?" engatado na caixa (protocolo mouse-texto) vem do servidor
 * (figura.engatada, via engatarIncognita/projetarCena) — não é mais estado
 * deste rascunho, exatamente para não existir uma segunda fonte de verdade
 * fora do gerador de cena.
 *
 * confirmando: elemento no passo "tem certeza que esse é o valor de X?"
 * (depois de digitar, antes de decidir Sim/Não) — confirmarValorIncognitaAceito
 * do desktop.
 */
export type EstadoRepresentacoes = Readonly<{
  snapshotServidor: EstadoWeb | null;
  posicoesEmEdicao: Readonly<Record<string, PosicaoVisual>>;
  valoresEmEdicao: Readonly<Record<string, string>>;
  elementoEmEdicao: string | null;
  acaoPendente: string | null;
  confirmando: string | null;
}>;

export type EventoRepresentacional =
  | Readonly<{ tipo: "SNAPSHOT_SERVIDOR_RECEBIDO"; snapshot: EstadoWeb }>
  | Readonly<{ tipo: "POSICAO_VISUAL_ALTERADA"; elementoId: string; posicao: PosicaoVisual }>
  | Readonly<{ tipo: "EDICAO_VALOR_INICIADA"; elementoId: string; actionId: string; valorInicial?: string }>
  | Readonly<{ tipo: "VALOR_EM_EDICAO_ALTERADO"; elementoId: string; valor: string }>
  | Readonly<{ tipo: "VALOR_PROPOSTO_PARA_CONFIRMACAO"; elementoId: string }>
  | Readonly<{ tipo: "CONFIRMACAO_NEGADA" }>
  | Readonly<{ tipo: "CONFIRMACAO_ENVIADA" }>
  | Readonly<{ tipo: "RASCUNHO_DESCARTADO" }>;

export const estadoRepresentacoesInicial: EstadoRepresentacoes = {
  snapshotServidor: null,
  posicoesEmEdicao: {},
  valoresEmEdicao: {},
  elementoEmEdicao: null,
  acaoPendente: null,
  confirmando: null
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
        acaoPendente: null,
        confirmando: null
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
        acaoPendente: evento.actionId, confirmando: null,
        valoresEmEdicao: evento.valorInicial === undefined ? estado.valoresEmEdicao
          : { ...estado.valoresEmEdicao, [evento.elementoId]: evento.valorInicial } };
    case "VALOR_EM_EDICAO_ALTERADO":
      return { ...estado, valoresEmEdicao: { ...estado.valoresEmEdicao,
        [evento.elementoId]: evento.valor } };
    case "VALOR_PROPOSTO_PARA_CONFIRMACAO":
      return { ...estado, confirmando: evento.elementoId };
    case "CONFIRMACAO_NEGADA":
      // Volta a pedir o valor (confirmarValorIncognitaAceito, Main.java) —
      // a caixa permanece engatada no servidor, não descarta o rascunho.
      return { ...estado, confirmando: null };
    case "CONFIRMACAO_ENVIADA":
      return { ...estado, elementoEmEdicao: null, acaoPendente: null, confirmando: null };
    case "RASCUNHO_DESCARTADO":
      return { ...estado, posicoesEmEdicao: {}, valoresEmEdicao: {},
        elementoEmEdicao: null, acaoPendente: null, confirmando: null };
  }
}
