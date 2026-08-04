# Log estruturado de auditoria dos agentes MONITOR/ZDP/MODELADOR

## Rodada 5 — unidade de análise A-B-C-D com explicações opcionais (2026-07-31)

Pacote de referência: `dados/pacote_unidade_analise_abcd_gerard_v2/PROMPT_CLAUDE_UNIDADE_ANALISE_ABCD_EXPLICACOES_OPCIONAIS.md`.
Diferente das rodadas 1-4 (correção de bugs na auditoria já existente), esta
rodada é uma **feature nova**: formaliza a unidade de análise do Gérard como
A (ação do computador, obrigatória) + B (ação do usuário, obrigatória,
classificada num dos seis protocolos) + C (perguntas explicativas, opcional)
+ D (respostas do usuário, opcional), com C/D dependendo do acionamento,
pelo próprio usuário, de um botão já existente ao lado do texto da
situação-problema.

### Decisão de arquitetura: reaproveitar, não recriar

O pedido pedia explicitamente para "verificar a tela existente na visão do
pesquisador e o botão ao lado do texto" antes de construir algo novo. Essa
tela já existe: `TelaArtefatoExplicativo` (`gerard.pesquisador.tentativa`),
aberta pelo botão "A" (`criarBotaoArtefatoExplicativo`/`abrirArtefatoExplicativo`
em `Main.java`), habilitado só quando há ao menos um posicionamento no
diagrama, nunca obrigatório, com um formulário por elemento semântico
(dificuldade + motivo) mais uma explicação geral — isto **já é** C/D na
prática, só não tinha rastreamento de estado (`not_opened`/
`opened_not_answered`/`partially_answered`/`answered`) nem saída
estruturada. Em vez de outra tela, a rodada 5 instrumenta o ciclo de vida
desta (abrir/cancelar/salvar/falhar) e constrói uma camada de auditoria
NOVA (`gerard.pesquisador.analiseunidade`) que **observa** o que
`AgentAuditService` já decide (A+B, reaproveitando gesture_id/canonical/
idempotency_key das rodadas 3/4 sem duplicar nenhuma lógica de decisão) e o
que a tela relata (C/D), sem nunca chamar MONITOR/ZDP/MODELADOR de novo.

### Componente A (ação do computador)

Um `A_computer_action` por unidade — hoje é o enunciado da situação-problema
vigente no momento do gesto canônico (reaproveita
`identificacao.situacao_problema`, já existente desde a rodada 2), mais a
lista de `technical_events` (eventos técnicos, ver abaixo) ocorridos
enquanto a unidade estava aberta. `feedback_presented`/`message_template_id`
ficam `false`/`null` — o Gérard não guarda hoje um registro estruturado
separado de "mensagem apresentada" por gesto; marcado honestamente como
indisponível, não inventado.

### Componente B (ação do usuário) — matriz dos seis protocolos

Cada `B_user_action` vem de **exatamente um** `AgentAuditEvent` canônico
(mesma garantia de unicidade das rodadas 3/4: debounce +
`gesture_id` único), com `protocol_type` derivado de `OrigemAvaliacao` —
mapeamento **verificado no código real** (não suposto), usando a MESMA
string que `Main.java` já passa para
`ConectorVereditoModelador.registrarVeredito` em cada ponto de chamada:

| `OrigemAvaliacao` (canônica) | `protocol_type` | Evidência no código |
|---|---|---|
| `SOLTURA_USUARIO` | `POSICIONAR` | `mouseReleased` chama `registrarVeredito(..., "POSICIONAR", ...)` |
| `SELECAO_CATEGORIA` | `SELECIONAR` | `clicarAtalhoCategoria` chama com `"SELECIONAR"` |
| `SELECAO_SINAL` | `SELECIONAR` | escolha de sinal do número relativo chama com `"SELECIONAR"` |
| `QUANTIFICACAO` | `TEXTO` | `confirmarValorIncognitaAceito` chama com `"TEXTO"`, não `"QUANTIFICAR"` |
| `SOLICITACAO_AJUDA` | *(sem mapeamento)* | enum existe desde a rodada 3, mas **nunca é instanciado** em nenhum ponto de `Main.java` (confirmado por busca) |

**Achado honesto, não escondido**: dos seis nomes de protocolo do pacote
(SELECIONAR/POSICIONAR/ORIENTAR/QUANTIFICAR/CAMINHO/TEXTO), só **três**
(SELECIONAR/POSICIONAR/TEXTO) chegam a ser `protocol_type` de uma
`B_user_action` avaliada por agente hoje. Os outros três já existem no
Gérard, mas em outra camada, puramente de telemetria de interação
(`registrarAcaoGranular`, em `Main.java`, linhas ~10709-10768): ORIENTAR
aparece como `"ORIENTACAO"` (mudança de direção durante um arraste) e
CAMINHO como `"CAMINHO"` (trajetória completa do arraste) — ambos
disparados a cada evento de mouse, **nunca ligados a uma avaliação real do
MONITOR/ZDP/MODELADOR**. QUANTIFICAR aparece nessa MESMA camada granular
(`registrarAcaoGranular("QUANTIFICAR", ...)`, quando o texto editado é
numérico), mas a ação equivalente, quando chega ao MONITOR/ZDP/MODELADOR
(origem `QUANTIFICACAO`), é rotulada `"TEXTO"` pelo próprio
`Main.java`/`ConectorVereditoModelador`, não `"QUANTIFICAR"` — uma
sobreposição pré-existente no vocabulário do Gérard (duas camadas
paralelas: uma orientada a agente com 5 origens canônicas, outra orientada
a telemetria com os 6 nomes do pacote), não algo introduzido nesta rodada.
Não foi "consertado" forçando uma reclassificação, porque isso exigiria
inventar um comportamento agente-avaliado para ORIENTAR/CAMINHO que o
Gérard não tem — reportado aqui como limitação real, ver seção própria.

Cada `B_user_action` carrega `agent_effects` (no máximo 1 avaliação do
MONITOR, 1 decisão do ZDP, 1 atualização do Modelador, 1 caso inserido —
todos vêm DIRETO do `AgentAuditEvent` já observado, nunca recalculados) e
`idempotency_key` (reaproveita `session_id|episode_id|gesture_id` da rodada
3, sem formato novo). Ações canônicas cujo Monitor não produziu avaliação
C/E aplicável (ex.: `ResultadoQuestionamento.naoAplicavel()`) **não geram**
`B_user_action` nem unidade — a definição da rodada 5 exige que toda
instância de protocolo receba exatamente uma avaliação C/E; sem isso não é
uma instância de protocolo nesta definição.

### Componentes C/D e os quatro estados

`AnalysisUnitAuditService` (novo) mantém, por tarefa (usuário+categoria+
papel-alvo — mesma chave de `AgenteZDP`/`AgentAuditService`), a unidade
mais recente ainda "aberta" (ainda não gravada), até: (a) uma nova ação
canônica da MESMA tarefa chegar, (b) a tela de explicações fechar (salva ou
cancelada) para essa tarefa, ou (c) o episódio terminar. Isso permite que
C/D — que só existem depois de a unidade já estar criada — sejam anexados à
MESMA linha de `unidades_analise.jsonl`, em vez de forçar duas linhas por
unidade.

`TelaArtefatoExplicativo` foi instrumentada (sem mudar sua lógica de
salvamento real) em quatro pontos: construção (botão acionado + tela
aberta → `registrarTelaAberta`), botão Cancelar E fechamento pela janela
(`WindowListener.windowClosing`) → `registrarTelaFechada(salvou=false)`,
salvamento bem-sucedido → `registrarTelaFechada(salvou=true, ...)` com o
conteúdo REAL digitado/selecionado (não reconstruído a partir do log), e
falha ao salvar (exceção real capturada no mesmo `try/catch` que já existia)
→ `registrarFalhaTecnica` (tela permanece aberta, usuário pode tentar de
novo). O status final de C/D é sempre um dos quatro valores do pacote:

| Situação real | `status` (C e D) | `analysis_unit_completeness` |
|---|---|---|
| botão nunca acionado | `not_opened` | `A_B` |
| tela aberta, cancelada sem preencher nada | `opened_not_answered` | `A_B_C` |
| tela aberta, só parte dos campos preenchidos | `partially_answered` | `A_B_C_D` |
| tela aberta, elemento + explicação geral preenchidos | `answered` | `A_B_C_D` |

`research_use.missing_explanation_reason` nunca usa palavras como
"erro"/"recusa"/"incapacidade" — os únicos valores usados são os do pacote
(`user_did_not_open_explanation_screen`, `user_opened_but_did_not_answer`,
`user_answered_partially`, `technical_failure`, `not_applicable`),
confirmado pelo teste 7.

### Limitação honesta: reabrir a tela sem uma nova ação B no meio

