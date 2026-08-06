# Auditoria — consistência interna de REFERENCE.md após as 10 decisões da Revisão 5

Data: 2026-08-06. Somente leitura — nenhum código, nenhuma edição de documentação, nenhum commit. Rigor epistêmico: [FATO NORMATIVO], [EVIDÊNCIA NO CÓDIGO], [INCONSISTÊNCIA], [INFERÊNCIA], [INCONCLUSIVO].

Escopo: `REFERENCE.md` inteiro (489 linhas, lido do início ao fim), os três `TAREFA_PENDENTE_*.md`, compilação completa, os dois harnesses do piloto, `git status`.

---

## 1. Contradições entre seções — nenhuma encontrada; uma redundância significativa

**[INCONSISTÊNCIA] Critério SISTEMA/INFERENCIA_COMPUTACIONAL declarado duas vezes, com formulações quase idênticas, na mesma seção.**

- Primeira declaração: `REFERENCE.md:182-191` — "O critério que distingue SISTEMA de INFERENCIA_COMPUTACIONAL é o tipo de operação, não o determinismo do algoritmo" + definição em bullets.
- Segunda declaração: `REFERENCE.md:195-200` — "Critério de desempate entre SISTEMA e INFERENCIA_COMPUTACIONAL para operações automáticas: SISTEMA é a reaplicação de uma regra ou equação fixa... INFERENCIA_COMPUTACIONAL é quando o sistema produz uma classificação, diagnóstico, estimativa ou regra derivada de evidências..."

As duas dizem substantivamente a mesma coisa, com palavras diferentes (a primeira fala em "determinismo do algoritmo"; a segunda, em "operação seja complexa" — mesma ideia, vocabulário distinto). Não são contraditórias — são redundantes. A segunda foi inserida nesta sessão (decisão item 1 da fila) como reforço com exemplo concreto (linhas 202-209) e nota de implementação (211-214), mas o parágrafo "critério de desempate" em si (195-200) não acrescenta uma regra nova além do que já existia em 182-191. Risco: as duas podem divergir em edições futuras se só uma for atualizada.

Nenhuma outra contradição de conteúdo foi encontrada entre seções novas e antigas, nem entre seções novas entre si — as dez decisões da fila da Revisão 5, lidas em sequência, são internamente coerentes umas com as outras (cardinalidade → Scaffolding → modalidade de interação → eixos de Scaffolding → arquitetura de evento → versionamento → FEEDBACK_EXIBIDO, todas em §4.8, formam uma progressão sem se contradizerem).

## 2. Referências cruzadas — quatro instâncias de "parágrafo anterior" desatualizadas

**[INCONSISTÊNCIA] "Parágrafo anterior" / "Seção anterior" não aponta mais para o parágrafo imediatamente anterior em quatro pontos**, porque decisões posteriores foram inseridas entre a referência e o trecho referido:

1. `REFERENCE.md:276`: "`action_id` (Seção anterior, cardinalidade 1:N)" — o parágrafo de cardinalidade está em `216-228`, mas entre ele e a linha 276 foram inseridos os parágrafos de Scaffolding (230-236), vocabulário de modalidade (238-244) e eixos de Scaffolding (246-265) — três blocos de distância, não "a seção anterior".
2. `REFERENCE.md:281-282`: "modalidade de interação (parágrafo anterior)" — o parágrafo de vocabulário de modalidade está em `238-244`; entre ele e a linha 281 foram inseridos os eixos de Scaffolding (246-265) e a introdução de arquitetura de evento (267-273) — dois blocos de distância.
3. `REFERENCE.md:282-283`: "estilo de Scaffolding utilizado, quando houver (parágrafo anterior)" — o parágrafo dos eixos de Scaffolding está em `246-259`; mesma distância do caso anterior.
4. `REFERENCE.md:317`: "vocabulário de modalidade de interação, parágrafo anterior" — o parágrafo real está em `238-244`; entre ele e a linha 317 foram inseridos quatro blocos (eixos de Scaffolding, arquitetura de evento, versionamento, e o início do próprio parágrafo de FEEDBACK_EXIBIDO) — a distância mais longa das quatro.

Essas quatro referências eram provavelmente corretas no momento em que cada trecho foi proposto isoladamente (quando o parágrafo referido de fato precedia imediatamente o texto novo), mas ficaram desatualizadas à medida que decisões subsequentes foram inseridas entre elas — exatamente o risco que a pergunta 2 da auditoria antecipava.

**[EVIDÊNCIA NO CÓDIGO] Uma referência inconsistente em estilo, não em conteúdo**: `REFERENCE.md:214` diz "ver registro separado sobre a lacuna de log **abaixo**". Não há nenhum outro trecho sobre "lacuna de log" mais abaixo dentro do próprio `REFERENCE.md` — o conteúdo referido está em `TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`, um arquivo externo. Isso contrasta com o padrão usado em `REFERENCE.md:114-115`, que nomeia o arquivo explicitamente ("é o escopo da tarefa registrada em `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`"). A palavra "abaixo" sugere continuidade dentro do mesmo documento, mas não há nada abaixo — só no arquivo externo, não nomeado.

**Referências que conferem, sem problema**: `(§4.8.1)` em `393-394`; `(seção 4.8)` em `382`; `(Seção 4.10)` em `323`; `(Seção 19.1 da Revisão 5)` em `267`, `(Seção 17 da Revisão 5)` e `(Seção 15)` em `415-416` — todas essas apontam corretamente para onde deveriam (internamente) ou são citações claramente externas (à Revisão 5).

