# Historinhas por categoria com números relativos

Autorização: nesta conversa, a usuária confirmou historinhas da própria
categoria após três rejeições consecutivas, generalizou para categorias com
números relativos e autorizou a implementação em 2026-10-02.

## Responsabilidades antes da implementação

| Conhecimento/efeito | Proprietário | Entrada | Saída | Porta/consumidor | Tecnologia |
|---|---|---|---|---|---|
| Repertório e seleção de historinhas | TipoSituacaoAditiva | ocorrência de papel relativo e limite de rejeições | repertório próprio ou vazio | Main / ProjetorAjudaVisualWeb | domínio, sem UI |
| Ocorrência de papel relativo | SemanticaCuradaSituacao / catálogo de papéis | situação curada | fato estrutural | projetor web / desktop | sem UI |
| Limite da incógnita | PapelQuantitativo | tentativas já avaliadas | bloqueio existente | TentativaModelagemAditiva | sem UI/I/O |
| Limite na escolha de operação | TentativaModelagemAditiva | resultados factuais constituídos do mesmo seletor | três rejeições consecutivas | Main / EscopoTentativaWeb | sem UI/I/O |
| Apresentação e limpeza | PainelAjudaNarrativaVisualCategoria / Main | repertório decidido | painel passivo ou oculto | Swing | Swing apenas no adaptador |
| Transporte | ProjetorAjudaVisualWeb | decisão e referências existentes | ajuda_visual | HTTP/React | sem decisão no cliente |

O acompanhamento de operações não altera sua avaliação nem bloqueia o seletor.
Não cria ações ou registros adicionais: acompanha os resultados existentes,
deduplicados por action_id. A restauração encerra esse acompanhamento; a
conclusão conserva o apoio já liberado e não conta exploração como rejeição.

## Investigação

- Main fixava COMPOSICAO_TRANSFORMACOES ao criar qualquer painel de historinha.
- O seletor também atribuía historinhas a TRANSFORMACAO_MEDIDAS, que não tem
  repertório próprio.
- O guarda externo do desenho impedia executar a limpeza quando o gatilho
  deixava de existir. O cache também não distinguia repertórios.
- O web já selecionava o repertório próprio, mas Composição de Relações e
  Composição de Transformações não publicavam escalada_no_limite.
- Não foi identificado caminho de exibição dos quadradinhos condicionado
  apenas à conclusão. A investigação posterior da rodada limpa está em
  `RELATORIO_COMPOSICAO_MATERIAL_CONCRETO_2026-10-02.md`.

## Validação

- Linha base antes: 149 testes aprovados; depois: 150 aprovados, zero reprovados.
  Cinco testes gráficos da linha base foram apenas compilados.
- Testes específicos de limite/deduplicação/restauração e projeção por categoria:
  aprovados. Grafo de skills e verificador de localidade aprovados; diff sem
  erros de whitespace.
- Web, mouse/teclado reais em localhost: Transformação de Relação, Composição
  de Relações e Composição de Transformações passaram. Em cada categoria,
  ausência antes do limite e nas rejeições 1/2; repertório próprio na rejeição 3;
  conclusão real preserva a ajuda; restauração a oculta.
  Evidências: `evidencias/historinhas_categorias_relativas_20261002/web/`,
  incluindo screenshots e `sequencia-http.json` com as respostas observadas.
- Desktop: execução Robot em perfil temporário, zero falhas. Confirmados
  limite/repertório e conclusão real nas três categorias, permanência da ajuda
  após concluir e limpeza ao trocar para Transformação de Medidas, sem
  repertório próprio. Exemplares: João/bilas, Julia/bonecas e Maria/idades.
  Evidências em `evidencias/historinhas_categorias_relativas_20261002/desktop/`.
  O roteiro não cobre a conclusão dos exemplares de composição em dois passos
  com estado intermediário.

A validação cobre os exemplares executados, não todas as narrativas curadas.
Não houve commit, push, alteração no Render ou em C:\\gd.