Como a unidade fecha (é gravada) assim que uma nova ação canônica chega
para a mesma tarefa, reabrir `TelaArtefatoExplicativo` para EDITAR uma
explicação já salva, **sem** que uma nova ação de posicionamento/seleção
tenha ocorrido para aquela tarefa entre uma abertura e outra, não encontra
mais nenhuma unidade aberta — `registrarTelaAberta` simplesmente não anexa
nada (função definida, silenciosa, documentada em código). O lado
pedagógico real (`AgenteModelador.registrarExplicacaoNoUltimoDiagnostico`)
continua funcionando normalmente nesse caso (sempre atualiza o diagnóstico
mais recente daquela tarefa, é isso que o teste 12 confirma), mas o
`edited_later`/a atualização de C/D em `unidades_analise.jsonl` só é
capturada quando a reabertura acontece para uma unidade AINDA aberta (ex.:
duas ações de posicionamento seguidas para o mesmo papel, cada uma seguida
de uma explicação — cenário coberto pelo teste 12 do lado do Modelador, mas
não pelo lado da unidade de análise). Registrado aqui em vez de fingido
resolvido.

### Arquivos criados

- `src/gerard/pesquisador/analiseunidade/{TipoProtocolo,StatusExplicacao,PerguntaExplicativa,RespostaExplicativa,ComponenteC,ComponenteD,AcaoComputador,AcaoUsuarioProtocolo,EventoTecnico,UnidadeAnalise}.java` — modelo de dados A/B/C/D.
- `src/gerard/pesquisador/analiseunidade/AnalysisUnitAuditService.java` — orquestrador (observa `AgentAuditService` + `TelaArtefatoExplicativo`, escreve os 4 JSONL).
- `src/gerard/pesquisador/analiseunidade/AnalysisUnitCardinalityReport.java` — escritor de `cardinalidade_unidades_analise.tsv`.
- `src/gerard/pesquisador/analiseunidade/TesteUnidadeAnaliseABCD.java` — os 17 testes permanentes.
- `src/gerard/pesquisador/auditoria/OuvinteUnidadeAnalise.java` — interface de observação nova em `AgentAuditService`.
- `dados/schema_unidade_analise_abcd_gerard.json` — schema (1.0.0) dos 4 arquivos JSONL, validável por `SchemaValidator` (rodada 4, reaproveitado sem mudança).

### Arquivos modificados

- `src/gerard/pesquisador/auditoria/AgentAuditService.java` — lista de `OuvinteUnidadeAnalise` + notificação em `finalizarAcao()` (aditivo, não muda nenhum comportamento existente).
- `src/gerard/pesquisador/tentativa/TelaArtefatoExplicativo.java` — instrumentação do ciclo de vida (abrir/cancelar/salvar/falhar), sem alterar `salvarNoLog`/o formato do log qualitativo existente.
- `src/Main.java` — novo campo `analysisUnitAuditService` (mesmo padrão `null`-por-padrão de `agentAuditService`, só existe quando o harness de teste injeta), wiring em `abrirArtefatoExplicativo`/`atualizarDisponibilidadeArtefatoExplicativo`.
- `src/TesteMonkeyGuiadoPorCasosReais.java` — constrói/injeta/fecha o novo serviço, escreve `cardinalidade_unidades_analise_*.tsv`.

### Compilação

`javac` completo do projeto (398 arquivos `.java`, incluindo os 6 novos)
sem erros — só os 2 avisos de depreciação pré-existentes (não
relacionados a esta rodada).

### Testes: os 17 pontos pedidos

`TesteUnidadeAnaliseABCD` — dirige agentes REAIS (`AgenteMonitor`,
`AgenteZDP`, `AgenteModelador` sobre `RepositorioModeloUsuario` isolado em
diretório temporário, nunca `~/Gerard` real) através da MESMA sequência que
`Main.java` usa (`iniciarAcao`→avaliar→`decidirEstrategia`→
`registrarVeredito`→`finalizarAcao`), porque o harness Robot
(`TesteMonkeyGuiadoPorCasosReais`) não aciona a tela de explicações hoje —
os testes de C/D chamam os mesmos métodos que `TelaArtefatoExplicativo`
chamaria, não simulam JSON à mão.

```
=== Resumo rodada 5: 17 passou, 0 falhou (de 17) ===
```

Resumo do episódio de teste (6 unidades, cobrindo os 4 estados + falha
técnica + debounce): `{analysis_units_total=6, units_A_B=2, units_A_B_C=2,
units_A_B_C_D=2, protocol_instances_total=6, correct_actions=1,
error_actions=5, explanation_screens_opened=4, opened_not_answered=2,
partially_answered=1, answered=1, technical_events=1, divergences=0}` — as
duas igualdades obrigatórias batem: `6 = 2+2+2` e `6 = 1+5`.

### Regressão completa (14 episódios reais, Robot)

`TesteMonkeyGuiadoPorCasosReais` rodado do zero após a instrumentação:
**14 episódios, 45 passos ok, 0 divergências** (idêntico às rodadas 3/4 —
nenhuma regressão funcional). As permanentes das rodadas anteriores contra
o JSONL fresco desta execução:

- `TesteCardinalidadeAuditoria` (rodada 3): **18/18 passou**.
- `TesteRodada4DespachoUnico` (rodada 4): **17/17 passou**.

Saída nova da rodada 5, real, gerada por esta mesma execução:

| Arquivo | Linhas | Validação de schema |
|---|---|---|
| `unidades_analise_*.jsonl` | 58 | 58/58 aceitas por `schema_unidade_analise_abcd_gerard.json` |
| `acoes_usuario_protocolos_*.jsonl` | 58 | 58/58 aceitas |
| `eventos_tecnicos_*.jsonl` | 352 | 352/352 aceitas |
| `explicacoes_usuario_*.jsonl` | 0 | — (arquivo existe, vazio: o harness Robot nunca abre a tela de explicações, comportamento esperado, não bug) |
| `cardinalidade_unidades_analise_*.tsv` | 14 linhas de episódio | `equalities_hold=true` nas 14 |

Distribuição real de `protocol_type` nas 58 instâncias: `POSICIONAR=44,
TEXTO=14` (SELECIONAR não ocorre nestes 14 episódios porque nenhum deles
passa pelo fluxo de adivinhação de categoria — comportamento do dataset
real, não do código; o teste unitário 9 confirma SELECIONAR separadamente).
Resultado: `C=56, E=2` (bate com os erros conhecidos de Jamile S9 — ver
exemplo abaixo).

### Exemplo real de unidade A_B (sem explicação — Jamile S9, primeiro erro conhecido)

Extraído de `unidades_analise_20260731_234320.jsonl`, gerado pela execução
Robot acima (não editado à mão):

```json
{
  "analysis_unit_id": "UA-EP-HIST_DOUTORADO_JAMILE_S9_090610-2-0001",
  "episode_id": "EP-HIST_DOUTORADO_JAMILE_S9_090610-2",
  "user_id": "hist_doutorado_jamile_s9_090610",
  "B_user_action": {
    "protocol_instance_id": "PI-GESTO-0005",
    "protocol_type": "POSICIONAR",
    "semantic_context": { "category": "TRANSFORMACAO_MEDIDAS", "source_role": "papel.estadoInicial", "target_role": "papel.transformacao" },
    "evaluation": { "result": "E", "error_type": "posicionamento_semantico" },
    "agent_effects": { "monitor_final_evaluations": 1, "zdp_effective_decisions": 1, "modeler_effective_updates": 1, "cases_inserted": 1 }
  },
  "C_explanation_questions": { "button_available": false, "status": "not_opened" },
  "D_user_explanations": { "status": "not_opened", "responses": [] },
  "analysis_unit_completeness": "A_B",
  "research_use": { "eligible_for_behavioral_analysis": true, "eligible_for_explanatory_analysis": false, "missing_explanation_reason": "user_did_not_open_explanation_screen" }
}
```

`button_available=false` aqui é real, não um bug: é o PRIMEIRO gesto de
Jamile S9 no episódio, ainda não havia nenhum posicionamento no diagrama
quando essa unidade foi criada (o botão só liga depois do primeiro
posicionamento, mesma regra de `existeAoMenosUmPosicionamentoNoDiagramaVergnaud`
de antes desta rodada).

### Exemplo real de unidade A_B_C_D (com explicação — gerado pelo mesmo `AnalysisUnitAuditService`, cenário de demonstração já que o Robot não aciona a tela)

