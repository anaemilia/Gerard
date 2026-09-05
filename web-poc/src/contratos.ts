export const SCHEMA_ESTADO = "gerard.atividade-web.estado.v1" as const;
export const SCHEMA_RESULTADO = "gerard.atividade-web.resultado-acao.v1" as const;

export type AcaoDisponivel = Readonly<{
  id: "SORTEAR_MEDIDAS" | "SORTEAR_RELACOES" | "REINICIAR_TENTATIVA" | "PROPOR_VALOR_PAPEL" |
    "ESCOLHER_CATEGORIA" | "CONFIRMAR_CATEGORIA_DIVERGENTE" | "ESCOLHER_OPERACAO_RELACAO" |
    "AJUSTAR_QUADRADINHO" | "POSICIONAR_CONHECIDO" | "ENGATAR_INCOGNITA";
  metodo: "POST"; href: string; corpo?: Readonly<Record<string, unknown>>;
}>;

export type PapelProjetado = Readonly<{ id: string; nome: string; conhecido: boolean; valor: number | null; engatada: boolean }>;
export type ElementoTexto = Readonly<{ valor: string; papel_id: string | null; incognita: boolean }>;
export type AreaAjudaContextual = "TEXTO" | "VERGNAUD" | "COMPLEMENTAR";
export type IntencaoAjuda = "DUVIDA" | "CONTINUAR" | "PROXIMO_PASSO";
export type OpcaoAjudaContextual = Readonly<{ intencao: IntencaoAjuda; rotulo: string }>;
export type ItemAjudaContextual = Readonly<{
  area: AreaAjudaContextual; cabecalho: string; opcoes: readonly OpcaoAjudaContextual[];
}>;
export type EstadoAtividade = Readonly<{
  schema: typeof SCHEMA_ESTADO; situacao_id: string; tentativa_id: string;
  categoria: "COMPOSICAO_MEDIDAS"; enunciado: string; relacao: string;
  papel_desconhecido_original: string; rotulo_papel_desconhecido: string; parte1: PapelProjetado;
  parte2: PapelProjetado; todo: PapelProjetado; concluida: boolean;
  material_concreto_disponivel: boolean;
  material_concreto_texto?: string;
  material_concreto_texto_adicionar?: string;
  material_concreto_texto_remover?: string;
  acoes_disponiveis: readonly AcaoDisponivel[];
}>;
export type EstadoClassificacao = Readonly<{
  schema: typeof SCHEMA_ESTADO; modo: "CLASSIFICACAO_CATEGORIA" |
    "AGUARDANDO_CONFIRMACAO_CATEGORIA" | "CATEGORIA_CLASSIFICADA" | "REEXPLICACAO_CATEGORIA";
  grupo_sorteio: "MEDIDAS" | "RELACOES"; situacao_id: string;
  situacao_grupo_id: string; categoria: string; categoria_selecionada: string | null;
  enunciado: string; concluida: boolean; acoes_disponiveis: readonly AcaoDisponivel[];
  questionamento?: string; categoria_revelada?: string;
  dica_proximo_passo?: string | null;
  ajuda_contextual?: readonly ItemAjudaContextual[];
  confirmacao_valor_papel?: string | null;
  cena?: CenaDiagrama;
  elementos_texto?: readonly ElementoTexto[];
  modelagem?: EstadoAtividade | EstadoModelagemTernaria
    | EstadoEscolhaOperacaoTransformacoes | EstadoEscolhaOperacaoRelacoes;
}>;
export type EstadoModelagemTernaria = Readonly<{
  schema: typeof SCHEMA_ESTADO; situacao_id: string; tentativa_id: string;
  categoria: "TRANSFORMACAO_MEDIDAS" | "COMPARACAO_MEDIDAS" |
    "TRANSFORMACAO_RELACAO"; relacao: string;
  papel_desconhecido_original: string; papeis: readonly PapelProjetado[];
  concluida: boolean; acoes_disponiveis: readonly AcaoDisponivel[];
}>;
export type EscolhaOperacao = "SOMA" | "SUBTRACAO" | null;
export type EstadoEscolhaOperacaoTransformacoes = Readonly<{
  schema: typeof SCHEMA_ESTADO; situacao_id: string; tentativa_id: string;
  categoria: "COMPOSICAO_TRANSFORMACOES"; enunciado: string;
  estado_inicial: PapelProjetado; transformacao_1: PapelProjetado;
  estado_intermediario: PapelProjetado; transformacao_2: PapelProjetado;
  transformacao_final: PapelProjetado; estado_final: PapelProjetado;
  escolha_entre_transformacoes: EscolhaOperacao;
  escolha_entre_estado_transformacao: EscolhaOperacao;
  correta_entre_transformacoes: boolean | null;
  correta_entre_estado_transformacao: boolean | null;
  segunda_etapa_habilitada: boolean; concluida: boolean;
  acoes_disponiveis: readonly AcaoDisponivel[];
}>;
export type EstadoEscolhaOperacaoRelacoes = Readonly<{
  schema: typeof SCHEMA_ESTADO; situacao_id: string; tentativa_id: string;
  categoria: "COMPOSICAO_RELACOES"; enunciado: string;
  relacao_1: PapelProjetado; relacao_2: PapelProjetado; relacao_final: PapelProjetado;
  escolha_operacao: EscolhaOperacao; correta: boolean | null;
  concluida: boolean; acoes_disponiveis: readonly AcaoDisponivel[];
}>;
export type InteracaoPermitidaFigura = Readonly<{
  tipo: "EDITAR_VALOR" | "POSICIONAR_CONHECIDO" | "ENGATAR_INCOGNITA";
  acao_id: "PROPOR_VALOR_PAPEL" | "POSICIONAR_CONHECIDO" | "ENGATAR_INCOGNITA";
  fase_envio: "CONFIRMACAO" | "IMEDIATA";
  papel_id: string;
}>;
export type ResultadoPosicionarConhecido = Readonly<{
  schema: typeof SCHEMA_RESULTADO; aceita: boolean; estado: EstadoWeb;
}>;
export type FiguraCena = Readonly<{ id: string; tipo: "RETANGULO" | "ELIPSE" | "RETANGULO_ARREDONDADO"; x: number; y: number; largura: number; altura: number; rotulo: string;
  posicao_rotulo: "CENTRO" | "ACIMA" | "ABAIXO"; exibir_lupa: boolean; lupa_habilitada: boolean; chave_papel_semantico: string; subtitulo: string;
  valor: number | null; conhecido: boolean; engatada: boolean;
  interacoes_permitidas: readonly InteracaoPermitidaFigura[] }>;
