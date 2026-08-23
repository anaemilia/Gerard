# Changelog — GERARD Semantic Model and Skills v2

## 2.8 — 2026-08-13

### Dois níveis temporais + fundamento rastreável da decisão adaptativa

- Distinguido o Modelo do Usuário histórico/intersessões, congelado no login,
  do Modelo da Situação/Solução contextual/intrasseção, produzido pelas ações
  do participante sobre elementos da interface.
- Preservada a localidade: o contexto corrente não é um mapa ou decisor
  global; chega como fatos tipados ao proprietário semântico pertinente.
- Registrada a fundamentação em Akhras e Self (2000) para o curso e o contexto
  das situações interativas e a observação empírica da usuária sobre ausência
  de progressão de dificuldade nas sessões reais, sem automatizar diagnóstico
  de tédio ou inventar limiares.
- `DecisaoAjuda` passou a congelar algoritmo e proveniência dos casos da regra
  aplicada para que o evento continue reconstruível após futuras publicações.

## 2.7 — 2026-08-07

### Evento FEEDBACK_EXIBIDO + arquitetura envelope/payload — implementados

- Item 6 do levantamento de pendências (`LEVANTAMENTO_PENDENCIAS_2026-08-07.md`).
  Escopo confirmado pela usuária: "tudo de uma vez, incluindo produção" —
  tanto a reestruturação do piloto quanto a ligação ao log real.
- `EventoEnvelope` (novo, `gerard.dominio.campoaditivo.evento`) — núcleo
  fixo (event_id, action_id, tipo versionado, origem da ação, timestamp)
  descrito em REFERENCE.md §4.8 (Seção 19.1 da Revisão 5).
- `TipoEventoPapel` ganhou `FEEDBACK_EXIBIDO` e `chaveVersionada()`
  (sufixo `.v1`, "type-based versioning" do CloudEvents).
- `ModalidadeEntregaScaffolding` (novo enum) — VISUAL, SONORA, HAPTICA,
  MANIPULATIVA, GUIADA_POR_MOVIMENTO; `ehPassiva()`/`ehInterativa()`
  determinam o critério de confirmação do evento ("renderizado" vs.
  "affordance ativada").
- `EventoPapelQuantitativo` recomposto para conter um `EventoEnvelope` +
  campos de payload — todos os getters públicos originais preservados,
  `paraMapa()` só ganhou chaves novas (nenhuma removida/renomeada).
  Novo factory `feedbackExibido(...)`.
- Produção: `Main.registrarFeedbackExibido(estiloScaffolding, modalidade,
  detalhesExtra)` traduz o mesmo vocabulário para uma linha real no log
  (`registrarLogComputador`), sem instanciar a classe do piloto em
  `Main.java` — "o domínio não grava logs diretamente"
  (gerard-semantic-event-logging). Ligado a 5 pontos reais: AG_EMLQ
  (`confirmarValorIncognitaAceito`), AG_EME
  (`mostrarDicaOperacaoIncognita`), AG_EMCME mensagem
  (`mostrarAvisoLimiteTentativasAtingido`), AG_EMCME material concreto
  (`registrarTentativaIncognita`, no bloqueio por limite) e AG_EMS
  (`configurarFeedbackConclusaoModelagem`).
- Verificado: compilação completa (436 arquivos, 0 erros), os 3 harnesses
  do piloto/comparativo (todos passando), e uma execução real sob Xvfb
  que exercitou os 5 pontos de disparo (4 deles atrás de `JOptionPane`
  modais, dispensados via Robot) e confirmou as 5 linhas
  `FEEDBACK_EXIBIDO` no TSV real de produção, cada uma exatamente uma
  vez, com `estilo`/`modalidade`/`criterio` corretos — ver
  `RELATORIO_FEEDBACK_EXIBIDO_2026-08-07.md`.
- REFERENCE.md §4.8 atualizado: as três afirmações "decisão-alvo, não
  implementação" (arquitetura de evento, versionamento de esquema,
  evento FEEDBACK_EXIBIDO) substituídas por notas de implementação
  datadas de 2026-08-07.

## 2.6 — 2026-08-07

### gerard-consistencia-estado: regra 5 (eixo dos inteiros) confirmada

- Investigação só leitura, item 2 do levantamento de pendências
  (`LEVANTAMENTO_PENDENCIAS_2026-08-07.md`) — pedida explicitamente pela
  usuária.
- O gate que impede a incógnita de se resolver visualmente antes do
  protocolo normal foi localizado em `Main.java:11612-11674`
  (`sincronizarNumeroRelativoComGraficoSeNecessario`), não em
  `ScaffoldingGraficoInteiros`/`LayoutPainelEixoInteiros`/
  `PoliticaPreenchimentoIncognita` como a formulação original sugeria.