```json
{
  "analysis_unit_id": "UA-EP-EXEMPLO-0001-0001",
  "B_user_action": {
    "protocol_type": "SELECIONAR",
    "evaluation": { "result": "C" },
    "agent_effects": { "monitor_final_evaluations": 1, "zdp_effective_decisions": 1, "modeler_effective_updates": 1, "cases_inserted": 1 }
  },
  "C_explanation_questions": {
    "button_available": true, "button_activated": true, "screen_opened": true, "status": "answered",
    "questions": [
      { "question_id": "...-01", "question_type": "dificuldade_e_motivo_do_elemento", "presented_to_user": true },
      { "question_id": "...-02", "question_type": "explicacao_geral_da_modelagem", "presented_to_user": true }
    ]
  },
  "D_user_explanations": {
    "status": "answered",
    "responses": [
      { "question_id": "...-01", "content": "dificuldade=FACIL;explicacao=Escolhi Transformação porque o texto descreve um ganho a partir de uma quantidade inicial.", "saved": true, "edited_later": false },
      { "question_id": "...-02", "content": "A situação parte de uma quantidade inicial (32) e aplica um ganho (22), então é uma transformação de medidas.", "saved": true, "edited_later": false }
    ]
  },
  "analysis_unit_completeness": "A_B_C_D",
  "research_use": { "eligible_for_behavioral_analysis": true, "eligible_for_explanatory_analysis": true, "missing_explanation_reason": "not_applicable" }
}
```

### Limitações restantes (honestas, não escondidas)

1. **ORIENTAR/CAMINHO/QUANTIFICAR nunca chegam a `protocol_type` de uma
   `B_user_action` avaliada** — existem só como telemetria granular
   (`registrarAcaoGranular`), nunca ligados a uma avaliação real do
   MONITOR. Corrigir isso exigiria decidir, fora do escopo deste pacote,
   se/como essas ações granulares deveriam também virar avaliações C/E —
   não inventado aqui.
2. **Reabrir a explicação para uma unidade já fechada não atualiza
   `unidades_analise.jsonl`** (ver seção própria acima) — o lado do
   Modelador continua correto, o lado da unidade de análise não anexa a
   edição a uma unidade antiga.
3. **`eventos_tecnicos.jsonl` cobre reavaliações reativas do
   `AgentAuditService`** (SINCRONIZACAO_REPRESENTACOES/REAVALIACAO_CONSISTENCIA/
   etc.), não os eventos brutos individuais de mouse (`mousePressed`/
   `mouseDragged`/`mouseReleased`) como o exemplo do pacote sugere — esses
   já são cobertos por outros dois mecanismos das rodadas 3/4
   (`RobotGestureTrace`/`robot_gestos.log` e
   `DespachoMouseReleasedDiagnostico`/`despacho_mouse_released_*.log`, este
   último texto plano, não JSON); duplicá-los aqui arriscaria inventar uma
   terceira fonte não instrumentada de verdade nos listeners reais.
4. **`explicacoes_usuario.jsonl` nunca é populado pelo replay Robot** —
   esperado (o harness não abre a tela), mas significa que a validação de
   ponta a ponta de C/D com dados 100% reais de um episódio histórico
   ainda não existe; o exemplo A_B_C_D acima usa o mesmo serviço real, mas
   com um cenário de demonstração, não um episódio replayado.
5. **`SchemaValidator` continua sendo um subconjunto do JSON Schema**
   (rodada 4) — sem `minItems`/`if`/`dependentSchemas`, então a regra
   "status=answered exige pelo menos uma resposta" é garantida pela LÓGICA
   do serviço (`AnalysisUnitAuditService`), não pelo schema em si; o schema
   valida a FORMA (oneOf por status com campos coerentes), não a
   cardinalidade mínima de `responses`.

---

2026-07-31. Quatro rodadas nesta mesma data: a primeira implementou o log
estruturado (schema 1.0.0); a segunda corrigiu o bug real de produção
(reavaliação reativa mutando estado real) e reestruturou para schema 2.0.0;
a terceira resolveu a perda do segundo gesto incorreto do episódio Jamile
S9 e instrumentou o replay por Robot (schema 3.0.0), mas deixou o disparo
triplo da interrogação como limitação não resolvida (só mitigada); a
quarta (este topo do documento) achou e corrigiu a CAUSA REAL do disparo
triplo, com evidência de baixo nível, conforme
`dados/pacote_rodada4_auditoria_gerard/`. As rodadas 1, 2 e 3 ficam
preservadas abaixo como histórico.

## Status atual (rodada 4 — causa raiz do disparo triplo, 2026-07-31)

### Causa raiz: CONFIRMADA (não mais "não identificada")

A rodada 3 tinha mitigado o disparo triplo (debounce + idempotência) sem
achar a causa. Esta rodada instrumentou a cadeia real de despacho AWT
(`gerard.pesquisador.auditoria.DespachoMouseReleasedDiagnostico`, novo,
grava `despacho_mouse_released_*.log`) e testou as 10 hipóteses pedidas
com evidência real, não suposição:

| # | Hipótese | Evidência | Resultado |
|---|---|---|---|
| 1 | Listener registrado mais de uma vez | `tela.getMouseListeners().length`/`getMouseMotionListeners().length` confirmados **1 e 1** em runtime (não só leitura de código) | Descartada |
| 2 | Mais de um componente recebendo o mesmo release | Todo `[DESPACHO]` mostra `listener_class=Main$TelaGerard` único | Descartada |
| 3 | Redespacho manual | 0 `REDISPATCH_DETECTED` em 72 despachos reais — `dispatch_index=1` sempre | Descartada |
| 4 | Chamada indireta de `mouseReleased` | Mesma evidência acima (cada `physical_event_id` aparece exatamente 1 vez) | Descartada |
| 5 | Criação de novo `MouseEvent` durante sincronização | `event_object_identity` (identityHashCode) sempre distinto entre despachos — cada um é um evento físico genuíno, não um clone | Descartada |
| 6 | Listeners diferentes executando a mesma lógica | Só existe 1 listener (`TelaGerard implements MouseListener`) | Descartada |
| 7 | Reentrada causada por atualização da interface | `mouseReleased` não é reentrante nos 72 despachos (nenhum dispatch_index>1) | Descartada |
| 8 | Chamadas repetidas de `AgentAuditService.iniciarAcao` | Consequência do achado real (item 9), não causa | N/A |
| 9 | Repetição do Robot | `robot_gestos_*.log` confirma **1 única** chamada de `arrastar()` por interrogação — harness não repete nada | Descartada |
| 10 | Correlação incorreta do debounce | Debounce funcionava corretamente (agrupava certo); o problema é que havia 3 disparos REAIS pra agrupar | Sintoma, não causa |

**Causa real**: `executarPassoTexto` arrasta a interrogação ("?") até o
papel-alvo e, **imediatamente depois, na MESMA coordenada**, faz um
duplo-clique pra abrir o diálogo de edição do número. `Main.mousePressed`
não distinguia "clique parado sobre um item que já está no lugar" de "novo
arrasto": cada um dos 2 cliques do duplo-clique também executa
`itemSelecionado = encontrarItemArrastavel(x,y)` (encontra a própria
interrogação, parada ali) e, no `mouseReleased` correspondente, chama
`avaliarQuestionamentoPosicionamento(item, SOLTURA_USUARIO)` de novo — 3
avaliações canônicas reais (o drop + os 2 cliques do duplo-clique) pra 1
gesto do usuário. Confirmado com `x,y` idênticos entre os 3 despachos e
`click_count` progredindo 1→1→2 (padrão exato de um duplo-clique).

### Correção (no ponto arquitetural certo, não só debounce maior)

`Main.mouseReleased` agora guarda a posição do item NO MOMENTO DO PICKUP
(`xDoItemNoPickup`/`yDoItemNoPickup`, novos campos) e, na soltura, só
classifica `OrigemAvaliacao.SOLTURA_USUARIO` (canônico) quando o item
**realmente mudou de posição** entre pickup e soltura — um release sem
deslocamento (incluindo os 2 cliques do duplo-clique, que nunca disparam
`mouseDragged`) vira `OrigemAvaliacao.REAVALIACAO_CONSISTENCIA` (reativo,
não muta estado), reaproveitando a classificação já existente desde a
rodada 3 — nenhum enum novo, nenhuma lógica de decisão duplicada.
Debounce (`AgentAuditService`) e idempotência real (`AgenteZDP.decidirEstrategia`,
`AgenteModelador.armazenarCaso`) foram **preservados como defesa**, não
removidos, conforme pedido explícito.

**Evidência de que a causa sumiu, não só o sintoma**: `duplicados_bloqueados`
caiu de 2/episódio (toda execução da rodada 3) pra **0/episódio em todos
os 14 episódios** desta rodada — a idempotência não precisa mais bloquear
nada porque a chamada duplicada nunca mais é feita.

### Bug adicional encontrado pelo validador de schema (não fazia parte do pedido, achado ao construir o validador real)

`AgentAuditEvent.paraMapa()` tinha `schema_version` **hardcoded em "2.0.0"**
desde a rodada 2 — nunca acompanhava a versão real passada por
`AgentAuditService` (identificação interna ficava certa, só o campo
top-level do JSON é que estava errado). Só foi descoberto porque
`SchemaValidator` (novo, real, ver abaixo) rejeitou um evento real contra
`const: "4.0.0"`. Corrigido: agora lê de `identificacao.getSchemaVersion()`.
Também achado: `blocoModelador.input.help_level` estava declarado
`["string","null"]` no schema, mas `ZdpAuditData.getNivelAjuda()` sempre
foi `int` — erro de autoria do schema desde a rodada 2, corrigido pra
`["integer","null"]`.