export type ConectorCena = Readonly<{ tipo: "SETA" | "SETA_CURVA" | "LINHA" | "CHAVE_VERTICAL" | "CHAVE_HORIZONTAL"; x1: number; y1: number; x2: number; y2: number; legenda: string; x_alvo?: number; y_alvo?: number }>;
export type CentroSeletorOperacao = Readonly<{ cx: number; cy: number }>;
export type CenaDiagrama = Readonly<{ titulo: string; descricao: string; figuras: readonly FiguraCena[]; conectores: readonly ConectorCena[];
  viewport: Readonly<{ x: number; y: number; largura: number; altura: number }>;
  seletor_operacao?: Readonly<{ entre_transformacoes?: CentroSeletorOperacao;
    entre_estado_transformacao?: CentroSeletorOperacao; relacao?: CentroSeletorOperacao }> }>;
// O topo da resposta nunca é EstadoAtividade "puro" — só aparece aninhado em
// EstadoClassificacao.modelagem (categoria COMPOSICAO_MEDIDAS). Todo estado
// de topo passa pela classificação; ver ServicoSorteioAtividadeWeb.estadoInicial.
export type EstadoWeb = EstadoClassificacao;
export type ResultadoClassificacao = Readonly<{
  schema: "gerard.atividade-web.resultado-classificacao.v1"; action_id: string;
  correta: boolean; diagnostico: string | null; desfecho: string;
  rejeicoes_consecutivas: number; estado: EstadoClassificacao;
}>;
export type ResultadoAjudaContextual = Readonly<{
  schema: "gerard.atividade-web.resultado-ajuda-contextual.v1"; mensagem: string;
}>;
export type ResultadoQuadradinho = Readonly<{
  schema: typeof SCHEMA_RESULTADO; aceita: boolean; limite_atingido: boolean;
  chave_mensagem: string | null; estado: EstadoWeb;
}>;
export type ResultadoAcao = Readonly<{
  schema: typeof SCHEMA_RESULTADO; action_id: string; aceita: boolean;
  diagnostico: string | null; chave_mensagem: string | null;
  limite_atingido?: boolean;
  rejeicoes_consecutivas: number; estado: EstadoWeb;
}>;
