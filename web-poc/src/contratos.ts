export const SCHEMA_ESTADO = "gerard.atividade-web.estado.v1" as const;
export const SCHEMA_RESULTADO = "gerard.atividade-web.resultado-acao.v1" as const;

export type AcaoDisponivel = Readonly<{
  id: "SORTEAR_MEDIDAS" | "SORTEAR_RELACOES" | "REINICIAR_TENTATIVA" | "PROPOR_VALOR_TODO" |
    "ESCOLHER_CATEGORIA" | "CONFIRMAR_CATEGORIA_DIVERGENTE";
  metodo: "POST"; href: string; corpo?: Readonly<Record<string, unknown>>;
}>;

export type PapelProjetado = Readonly<{ id: string; nome: string; conhecido: boolean; valor: number | null }>;
export type EstadoAtividade = Readonly<{
  schema: typeof SCHEMA_ESTADO; situacao_id: string; tentativa_id: string;
  categoria: "COMPOSICAO_MEDIDAS"; enunciado: string; relacao: string;
  papel_desconhecido_original: string; parte1: PapelProjetado;
  parte2: PapelProjetado; todo: PapelProjetado; concluida: boolean;
  acoes_disponiveis: readonly AcaoDisponivel[];
}>;
export type EstadoClassificacao = Readonly<{
  schema: typeof SCHEMA_ESTADO; modo: "CLASSIFICACAO_CATEGORIA" |
    "AGUARDANDO_CONFIRMACAO_CATEGORIA" | "CATEGORIA_CLASSIFICADA" | "REEXPLICACAO_CATEGORIA";
  grupo_sorteio: "MEDIDAS" | "RELACOES"; situacao_id: string;
  situacao_grupo_id: string; categoria: string; categoria_selecionada: string | null;
  enunciado: string; concluida: false; acoes_disponiveis: readonly AcaoDisponivel[];
  questionamento?: string; categoria_revelada?: string;
}>;
export type EstadoWeb = EstadoAtividade | EstadoClassificacao;
export type ResultadoClassificacao = Readonly<{
  schema: "gerard.atividade-web.resultado-classificacao.v1"; action_id: string;
  correta: boolean; diagnostico: string | null; desfecho: string;
  rejeicoes_consecutivas: number; estado: EstadoClassificacao;
}>;
export type ResultadoAcao = Readonly<{
  schema: typeof SCHEMA_RESULTADO; action_id: string; aceita: boolean;
  diagnostico: string | null; chave_mensagem: string | null;
  rejeicoes_consecutivas: number; estado: EstadoAtividade;
}>;
export type ComandoPosicionar = Readonly<{ papel_id: string; valor: number }>;