### Schema 4.0.0 — validação REAL (não só JSON bem-formado)

`gerard.pesquisador.auditoria.SchemaValidator` (novo) — sem lib externa,
implementa manualmente `$ref` (resolvido contra `#/$defs`), `oneOf`,
`required`, `type` (com arrays de tipos e `null`), `enum`, `const`,
`additionalProperties`, `pattern` e `format: date-time`. Não é o validador
completo do draft 2020-12 (sem `allOf`/`anyOf`/`not`), mas cobre
exatamente os pontos pedidos. Rodado contra dados reais: aceita um evento
real (0 erros) e rejeita uma cópia com `agents` removido + `schema_version`
errado (2 erros detectados) — ver testes 13/14 abaixo.

### Matriz de integração de regras — `matriz_integracao_regras.tsv`

Gerada a partir de dados REAIS de uma execução (não fabricada): MONITOR
consulta `regras_dominio.jsonl` (17 regras) só pra POSICIONAR — 4 regras
ativadas (R-DOM-COMP-001/002, R-DOM-TRANS-001/002), 0 usadas na decisão
final (contexto, não decisão — decisão continua vindo da comparação
determinística de papéis, decisão de arquitetura da rodada 2). ZDP consulta
`regras_pedagogicas.jsonl` (14 regras) pra TODAS as origens, mas só ativa
quando o padrão de erro bate — R-PED-001 disparou e foi usada em
reavaliações reativas desta execução. MODELADOR **não consulta nenhuma
base** em `armazenarCaso` (confirmado por leitura direta do código) — a
ação 2 (`inferirRegras`, PART+Apriori sobre as 432 regras integradas) roda
sob demanda, nunca por ação do usuário. **Nenhuma regra nova foi conectada
nesta rodada** — a matriz é diagnóstica, conforme pedido ("não conecte
indiscriminadamente").

### Os 17 testes pedidos — `gerard.pesquisador.auditoria.TesteRodada4DespachoUnico`

Nova classe (mesmo padrão sem-JUnit de sempre). Rodado contra dados reais
desta sessão:

```
=== Resumo rodada 4: 17 passou, 0 falhou, 0 nao verificado (de 17) ===
```

Destaques: teste 1 confirma que os 14 gestos reais com valor `"?"` têm
exatamente 1 ação canônica cada (não mais 3); teste 4 confirma 0
`REDISPATCH_DETECTED` em 72 despachos reais; testes 13/14 usam o
`SchemaValidator` real contra um evento real e um evento deliberadamente
quebrado; testes 16/17 derrubam deliberadamente o diretório de destino do
logger no meio de uma operação e confirmam que a falha fica registrada
sem interromper a chamada (nenhuma `RuntimeException` escapa).

**Efeito colateral honesto**: corrigir a causa raiz quebrou o teste 12 da
rodada 3 (`TesteCardinalidadeAuditoria`), que provava idempotência
procurando um `duplicate=true` que ocorria NATURALMENTE no JSONL — como
esse natural não existe mais (a causa sumiu), o teste foi reescrito pra
chamar `armazenarCaso` deliberadamente duas vezes com a mesma
`idempotencyKey` sobre um repositório isolado em arquivo temporário
(nunca toca `~/Gerard/perfis_usuario.tsv` real) — mecanismo confirmado
funcionando, não mais dependente de um sintoma do bug corrigido. Suite
completa da rodada 3 revalidada: 18/18 (17 pontos + a checagem dupla do
item 13).

### Validação executada (rodada 4)

1. **Compilação completa**: 0 erros, todo o projeto.
2. **`TesteMonkeyGuiadoPorCasosReais`**: rodado 6+ vezes durante esta
   rodada (diagnóstico + bisseção + validação da correção) — última
   execução: 14 episódios, 45 passos, 0 divergências.
3. **`TesteCardinalidadeAuditoria`** (rodada 3, revalidada): 18/18, 0 falhas.
4. **`TesteRodada4DespachoUnico`** (novo): 17/17, 0 falhas.
5. **`cardinalidade_episodios.tsv`**: `cardinalidade_consistente=true` em
   14/14 episódios, **`duplicados_bloqueados=0` em 14/14** (era 2/14 em
   toda execução da rodada 3 — prova direta de que a causa sumiu).
6. **`perfis_usuario.tsv`/`diagnosticos_tarefa.tsv`**: confirmados
   intactos (timestamps anteriores a esta sessão).
7. **`falhas_auditoria_*.log`**: nenhum arquivo criado nas execuções reais
   do harness (zero falhas internas) — a falha deliberada do teste 16/17
   usa um diretório temporário isolado, não conta pra este número.
8. `scripts/verificar_regressao_gerard.py` e `scripts/testar_*.sh` —
   **não executados**. `ant` continua indisponível neste shell.
9. **Achado operacional, não técnico**: nas primeiras tentativas desta
   rodada, o teste do Robot falhou 0/45 repetidas vezes de um jeito que
   parecia regressão de código — investigação (via `GetForegroundWindow`
   do Windows) revelou que a JANELA EM PRIMEIRO PLANO não era o Gerard
   (era o Chrome, depois um editor de texto) — os cliques sintéticos do
   Robot vão pra QUALQUER janela em primeiro plano, não especificamente
   pro Gerard. Não é um bug de código nenhum; é uma limitação operacional
   do mecanismo de teste (precisa da tela livre). Passou a pedir
   confirmação explícita da usuária antes de cada execução do Robot a
   partir desse ponto.

### Estudo atualizado de Jamile S9

Sequência canônica real desta execução (6 ações, confirmado de novo):
`GESTO-0005` (32→transformação, **E**), `GESTO-0006` (32→estadoFinal,
**E** — o 2º erro, ainda capturado corretamente), `GESTO-0007` (32→estadoInicial,
C), `GESTO-0008` (22→transformação, C), `GESTO-0009` (drop da interrogação,
C — agora com só 1 canônica, não 3), `GESTO-0010` (quantificação, C). Nada
mudou na semântica pedagógica desta rodada — a correção foi puramente na
classificação canônico/reativo do duplo-clique, que nesta rodada específica
não afeta nenhum dos 6 gestos "reais" de Jamile S9 (afeta a interrogação,
`GESTO-0009`, que já eraC e continua C, só sem as 2 repetições fantasma).

### Limitações conhecidas (rodada 4)

- `ant`, `scripts/verificar_regressao_gerard.py`, `scripts/testar_*.sh` —
  não executados (indisponíveis neste ambiente).
- `SchemaValidator` cobre `$ref`/`oneOf`/`required`/`type`/`enum`/`const`/
  `additionalProperties`/`pattern`/`format:date-time` — não é o draft
  2020-12 completo (sem `allOf`/`anyOf`/`not`).
- `despacho_mouse_released_*.log` continua sendo texto plano, não JSON
  Lines — documentado como decisão de arquitetura (`x-decisao-arquitetura`
  no schema), não validado pelo `SchemaValidator`.
- A causa raiz confirmada é específica do padrão "drag-and-drop seguido de
  duplo-clique na mesma posição" (`executarPassoTexto`). Outros padrões de
  interação que também gerem release-sem-deslocamento (ex.: cliques
  simples em itens já posicionados, fora do fluxo de teste automatizado)
  agora também são corretamente classificados como reativos — não foi
  necessário nenhum caso especial pra isso, é a mesma checagem geral
  (posição no pickup vs. na soltura).

### Entregáveis (rodada 4)

1. Projeto atualizado — sem ZIP separado (mesmo padrão das rodadas anteriores).
2. `agentes_execucao_20260731_210727.jsonl` (257 eventos, schema 4.0.0).
3. `agentes_execucao_legivel_20260731_210727.log`.
4. `robot_gestos_20260731_210727.log`.
5. `despacho_mouse_released_20260731_210727.log` — novo nesta rodada.
6. `dados/schema_agentes_execucao_gerard.json` — 4.0.0.
7. `cardinalidade_episodios_20260731_210727.tsv` — 14/14 consistentes, 0 duplicados bloqueados.
8. `matriz_integracao_regras.tsv` — novo nesta rodada.
9. Este relatório (seção "rodada 4" acima).
10. **Causa raiz e evidências**: ver seção própria acima.
11. **Arquivos criados**: `gerard/pesquisador/auditoria/{DespachoMouseReleasedDiagnostico,SchemaValidator,TesteRodada4DespachoUnico}.java`.
    **Arquivos modificados**: `Main.java` (rastreamento de posição no pickup +
    classificação condicional canônico/reativo em `mouseReleased`),
    `AgentAuditEvent.java` (bug do `schema_version` hardcoded),
    `TesteMonkeyGuiadoPorCasosReais.java` (checagem de listeners + arquivo
    de despacho), `TesteCardinalidadeAuditoria.java` (teste 12 reescrito),
    `dados/schema_agentes_execucao_gerard.json` (4.0.0 + correção do
    `help_level`).
12. Resultado da compilação e testes: 0 erros, 18/18 + 17/17 testes, 45/45
    passos, 0 divergências.
13. Resultado da regressão: ver seção "Validação executada" acima.
14. Validação completa do schema: ver seção própria acima.
15. Limitações restantes: ver seção própria acima.

---

## Status atual (rodada 3 — fidelidade do Robot, 2026-07-31)

### O que foi pedido e o que foi entregue

| Critério de aceitação do pacote v3 | Status |
|---|---|
| Segundo erro de Jamile S9 capturado | ✅ Verificado (`GESTO-0006`, ver estudo de caso abaixo) |
| Seis ações pedagógicas canônicas | ✅ Verificado (`acoes_pedagogicas_canonicas=6` em `cardinalidade_episodios.tsv`) |
| Quantificação agregada em uma ação | ✅ Verificado (subeventos compartilham `gesture_id`/`action_id`) |
| Nenhuma falha do Robot silenciosa | ✅ Verificado (76 tentativas registradas para 45 gestos físicos, 0 perdidas) |
| Coordenadas recalculadas | ✅ Verificado (100% das tentativas, `robot_gestos_*.log`) |
| Reavaliações continuam sem alterar estado | ✅ Verificado (preservado da rodada 2, reconfirmado) |
| Testes permanentes criados | ✅ `TesteCardinalidadeAuditoria` (17/17 pontos pedidos, ver seção própria) |
| Schema 3.0.0 válido | ✅ JSON bem-formado (validado com `python`); **não** é validação `$schema`/`$ref` completa (sem lib `jsonschema`) |
| Nenhuma regressão funcional | ✅ 45/45 passos, 0 divergências, mantido em toda a rodada |

### Causa raiz do gesto perdido (não "causa confirmada" por suposição — achada lendo código)

O segundo arraste errado de Jamile S9 ("repete o engano, arrastando 32 para
o estado final") nunca chegava à auditoria porque
`TesteMonkeyGuiadoPorCasosReais.executarPasso` sempre calculava a origem do
arraste a partir de `tela.elementosTexto` (a posição ORIGINAL do numeral no
enunciado) — nunca a partir de `tela.itensArrastaveis` (a posição ATUAL, se
o numeral já tiver sido posicionado, mesmo que errado, antes). Depois do
primeiro arraste errado, `PoliticaUnicidadeElementoMatematicoTexto.jaEstaNoDiagrama`
bloqueia um novo pickup a partir do marcador de texto original (mostra só
um tooltip "já posicionado" e retorna, sem selecionar nada) — o Robot
clicava num lugar que não fazia mais nada, silenciosamente, sem que
`mousePressed` chegasse perto de `agentAuditService.iniciarAcao`.

**Correção**: `SemanticComponentLocator.localizarOrigem` agora procura
PRIMEIRO em `itensArrastaveis` (posição atual, se já no diagrama) e só cai
para `elementosTexto` se o item ainda não tiver sido posicionado —
replicando a mesma prioridade que `Main.mousePressed` já usa de verdade.
`GestureCoordinateResolver` relocaliza origem+destino do zero
imediatamente antes de cada tentativa (nunca reaproveita um ponto de uma
chamada anterior), com retentativa limitada (3 tentativas) em caso de
falha — cada tentativa vira um `RobotGestureAttempt` completo em
`robot_gestos_*.log`, sucesso ou falha.

**Bug real encontrado e corrigido durante a implementação desta correção**
(não fazia parte do pedido original, descoberto ao validar): a primeira
versão de `ComponenteLocalizado` calculava o centro vertical de QUALQUER
componente como `y + altura/2` — correto para `ItemTextoArrastavel`/
`ElementoVergnaud` (que usam `y` como canto superior esquerdo de um
retângulo preenchido), mas ERRADO para `ElementoTextoMovel` (que usa `y`
como baseline de texto — o glifo vai de `y-altura` até `y`, centro real é
`y-altura/2`). Isso quebrou TODO pickup a partir do texto do enunciado (não
só o segundo gesto que esta rodada foi pedida para investigar) — corrigido
antes de qualquer validação ser aceita como válida.

### Achado adicional (não pedido, descoberto ao validar): disparo triplo do `mouseReleased` da interrogação

Ao validar Jamile S9 pela primeira vez com a correção acima, o arrasto da
interrogação ("?") para o papel-alvo — **uma única chamada real** de
`arrastar()` no harness (confirmado: só 1 linha "tentativa 1" no log,
nenhuma retentativa) — produzia **3 eventos canônicos `soltura_usuario`**
separados no JSONL, todos com o mesmo valor (`"?"`) e mesmo papel
(`estadoFinal`), em menos de 700ms. Investigação bounded (não abri tempo
ilimitado nisso): descartei como causa (a) o loop de retentativa do
próprio harness (confirmado só 1 tentativa no log), (b)
`finalizarProxyTextoSolto` (não tem laço nem re-chama a avaliação), (c)
múltiplos pontos de chamada de `OrigemAvaliacao.SOLTURA_USUARIO` em
`Main.java` (confirmado só 1 ocorrência no código-fonte inteiro). A causa
exata (por que `mouseReleased` despacha 3 vezes para este único
release físico) **não foi identificada** — fica como limitação registrada,
não como fato inventado.

**Mitigação aplicada** (real, não só cosmética): como não pude eliminar a
causa, protegi o EFEITO — dois mecanismos novos, complementares:
1. `AgentAuditService.iniciarAcao` — debounce: uma chamada canônica para a
   MESMA tarefa+valor em menos de 1,5s reaproveita o `gesture_id` anterior
   em vez de mintar um novo (nunca esconde a linha do log — as 3 ainda
   aparecem no JSONL, só compartilham identidade).
2. `AgenteZDP.decidirEstrategia` ganhou uma sobrecarga com
   `idempotencyKey` (mesmo padrão que `AgenteModelador.armazenarCaso` já
   tinha) — chamadas repetidas com a mesma chave devolvem a MESMA
   estratégia já decidida, sem mutar `errosConsecutivosPorTarefa` de novo.
   Sem isto, a idempotência do Modelador sozinha bloqueava a inserção de
   caso duplicado, mas o ZDP continuava mutando estado real 3 vezes por
   disparo triplo — achado só depois de notar
   `cardinalidade_consistente=false` no TSV (o próprio mecanismo de
   verificação pedido nesta rodada expôs o problema).

A contagem de `cardinalidade_episodios.tsv` também mudou: `acoes_pedagogicas_canonicas`/
`decisoes_zdp`/`atualizacoes_modelador` agora contam por **gesture_id
distinto**, não por linha — sem isso, mesmo com o debounce/idempotência
acima, cada disparo duplicado ainda inflava a contagem de ações (a
mutação real parava de se repetir, mas a LINHA do log continuava sendo
contada 3x).

### Agregação da quantificação (sem `subevents` aninhado — desvio deliberado)

O pacote pedia um campo `subevents` DENTRO de cada evento JSON. Implementado
diferente: `AgentAuditService.reservarProximoGesto()`/`liberarReservaDeGesto()`
fazem `editarNumeroNatural` reservar um `gesture_id`/`action_id` ANTES da
checagem de posição (agora `OrigemAvaliacao.REAVALIACAO_CONSISTENCIA`,
reativa — não conta como ação nova) e liberá-lo depois da checagem de
valor em `confirmarValorIncognitaAceito` (`OrigemAvaliacao.QUANTIFICACAO`,
canônica) — as duas chamadas de `iniciarAcao` (origens diferentes) usam o
MESMO id, cada uma como sua própria linha no JSONL, diferenciadas por
`evaluation_id`. Registrado como desvio de arquitetura no schema
(`x-decisao-arquitetura`), não escondido.

### Estudo de caso: Jamile S9, dados reais da rodada 3

Sequência canônica completa (`EP-HIST_DOUTORADO_JAMILE_S9_090610-2`, 6
ações pedagógicas — exatamente o pedido):

| Gesto | Origem | Valor | Papel origem→destino | Veredito Monitor | Erros consecutivos da TAREFA (antes→depois) | Caso inserido |
|---|---|---|---|---|---|---|
| GESTO-0005 | soltura_usuario | 32 | estadoInicial→transformação | **E** | 0→1 | sim |
| GESTO-0006 | soltura_usuario | 32 | estadoInicial→**estadoFinal** | **E** | 0→1 | sim |
| GESTO-0007 | soltura_usuario | 32 | estadoInicial→estadoInicial | C | 0→0 | sim |
| GESTO-0008 | soltura_usuario | 22 | transformação→transformação | C | 1→0 | sim |
| GESTO-0009 | soltura_usuario | ? | estadoFinal→estadoFinal | C | 1→0 | sim (+ 2 duplicatas bloqueadas, ver achado acima) |
| GESTO-0010 | quantificação | 54 | (digitação)→estadoFinal | C | 0→0 | sim |

**GESTO-0006 é o gesto que a rodada 2 tinha perdido** — agora chega com
`gesture_id` próprio, `action_id` próprio, avaliação real `E` do Monitor,
uma única decisão real do ZDP e um único caso inserido no Modelador
(critérios de aceitação todos verificados por `TesteCardinalidadeAuditoria`,
testes 5/6/7).

**Divergência honesta do que o pacote v3 previa**: a especificação pedia
progressão `0 → 1 → 2 → 0 → 0 → 0 → 0` (um único contador global subindo
2 antes de resetar). O real `AgenteZDP` guarda erros consecutivos **por
tarefa** (`errosConsecutivosPorTarefa`, chave = usuário+categoria+papel-alvo
— confirmado no código, não é suposição) — GESTO-0005 erra contra a tarefa
"transformação", GESTO-0006 erra contra a tarefa "estadoFinal": são
tarefas DIFERENTES, cada uma com seu próprio contador, então cada uma sobe
0→1 independentemente, nunca 1→2. Isso é arquiteturalmente correto (um
erro no papel "estado inicial" não deveria inflar o contador de erro do
papel "estado final" — são dificuldades conceituais distintas, alinhado
com Vergnaud) — não alterei essa lógica pra forçar o número que o pacote
previa, porque isso exigiria fabricar um contador global que não existe e
não reflete o que o ZDP realmente decide. Reportado aqui em vez de
escondido.

### Os 17 testes pedidos — `gerard.pesquisador.auditoria.TesteCardinalidadeAuditoria`

Classe reexecutável (não é JUnit — projeto não tem biblioteca de teste no
classpath, mesmo padrão de `TesteReplayProtocolosReais`/`ValidadorJsonl`:
`public static void main`, assert manual, `System.exit(1)` em falha real).
Dois testes são de UNIDADE (não precisam de execução prévia do monkey
test); os demais lêem o JSONL/legível/TSV/log mais recentes de verdade.
Rodado contra os dados reais desta sessão:

```
=== Resumo: 18 passou, 0 falhou, 0 nao verificado (de 17) ===
```

(18 porque o item 13 — unicidade de gesture_id/action_id/evaluation_id —
tem uma checagem de unidade E uma de integração, ambas reportadas.) Todos
os 17 pontos pedidos foram executados com dados reais, nenhum
`NAO_VERIFICADO` na execução final — quando um teste não tinha dado
disponível (ex.: log legível não trazia `event_id` no formato que eu
esperava na primeira tentativa), corrigi o parser para o formato REAL do
arquivo em vez de inventar o resultado.

### Validação executada (rodada 3)

1. **Compilação completa**: `javac` manual, 383 arquivos `.java`, 0 erros.
2. **`TesteMonkeyGuiadoPorCasosReais`**: rodado 6 vezes durante esta rodada
   (cada correção intermediária foi revalidada, não só a versão final) —
   última execução: 14 episódios, 45 passos, 0 divergências.
3. **`TesteCardinalidadeAuditoria`**: 17/17 pontos pedidos, 0 falhas (ver acima).
4. **`cardinalidade_episodios.tsv`**: `cardinalidade_consistente=true` em
   14/14 episódios (ações canônicas == decisões ZDP == atualizações
   Modelador == casos inseridos, em TODOS os episódios, não só Jamile S9).
5. **`perfis_usuario.tsv`/`diagnosticos_tarefa.tsv`**: confirmados intactos
   (timestamps anteriores a esta sessão) — mecanismo de backup/restauração
   do harness continua funcionando.
6. **`falhas_auditoria_*.log`**: nenhum arquivo criado em nenhuma das 6
   execuções — zero falhas internas do serviço de auditoria.
7. `scripts/verificar_regressao_gerard.py` e `scripts/testar_*.sh` — **não
   executados**. `ant` continua indisponível neste shell. Não declaro como
   executado o que não foi.
8. Teste deliberado de falha de pickup: coberto pelo teste de unidade #2
   de `TesteCardinalidadeAuditoria` (constrói um `RobotGestureAttempt` com
   `componentFound=false` direto, sem precisar de uma falha real do Robot
   em produção — mais confiável que esperar uma falha acontecer sozinha).
   Teste deliberado de falha do LOGGER (não do Robot) — **não executado**
   nesta rodada; `FalhaAuditoriaLogger` já tem cobertura architectural da
   rodada 2 (try/catch em toda chamada pública de `AgentAuditService`), não
   testado de novo aqui.

### Limitações conhecidas (rodada 3)

- **Causa exata do disparo triplo do `mouseReleased` da interrogação não
  identificada** — mitigada (debounce + idempotência real), não
  root-caused. Precisaria depuração interativa (breakpoint no
  `MouseListener`, não só leitura de código) pra confirmar o mecanismo
  exato. Maior limitação honesta desta rodada.
- **`robot_trace`/`subevents` não ficam embutidos dentro de cada evento
  JSON** — ficam em arquivos separados (`robot_gestos_*.log`,
  `cardinalidade_episodios.tsv`) correlacionáveis por `gesture_id`/
  `episode_id`. Desvio de arquitetura deliberado (separar "o que os
  agentes decidiram" de "o que o Robot fez tecnicamente"), documentado no
  schema (`x-decisao-arquitetura`), não uma omissão.
- Schema 3.0.0 validado como JSON bem-formado (`python`), não como
  `$schema`/`$ref`/`oneOf` completo — sem `jsonschema` instalado. O teste
  #16 é uma checagem estrutural manual dos campos obrigatórios, não uma
  validação de schema real.
- `ant`, `scripts/verificar_regressao_gerard.py`, `scripts/testar_*.sh` e
  teste deliberado de falha do logger — não executados nesta rodada (ver
  seção de validação).
- Progressão de erro `0→1→2→0` do pacote v3 não é literal no sistema real
  (contadores são por tarefa/papel-alvo, não globais) — ver estudo de caso.

### Entregáveis (rodada 3)

1. Projeto atualizado: `C:\Users\cecomp\Documents\aemq\Gerard\src` — sem
   ZIP separado (mesmo padrão das rodadas 1/2; não há passo de instalação
   distinto do próprio diretório do projeto).
2. `agentes_execucao_20260731_171956.jsonl` (mais recente, 323 eventos).
3. `agentes_execucao_legivel_20260731_171956.log`.
4. `dados/schema_agentes_execucao_gerard.json` — 3.0.0.
5. `cardinalidade_episodios_20260731_171956.tsv` — 14/14 episódios com
   `cardinalidade_consistente=true`.
6. `falhas_auditoria_*.log` — nenhum criado (zero falhas internas).
7. `robot_gestos_20260731_171956.log`.
8. Este relatório (seção "rodada 3" acima; rodadas 1/2 preservadas abaixo).
9. **Arquivos criados**: `SemanticComponentLocator.java`,
   `GestureCoordinateResolver.java` (pacote default),
   `gerard/pesquisador/replay/robot/{RobotGestureStatus,ComponenteLocalizado,
   RobotGestureAttempt,RobotGestureTrace,RobotGestureLogger,EpisodeCardinalityReport}.java`,
   `gerard/pesquisador/auditoria/TesteCardinalidadeAuditoria.java`.
   **Arquivos modificados**: `TesteMonkeyGuiadoPorCasosReais.java`
   (`executarPasso`/`executarPassoTexto` reescritos), `Main.java`
   (`editarNumeroNatural`/`confirmarValorIncognitaAceito` — reserva de
   gesto; 5 pontos de chamada de `decidirEstrategia` — idempotencyKey),
   `AgentAuditService.java` (debounce, contadores por gesto distinto,
   `finalizarEpisodio` agora devolve o resumo em vez de escrever o TSV
   antigo sozinho), `AgenteZDP.java` (idempotência),
   `FalhaAuditoriaLogger.java` (renomeado nesta e na rodada anterior — sem
   mudança nesta rodada).
10. Resultado da compilação e testes: ver seção "Validação executada"
    acima — 0 erros de compilação, 45/45 passos, 17/17 pontos de teste.
11-14. Ver seções "Estudo de caso" e "Os 17 testes pedidos" acima.
15. Ver "Limitações conhecidas" acima.

---

## Status atual (rodada 2 — correção)

**O bug real**: `avaliarQuestionamentoPosicionamento` é chamado de 7 pontos
diferentes em `Main.java`. Antes desta correção, TODOS os 7 chamavam as
versões REAIS de `AgenteZDP.decidirEstrategia`/
`ConectorVereditoModelador.registrarVeredito` — inclusive
`atualizarQuestionamentoPersistenteDuranteMovimento`, que roda a cada evento
de mouse **durante o arraste**, antes de soltar. Ou seja: no app real (não
só no teste), arrastar um item por cima de um alvo válido podia inflar
erros consecutivos, escalonar a camada do ZDP e inserir casos duplicados no
Modelador — um problema de estado pedagógico real, não só de log verboso.

**A correção**: `OrigemAvaliacao` (10 valores: `soltura_usuario`,
`selecao_categoria`, `selecao_sinal`, `quantificacao`, `solicitacao_ajuda`
são canônicos; `sincronizacao_representacoes`, `reavaliacao_consistencia`,
`atualizacao_diagrama`, `atualizacao_representacao`, `outro` são reativos).
Cada um dos 7 pontos de chamada agora passa a origem certa. Dentro de
`avaliarQuestionamentoPosicionamento`, o gate real ficou assim:

```java
if (resultado != null && resultado.isAplicavel() && origem.isCanonica()) {
    // só aqui AgenteZDP.decidirEstrategia / registrarVeredito (real) são chamados
}
```

Avaliações reativas continuam aparecendo no log técnico (pedido explícito
da usuária: não esconder que a chamada aconteceu), mas com os blocos
ZDP/MODELADOR preenchidos por um caminho **dry-run** novo —
`AgenteZDP.avaliarSemAlterarEstado`/`AgenteModelador.avaliarSemArmazenar` —
que reaproveita a MESMA lógica de decisão dos métodos reais (nada
duplicado), só sem escrever nos mapas internos/repositório. No log, esse
bloco aparece marcado `"(hipotética)"` na camada e
`classificacao_evento.canonical=false` — nunca confundível com uma decisão
real gravada.

**Idempotência**: `AgenteModelador.armazenarCaso` ganhou uma checagem por
chave `session_id|episode_id|gesture_id` (`Set<String>` interno) — um
gesto canônico só insere caso uma vez, mesmo se `registrarVeredito` fosse
chamado de novo pra ele por engano.

### Dois bugs adicionais encontrados e corrigidos durante a validação desta rodada

1. **`protocolos_reais_replay.tsv` estava com o formato errado outra vez.**
   Ao rodar `TesteMonkeyGuiadoPorCasosReais` pela primeira vez nesta rodada,
   o teste travou (processo Java vivo, mas 0 episódios processados) porque
   `src/gerard/pesquisador/replay/dados/protocolos_reais_replay.tsv` tinha
   voltado a ser o dump bruto de 2782 linhas/17 colunas (`PR0001`, `PR0002`...
   na coluna de categoria), não o arquivo curado de 418 linhas/10 colunas
   (`idUsuarioReal categoria rotulo ordem tipoPasso chavePapelNumeral
   chavePapelAlvo categoriaEscolhida correto descricao`) que
   `RepositorioProtocolosReaisReplay` espera. `TipoSituacaoAditiva.valueOf("PR0001")`
   lançava `IllegalArgumentException` na primeira linha. **Corrigido**:
   restaurado a partir do backup verificado em
   `build/classes/gerard/pesquisador/replay/dados/protocolos_reais_replay.tsv`
   (418 linhas, 249 POSICIONAR + 87 CATEGORIA + 81 TEXTO — bate exatamente
   com o que já estava documentado). O dump bruto de 2782 linhas foi
   preservado à parte (scratchpad desta sessão), não apagado.
2. **`TesteMonkeyGuiadoPorCasosReais` engolia a exceção que causou o item 1.**
   O handler de exceção não-tratada da thread `main` escrevia só no `log`
   (`PrintWriter` do arquivo `monkey_casos_reais_*.log`) — mas o `finally`
   do método `main` **já tinha fechado esse mesmo `PrintWriter`** antes da
   exceção chegar ao handler (toda exceção não-capturada só é despachada
   pro handler DEPOIS do `finally` rodar). Resultado: `println`/
   `printStackTrace` em um `PrintWriter` fechado são no-op silencioso — a
   exceção desaparecia sem deixar rastro em nenhum arquivo, e o processo
   Java ficava vivo indefinidamente (janela do Swing ainda aberta, thread
   AWT não-daemon) parecendo travado, sem nunca ter de fato travado (a
   thread `main` já tinha terminado). Só foi possível diagnosticar via
   `jstack` (ausência da thread `main` no dump = já tinha terminado) e
   inspeção manual do TSV. **Corrigido**: o handler agora também escreve em
   `System.err` (que o redirecionamento do shell captura mesmo depois do
   `log` fechar).
3. **`AgentAuditService.iniciarAcao` nunca usava `episodeIdAtual`/`sessionIdAtual` de fallback.**
   A expressão `identificacao == null ? episodeIdAtual : identificacao.getEpisodeId()`
   está errada: `identificacao` (o objeto vindo de `Main.java`) nunca é
   `null` nos 9 pontos de chamada reais — é o **campo** `episodeId` dentro
   dele que vem `null` sempre (só o harness de teste sabe o episódio, via
   `definirContextoEpisodio`, não `Main.java`). Resultado: todo evento
   individual saía com `episode_id`/`session_id` nulos no JSONL (só a linha
   `episode_summary` tinha o valor certo, porque essa é escrita à parte).
   **Corrigido**: agora checa se o campo em si é nulo, não o objeto
   contêiner.

Os itens 1 e 3 tornavam impossível validar o caso Jamile S9 pedido
explicitamente pela usuária (sem dado real carregado, e sem
`episode_id` pra filtrar por episódio). Com os dois corrigidos, a validação
abaixo é sobre dados reais, gerados nesta sessão.

## Validação executada nesta rodada (dados reais, sem simulação)

1. **Compilação**: `javac` manual (classpath Weka+Bounce), 0 erros — todo o
   projeto, não só os arquivos tocados.
2. **Correção do ambiente de execução**: `ant` continua indisponível neste
   shell. **Descoberta nesta rodada**: `python3` (o comando) é mesmo um stub
   quebrado da Microsoft Store, mas `python` (sem o "3") é um Python 3.13
   real e funcional — usado para validar JSON/agregações abaixo. Não tem
   `jsonschema` instalado (não tentei instalar sem perguntar), então a
   validação contra `schema_agentes_execucao_gerard.json` foi **estrutural
   manual** (todo campo obrigatório presente, JSON bem-formado linha a
   linha), não uma validação `$schema`/`$ref` completa — limitação real,
   registrada, não meio-escondida.
3. `TesteMonkeyGuiadoPorCasosReais`: **14 episódios, 45 passos (contador do
   harness), 0 divergências** — arquivo final:
   `~/Gerard/logs/agentes_execucao_20260731_145939.jsonl` (241 linhas: 227
   eventos + 14 resumos de episódio).
4. **241/241 linhas JSON válidas**, todos os 7 campos obrigatórios de
   `evento` presentes em 100% das 227 linhas de evento (checado com
   `python`, script descartável desta sessão).
5. `perfis_usuario.tsv`/`diagnosticos_tarefa.tsv` restaurados ao estado
   anterior ao teste (mecanismo de backup já existente, confirmado intacto
   — os timestamps dos arquivos reais continuam de antes desta sessão).
6. `falhas_auditoria_*.log`: **nenhum arquivo criado** — `FalhaAuditoriaLogger`
   só grava por escrita (não no construtor), então isso confirma zero
   falhas internas do serviço de auditoria durante os 227 eventos, mas
   também significa que não pude confirmar ao vivo que o mecanismo de log
   de falha escreve corretamente (nenhuma falha ocorreu para testar).

### Cardinalidade por episódio (`cardinalidade_episodios_20260731_145939.tsv`)

| Episódio | Gestos | Ações canônicas | Aval. técnicas | Aval. reativas | ZDP canônico | Modelador canônico | Casos inseridos | Duplicados bloqueados | Divergências |
|---|--:|--:|--:|--:|--:|--:|--:|--:|--:|
| Felipe Wanderley 01-06-10 P1 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| **Jamile S9 09-06-10 P2** | 7 | 7 | 18 | 11 | 7 | 7 | 7 | 0 | 0 |
| Jamile S8 08-06 P1 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| FelipeWanderley 15-06-10 Q3 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| FelipeWanderley 17-07-10 Q1 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| FelipeWanderley 17-07-10 Q4 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| FelipeWanderley 19-07-10 Q2 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| FelipeWanderley 19-07-10 Q6 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 08-06-10 P2 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 08-06-10 P4 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 13-07-10 P1 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 13-07-10 P4 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 14-07-10 P3 | 7 | 7 | 15 | 8 | 7 | 7 | 7 | 0 | 0 |
| Jamilly 14-07-10 P7 | 7 | 7 | 17 | 10 | 7 | 7 | 7 | 0 | 0 |
| **Total (14 episódios)** | **98** | **98** | **227** | **129** | **98** | **98** | **98** | **0** | **0** |

O que isso prova: em **100% dos 14 episódios**, `ações canônicas ==
decisões canônicas do ZDP == atualizações canônicas do Modelador == casos
inseridos`, com **0 duplicados bloqueados** e **0 divergências** entre o
veredito real do `AgenteMonitor` e o rótulo humano original do protocolo.
Isso é a prova de que o gate `origem.isCanonica()` está de fato impedindo
mutação de estado real em avaliação reativa — o total de 227 avaliações
técnicas nunca se traduziu em mais de 98 mutações reais, mesmo com 129
reavaliações reativas registradas no log.

## Estudo de caso: Jamile S9 (episódio pedido explicitamente)

`EP-HIST_DOUTORADO_JAMILE_S9_090610-2` — 18 avaliações técnicas, **7 ações
canônicas** (não 6, ver observação abaixo), 11 reavaliações reativas, 0
duplicados, 0 divergências. Sequência real (só linhas canônicas):

| Gesto | Origem | Veredito Monitor | Erros consecutivos antes → depois |
|---|---|---|---|
| GESTO-0008 | soltura_usuario | **E** | 0 → **1** |
| GESTO-0009 | soltura_usuario | C | 1 → **0** |
| GESTO-0010 | soltura_usuario | C | 0 → 0 |
| GESTO-0011 | soltura_usuario | C | 0 → 0 |
| GESTO-0012 | soltura_usuario | C | 0 → 0 |
| GESTO-0013 | quantificacao | C | 0 → 0 |
| GESTO-0014 | quantificacao | C | 0 → 0 |

Progressão real do contador de erros consecutivos: **0 → 1 → 0 → 0 → 0 → 0
→ 0**.

**Divergência do que o pacote de correção original previa** (6 ações
canônicas, progressão `0→1→2→0`, dois erros reais seguidos antes da
correção): o protocolo curado tem de fato **dois** arrastes errados
seguidos (`ordem=2`: estadoInicial→transformação, errado; `ordem=3`:
estadoInicial→estadoFinal, "repete o engano", errado) antes do acerto
(`ordem=4`). O log de passos do harness confirma que o Robot **executou**
os 6 arrastes/digitação nas coordenadas certas (`monkey_casos_reais_*.log`,
seção do episódio). Mas o **segundo** arraste errado (`ordem=3`) não
produziu evento nenhum no log de auditoria — nem canônico, nem reativo —
só as duas reavaliações reativas de `GESTO-0008` aparecem, e elas reavaliam
o MESMO alvo do primeiro arraste (transformação), não o alvo do segundo
(estadoFinal).

**Hipótese não confirmada** (não vou apresentar como fato): a animação de
tremor/feedback de erro disparada pelo primeiro arraste errado pode ter
deslocado visualmente o item antes do Robot tentar pegá-lo de novo para o
segundo arraste — como o harness calcula as coordenadas de origem uma única
vez, antes de qualquer feedback visual, um "pickup" que erra o alvo produz
`itemSelecionado == null` no `mouseReleased`, e
`avaliarQuestionamentoPosicionamento(null, ...)` retorna cedo (`item ==
null`) **antes** de chamar `agentAuditService.iniciarAcao` — nem canônico
nem reativo, silêncio total, exatamente o que se observa. Verificar isso
exigiria depuração interativa (não fiz — ficaria simulando certeza que não
tenho). Fica registrado como próximo passo concreto, separado da correção
desta rodada (é uma questão de fidelidade do Robot ao protocolo, não do
mecanismo de auditoria canônico/reativo, que se comportou corretamente com
os 6 gestos que efetivamente chegaram até ele).

As 7 ações canônicas (não 6) vêm de: 4 arrastes de posicionamento
realmente distintos (`ordem=2,4,5` + a checagem de posição do item da
incógnita) + a checagem de posição do "?" antes de editar (`GESTO-0013`,
classificada `quantificacao` porque acontece dentro de
`editarNumeroNatural`) + a confirmação do valor digitado (`GESTO-0014`).
Ou seja: a ação "digitar 54" do protocolo (1 linha curada) gera 2 avaliações
canônicas reais no Gérard (posição do item + valor digitado) — são duas
perguntas diferentes que o sistema já fazia antes desta correção, só que
agora ambas ficam visíveis e corretamente marcadas como canônicas.

## Antes/depois (rodada 1 → rodada 2)

| Métrica | Rodada 1 (schema 1.0.0) | Rodada 2 (schema 2.0.0, corrigida) |
|---|---|---|
| Eventos no JSONL | 227 (tudo junto, sem distinção) | 227 técnicos = 98 canônicos + 129 reativos (agora rotulados) |
| Mutação real de estado por evento reativo | **Sim** (bug de produção) | **Não** — dry-run (`avaliarSemAlterarEstado`/`avaliarSemArmazenar`) |
| `episode_id`/`session_id` em eventos individuais | N/A (não existia) | Corrigido nesta rodada (bug 3 acima) |
| Casos duplicados no Modelador | Não medido | 0/98 (idempotência por `session_id\|episode_id\|gesture_id`) |
| `category` chega ao Monitor como enum | Não (só texto localizado) | Parcial: nova sobrecarga de `avaliarPosicionamento` aceita `TipoSituacaoAditiva`; usada pelos 2 chamadores reais (`Main.java`, `TesteReplayProtocolosReais`) |
| `cardinalidade_episodios.tsv` | Não existia | Gerado a cada episódio, 1 linha por episódio |
| `falhas_auditoria.log` | Nome antigo: `auditoria_falhas_*.log` | Renomeado pra `falhas_auditoria_*.log` (prefixo pedido) |

## O que ficou `null`/incompleto — e por quê (continua válido da rodada 1)

| Campo | Por que está `null`/vazio |
|---|---|
| `MODELADOR.diagnosis_before/after` | Sem motor de diagnóstico automático. |
| `MODELADOR.detected_strategy` | `R-ENT-*` dependem de texto livre preenchido pelo pesquisador **depois** da ação — decisão da usuária (2026-07-23) de não ligar classificação automática de texto livre sem curadoria. |
| `MODELADOR.rule_or_pattern_incorporated` | Ação sob demanda (`inferirRegras`), não por caso armazenado. |
| `MODELADOR.new_case_inserted.similarity_to_existing_case` | `armazenarCaso` não calcula similaridade. |
| `MONITOR.rules_fired` (posicionamento) | Vazio quando a sobrecarga só-com-texto é usada (nenhum chamador restante faz isso hoje, mas a sobrecarga continua existindo). Quando a categoria enum chega, lista as regras de domínio como CONTEXTO (`used_in_final_decision=false` sempre — o veredito continua vindo da comparação determinística de papéis, não das regras). |
| `MONITOR.rules_fired` (sinal/incógnita/categoria) | Essas 3 assinaturas não recebem contexto suficiente pra consultar regra nenhuma. |
| `user_profile_before/after` | Sempre `available=false` — `ModeloUsuario`/`PerfilAluno`/`PerfilAprendizagem` não guardam isso hoje. |
| `audit_session_counters_*` | **Reais**, mas agregados pelo próprio `AgentAuditService`, não pelo Modelador — agora em dois grupos (`technical_evaluation_counters`/`canonical_user_action_counters`), nunca confundidos com o perfil persistido. |
| `interaction_result.divergence` | `null` (não `false`) quando `comparison_status=not_evaluable` — corrigido nesta rodada, como pedido. |

## Limitações conhecidas

- A discrepância do caso Jamile S9 (item acima) não foi resolvida —
  registrada como hipótese, não como fato, com o próximo passo concreto
  (depuração interativa do pickup do Robot durante animação de erro).
- `ant` continua indisponível neste ambiente de execução.
- Validação de schema é estrutural manual (sem `jsonschema` instalado) —
  não é uma validação `$ref`/`oneOf` completa contra
  `schema_agentes_execucao_gerard.json`.
- `MONITOR.rules_fired` só é preenchido para posicionamento quando a
  categoria enum é passada — sinal/incógnita/categoria continuam sem
  regras de domínio ligadas (fora de escopo desta rodada).
- Os 14 automatizados testes explicitamente listados no pedido expandido
  não foram implementados como métodos de teste formais — a validação
  desta rodada foi feita via inspeção direta do JSONL real com `python`
  (scripts descartáveis, não commitados), não via suíte automatizada
  reexecutável. Registrado como próximo passo caso a usuária queira testes
  permanentes.

## Entregáveis

- Projeto atualizado: `C:\Users\cecomp\Documents\aemq\Gerard\src`.
- `dados/schema_agentes_execucao_gerard.json` — reescrito para 2.0.0.
- `src/gerard/pesquisador/replay/dados/protocolos_reais_replay.tsv` —
  restaurado ao formato curado de 418 linhas (bug 1 acima).
- Execução de referência usada para todas as validações acima:
  `~/Gerard/logs/agentes_execucao_20260731_145939.jsonl`,
  `agentes_execucao_legivel_20260731_145939.log`,
  `cardinalidade_episodios_20260731_145939.tsv`.
- Este relatório.