- Escopo mais preciso: o gate só se aplica quando o item movido pelo
  eixo é a própria incógnita ainda pendente de confirmação — não é um
  bloqueio geral do eixo. O mesmo mecanismo (`incognitaAguardandoConfirmacaoDeValor`/
  `confirmarValorIncognitaAceito`) já protege `EIXO_VERTICAL` e
  `ARRASTE` da mesma forma.
- Nenhum código de produção muda por este registro — só documentação.

## 2.5 — 2026-08-07

### Nova skill: gerard-handlers-de-interacao (direção, não implementada)

- Registrada a pedido da usuária, a partir de uma discussão externa sobre
  a concentração de código de mouse em `TelaGerard` (`mousePressed` com
  ~366 linhas).
- Propõe um padrão concreto (handler de interação por tipo de elemento,
  `TelaGerard` só roteando) para a camada "Interação" que
  `gerard-domain-model-first` já definia conceitualmente, mas sem
  prescrever estrutura de código.
- `gerard-domain-model-first` ganhou uma única linha de referência
  cruzada na seção "Interação", sem alterar nenhuma regra existente.
- Nenhum código de produção muda por este registro — status explícito de
  "proposta, não implementada", com roteiro incremental que exige
  confirmação explícita antes de cada etapa.

## 2.4 — 2026-08-04

### Correção do registro 2.3: Comparação já tem lógica coberta, fora do pacote piloto

- A nota de status de `RelacaoEstruturalComparacao` (2.3) dizia apenas
  "lacuna de cobertura conhecida" — investigação de acompanhamento achou
  que a responsabilidade de resolver a terceira quantidade a partir de
  duas conhecidas, para as três categorias (incluindo Comparação), já é
  coberta por `EstadoSemanticoCompartilhado`, fora do pacote piloto.
- `REFERENCE.md §4.3` atualizado para refletir isso — não é mais lida
  como "nenhuma lógica existe".
- `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md` ganhou seção de contexto
  arquitetural com esse achado.

## 2.3 — 2026-08-04

### Registro da lacuna de cobertura: Comparação de Medidas

- Confirmado, por investigação em código (git grep, listagem de
  `gerard/dominio/campoaditivo/`), que `RelacaoEstruturalComparacao`
  nunca foi implementada, apesar de prevista em `REFERENCE.md`.
- Nenhum commit ou documento anterior registrava isso como decisão
  deliberada de escopo — passa a ser registrado agora.
- A partir desta entrada, toda verificação de antes/depois desta sessão
  deve declarar explicitamente que cobre só Composição e Transformação.

## 2.2 — 2026-08-04

### Mobilização do invariante operatório: sugestão do sistema vs. atribuição do pesquisador

- Esclarecido que `SugestorInvarianteOperatorio.sugerirCodigo` produz uma
  sugestão auxiliar, sem origem definida até ser adotada.
- Definido que o evento que registra o invariante operatório mobilizado é
  sempre o do pesquisador (origem `PESQUISADOR`), via seleção ou criação no
  combobox correspondente.

## 2.1 — 2026-08-04

### Resolução de ambiguidades normativas (Revisão 5 da auditoria arquitetural)

- Definido o critério de desempate entre `SISTEMA` e `INFERENCIA_COMPUTACIONAL`:
  regra explícita e fixa versus juízo derivado de evidências.
- Adicionadas as condições do valor de um papel semântico (dado, desconhecido,
  proposto, aceito, rejeitado, calculado, revisado) em `REFERENCE.md §4.2.1`.

## 2.0 — 2026-08-01

### Documento normativo criado

- Adicionado `gerard-semantic-model/REFERENCE.md`.
- Definidos esquema, teorema-em-ação, conceito-em-ação e invariantes operatórios com referência a Vergnaud (1998).
- Definido `C = (S, I, R)` como triplo dinâmico e relacional.
- Separados papéis semânticos, relações estruturais, eventos e hipóteses analíticas.

### Domain Model First

- “Todo elemento do diagrama” foi substituído por “todo elemento semanticamente significativo”.
- Retirados posição de tela, dimensões concretas e aparência da responsabilidade do domínio.
- Adicionada distinção entre descritor abstrato e renderização.
- Proibido nomear relação formal como invariante operatório.

### Knowledge-Oriented Domain Objects

- Retirada a afirmação de que cada objeto representa um conceito completo.
- Adicionadas fronteiras entre objeto local, relação estrutural, política pedagógica e hipótese analítica.
- Diagnóstico factual separado de feedback pedagógico.

### Knowledge Locality Principle

- Localidade dividida em objeto, relação, pedagogia, infraestrutura e análise epistemológica.
- Reconhecidos coordenadores de escopo fechado.

### Semantic Event Logging

- Origem da ação tornada obrigatória.
- Adicionado contexto de sessão, tentativa, situação e representação.
- Eventos separados de hipóteses sobre esquemas e invariantes operatórios.
- Cálculos automáticos não podem ser registrados como ações do usuário.