**[INCONCLUSIVO] Referências à Revisão 5 por número de seção** (`Seção 19.1`, `Seção 17`, `Seção 15`, `Seção 21`, `Seção 26`, `Seção 13`) citadas em várias das dez decisões: como o relatório da Revisão 5 nunca foi salvo no repositório (confirmado em investigação anterior desta sessão), essas citações são hoje inverificáveis para qualquer leitor que só tenha acesso a `REFERENCE.md` — não são incorretas, mas são órfãs: apontam para um documento que não existe neste repositório. Não constitui erro no `REFERENCE.md` em si, mas é uma fragilidade de rastreabilidade.

## 3. Números de linha citados dentro do próprio REFERENCE.md

Nenhum encontrado. Todas as citações numéricas de linha em `REFERENCE.md` referem-se a arquivos externos (`Main.java`, `TelaArtefatoExplicativo.java`, `EventoLogGerard.java`, `EstadoSemanticoCompartilhado.java`) — nenhuma linha do próprio `REFERENCE.md` é citada por número dentro dele mesmo. **[FATO NORMATIVO]** — nada a verificar quanto a esse ponto específico.

## 4. Os três arquivos TAREFA_PENDENTE

**`TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`**: consistente. Não intersecta diretamente com as decisões 5-10 (modalidade/Scaffolding/evento/versionamento/FEEDBACK_EXIBIDO); a decisão 1 (critério SISTEMA) reforça, sem contradizer, o conteúdo já registrado sobre `EstadoSemanticoCompartilhado`.

**`TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`**: consistente — criado na mesma rodada da decisão 1, referência a `REFERENCE.md §4.8` continua válida.

**`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`**: **[INFERÊNCIA] não contradiz nada, mas está incompleto em relação às decisões 5, 8 e à arquitetura de evento (6/7) — decididas depois dele.** Especificamente:
- O arquivo descreve o fluxo N=3 → tela de ajuda, mas não menciona que essa exibição corresponde hoje a um evento `FEEDBACK_EXIBIDO` especificado (decisão 8, `REFERENCE.md:300-326`) — o próprio cenário que o arquivo descreve é exatamente o que esse evento modela, mas a ligação não está feita.
- A seção "Vibração/som: execução mecânica da interface" não referencia a taxonomia de duas eixos de Scaffolding (tipo funcional × modalidade de entrega) decidida depois (`REFERENCE.md:246-259`) — vibração/som correspondem a "háptica"/"sonora" nessa taxonomia, mas o arquivo não usa esse vocabulário, criado depois dele.
- Não há contradição de fato — apenas o arquivo não reflete o vocabulário mais rico disponível agora. Uma nota de atualização (não uma correção) deixaria os dois documentos mais alinhados, mas isso é uma decisão sua, não uma correção que eu deva presumir.

## 5. Compilação e harnesses

| | Resultado |
|---|---|
| Compilação (421 arquivos) | exit `0`, mesmos 4 avisos pré-existentes |
| `TestePilotoPapelQuantitativo` | exit `0`, TODOS OS TESTES PASSARAM |
| `TestePilotoTransformacaoMedidas` | exit `0`, TODOS OS TESTES PASSARAM |

Idêntico ao resultado da Revisão 5, como esperado — mudanças desta sessão posteriores à Revisão 5 foram só em documentação.

## 6. git status

Branch atual: `migracao-nomenclatura-relacao-estrutural`. Último commit: `b43c61a` (anterior a toda esta sessão) — **nenhum commit novo foi criado**, nenhum push.

Nota factual, não solicitada mas relevante: o contexto de início desta conversa registrava a branch como `painel-atalho-categoria`; o estado atual do repositório mostra `migracao-nomenclatura-relacao-estrutural`. Não investiguei a causa (fora do escopo pedido) — só registro a divergência para você avaliar se é esperada.

Arquivos modificados (rastreados, não staged):
```
.claude/skills/gerard-semantic-event-logging/SKILL.md
.claude/skills/gerard-semantic-model/CHANGELOG.md
.claude/skills/gerard-semantic-model/MIGRATION_GUIDE.md
.claude/skills/gerard-semantic-model/REFERENCE.md
src/Main.java
src/gerard/agente/modelador/AgenteModelador.java
src/gerard/pesquisador/log/EventoLogGerard.java
src/gerard/pesquisador/log/LoggerInteracaoGerard.java
src/gerard/pesquisador/tentativa/TelaArtefatoExplicativo.java
```

Novos, rastreados (`A`):
```
TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md
src/gerard/agente/modelador/ContadorMineracao.java
src/gerard/agente/modelador/RepositorioRegrasInferidas.java
```

Novos, não rastreados (`??`) — 16 relatórios + 2 tarefas pendentes desta sessão, listados no `git status` completo acima.

---

## Resumo

- **Nenhuma contradição de conteúdo** entre seções novas e antigas, ou entre seções novas entre si.
- **Uma redundância normativa** (critério SISTEMA/INFERENCIA_COMPUTACIONAL declarado duas vezes com formulação distinta) — risco de drift futuro, não erro atual.
- **Quatro referências "parágrafo/seção anterior" desatualizadas** por inserções sequenciais — apontam para o bloco errado hoje.
- **Uma referência "abaixo" inconsistente em estilo** — deveria nomear o arquivo externo, como o resto do documento faz.
- **Referências a seções da Revisão 5 são órfãs** (documento nunca salvo no repositório) — não erradas, mas inverificáveis.
- **Nenhuma citação de linha interna a `REFERENCE.md`** — nada a checar nesse ponto.
- **Um `TAREFA_PENDENTE` incompleto**, não contraditório, em relação a decisões posteriores.
- **Compilação e harnesses idênticos** à Revisão 5.
- **Nada commitado, nada enviado** — confirmado, com uma divergência de nome de branch registrada para sua avaliação.

Nada foi corrigido. Aguardando decisão sobre o que, se algo, deve ser ajustado.
