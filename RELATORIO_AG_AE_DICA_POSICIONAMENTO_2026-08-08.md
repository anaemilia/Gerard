# Relatório — AG_AE, dica de posicionamento sob demanda (2026-08-08)

Item 7 de `LEVANTAMENTO_PENDENCIAS_2026-08-07.md` ("Automatização de
passos", quarto tipo de scaffolding da skill `gerard-scaffolding-interacao`
— até aqui só um rótulo de legenda não usado, nenhum código implementava).

## Desenho pedagógico, definido pela usuária antes de qualquer código

A skill exige autorização explícita do pesquisador para a ordem de
qualquer automação — não é uma decisão de implementação. A usuária
descreveu o comportamento (mostrar ao participante onde cada frase do
enunciado pertence no diagrama, usando rótulos, para reduzir demanda
cognitiva na etapa de interpretação linguística) e confirmou, em duas
rodadas de perguntas:

- **Gatilho**: sob demanda, via botão "Ver dica" — não automático, não
  ligado à escalada de tentativas rejeitadas (diferente de AG_EMCME).
- **Escopo por clique**: um papel por vez, progressivo — nunca todos de
  uma vez.
- **Correção de arquitetura, feita pela usuária durante a revisão do
  plano**: a primeira versão do plano descrevia "cada exibição vira um
  evento, sem limite de uso" — a usuária apontou que isso ignorava a
  cardinalidade ação:evento já adotada (`REFERENCE.md §4.8`, Alternativa
  B, 1:N: uma ação pode gerar vários eventos correlacionados pelo mesmo
  `action_id`, até fechar). O desenho final trata "pedir a dica do mesmo
  papel enquanto ele continua pendente" como uma única ação — cada
  exibição repetida é um evento correlacionado, não um evento avulso; a
  ação fecha quando o papel é resolvido.

Escopo deliberadamente restrito nesta primeira implementação: só papéis
dado (nunca a incógnita — ver `gerard-consistencia-estado`) e só a etapa
de posicionar frases do enunciado no diagrama (não a etapa numérica dos
funis, já coberta por outro mecanismo).

## O que foi implementado

Tudo em `Main.java`/`TelaGerard`, sem pacote novo — a lógica é pequena e
se apoia inteiramente em mecanismos já existentes (nenhum reimplementado):

- **`papelPosicionamentoResolvido(papel)`**: reaproveita
  `scaffoldingQuestionamento.papeisCompativeis` (mesmo critério de
  `AvaliadorConclusaoModelagem`), aplicado a um papel isolado.
- **`obterProximoPapelNaoResolvidoParaDica()`**: primeiro papel-dado (nunca
  a incógnita) ainda não resolvido, em ordem canônica de
  `elementosVergnaud`, que tenha uma frase disponível para mostrar.
- **`obterFraseParaDicaPosicionamento(papel)`**: busca em
  `elementosTexto` (os pedaços do enunciado ainda não arrastados) o
  elemento com vínculo semântico para aquele papel — a mesma frase que o
  participante vê no texto; cai para `itensArrastaveis` se a frase já foi
  arrastada (para o lugar certo ou errado).
- **Correlação ação:evento (1:N)**: `acaoDicaPosicionamentoPorPapel`
  (papel → action_id aberto) + `registrarFeedbackExibido(...)` ganhou um
  4º parâmetro opcional `actionId` (overload de 3 args preservado,
  delega com `null` — os 5 pontos de disparo pré-existentes, AG_EMS/EME/
  EMLQ/EMCME, continuam exatamente como estavam).
- **Renderização**: reaproveita a mesma máquina de anotação persistente já
  usada por `itemQuestionadoPersistente` e
  `agrupamentoLimiteQuantidadeQuestionado` dentro de
  `desenharAnotacaoMouseOver` — uma terceira variante
  (`usarDicaPosicionamentoPersistente`), ancorada no `ElementoVergnaud`
  alvo. Auto-fecha (e encerra a ação correlacionada) no próximo repaint
  se o papel já tiver sido resolvido nesse meio-tempo — sem precisar de
  um gancho em cada ponto onde um item pode ser solto.
- **Botão "Ver dica"**: novo `JButton` (com texto, não só ícone — ainda
  não é uma affordance reconhecível por forma), ao lado dos botões de
  ajuda contextual existentes; visível só quando há papel pendente com
  frase disponível.
- **i18n**: `ui.hint.stepPlacement` (mensagem, com `{0}` para a frase) e
  `ui.hint.stepPlacement.button` (rótulo do botão), pt/en/es/fr.
- **Reset**: `limparEstadoDicaPosicionamento()` chamado nos 3 pontos onde
  `elementosVergnaud.clear()` já ocorria — nenhuma dica ou ação sobrevive
  à troca de situação-problema.

## Verificação

1. **Compilação completa**: 445 arquivos, 0 erros.
2. **Harnesses existentes** (`TestePilotoTentativasRejeitadas`,
   `TesteComparativoEstadoSemanticoCompartilhado` — 40 cenários,
   `TestePilotoComposicaoMedidas`) — sem regressão.
3. **Teste real sob Xvfb** (`TesteTemporarioAGAE.java`, deletado após a
   verificação, nunca commitado), contra a aplicação real, via reflection
   nos mesmos métodos privados que o botão dispara (nunca reimplementando
   a lógica) — 19 checks, todos passaram:
   - Categoria selecionada de verdade (menu real, `doClick`); diagrama
     povoado.
   - 1º clique em "Ver dica": estado ativado, âncora é um
     `ElementoVergnaud` real do diagrama atual, ação aberta com
     `action_id`.
   - `desenharDiagramaVenn` + `desenharAnotacaoMouseOver` reais (
     `Graphics2D` de `BufferedImage`) não lançam exceção com a dica ativa.
   - **2º clique no mesmo papel (ainda pendente)**: reaproveita o MESMO
     `action_id` — a correção pedida pela usuária, confirmada no
     mecanismo real, não só na descrição.
   - Resolução real: `converterElementoTextoEmItemDiagrama` (o mesmo
     método que um solto de mouse real dispara) arrasta a frase certa do
     enunciado para o alvo — papel passa a "resolvido".
   - Dica se auto-fecha e a ação se encerra (`action_id` removido) no
     próximo desenho, sem gancho manual no ponto de soltura.
   - Próximo papel não resolvido avança para o seguinte; nunca aponta
     para a incógnita, nem na 1ª nem na 2ª dica.
4. **TSV real de produção** (`$HOME/Gerard/logs/gerard_interacao_*.tsv`):
   duas linhas `FEEDBACK_EXIBIDO` com `estilo=AG_AE`, geradas pelos dois
   cliques no mesmo papel, com **exatamente o mesmo `action_id`**:

   ```
   ...FEEDBACK_EXIBIDO	estilo=AG_AE; modalidade=VISUAL; criterio=renderizado (modalidade passiva); dica de posicionamento sob demanda; papel=papel.parte1; action_id=c9335e2d-953d-4177-8b05-ee8b6b0fa650...
   ...FEEDBACK_EXIBIDO	estilo=AG_AE; modalidade=VISUAL; criterio=renderizado (modalidade passiva); dica de posicionamento sob demanda; papel=papel.parte1; action_id=c9335e2d-953d-4177-8b05-ee8b6b0fa650...
   ```

## Adendo (2026-08-08) — auditoria contra as skills e correção de localidade

A pedido da usuária, depois da entrega acima: conferência explícita da
implementação contra `gerard-domain-model-first` e
`gerard-knowledge-locality-principle` (releitura dos dois `SKILL.md`, não
por memória de sessão).

**Achado**: a primeira versão colocava regra semântica dentro de
`Main.java`/`TelaGerard` — "papel resolvido ou não" duplicava
`papeisCompativeis` inline, e a correlação de `action_id` vivia num
`Map<String,String>` solto na tela. Ambos violam "Nenhuma regra semântica
pode existir apenas na interface" (`gerard-domain-model-first`) e
"localidade pedagógica: decidir nível de scaffolding... pertence a
skills ou serviços especializados, não a controllers"
(`gerard-knowledge-locality-principle`).

**Correção**:

- `AvaliadorConclusaoModelagem` (já existente, `gerard.campoaditivo.
  conclusao` — antes não instanciado em nenhum lugar) ganhou dois métodos
  públicos: `papelResolvido(papel, posicionamentos)` e
  `obterProximoPapelNaoResolvido(papeisEsperados, posicionamentos,
  papelExcluido)`. Reaproveitam os mesmos `compativeis`/`papelValido`
  privados que já decidem a conclusão da modelagem inteira — zero
  duplicação da regra de compatibilidade de papéis.
- Novo `gerard.Scaffolding.automatizacao.ScaffoldingAutomatizacaoPassos`
  — única responsabilidade: correlação ação:evento (`obterOuIniciarAcao`/
  `encerrarAcao`/`temAcaoAberta`/`limpar`), mesmo padrão de nome e pacote
  das demais classes `Scaffolding*` do projeto (`ScaffoldingQuestionamento`,
  `ScaffoldingAjudaContextual` etc.).
- `Main.java` ficou só com orquestração e leitura de estado vivo da UI
  (`obterFraseParaDicaPosicionamento`, que lê `elementosTexto`/
  `itensArrastaveis` — permanece em `Main.java` de propósito: é
  responsabilidade de **representação**, não de domínio, segundo a
  própria skill) — `papelPosicionamentoResolvido` e o `Map` de ações
  foram removidos inteiramente.

**Verificação da refatoração**: recompilação completa (446 arquivos, 0
erros), os 3 harnesses existentes sem regressão, e um segundo teste real
sob Xvfb (`TesteTemporarioAGAERefatorado.java`, deletado após a
verificação, nunca commitado) repetindo os mesmos passos do teste
anterior contra a aplicação real — 10 checks, todos passaram,
confirmando que o comportamento observável (inclusive a correlação de
`action_id` entre dois cliques) não mudou.

**Demais skills conferidas nesta rodada** (releitura completa, não só
grep pontual): `gerard-scaffolding-interacao` (seção 4 estava
desatualizada — corrigida, ver commit `5e9ff6d`), `gerard-consistencia-
estado` (nenhuma violação — AG_AE não escreve em
`EstadoSemanticoCompartilhado`), `gerard-log-acao-instrumental`
(`registrarAcaoGranular("SELECIONAR", ...)` usa um dos seis valores de
Shneiderman e segue exatamente o mesmo formato de chamada já usado em
`criarOpcaoAjudaContextual` — nenhum campo inventado). `gerard-semantic-
event-logging` já havia sido conferida na entrega original (campo
`action_id` é explicitamente previsto no "contexto mínimo do evento").

## Observação de escopo

Não implementado aqui (deliberadamente, fora do que foi autorizado até
agora): AG_AC ("automatizar a contagem"), dica para a incógnita, e
qualquer automação da etapa numérica dos funis. `LEVANTAMENTO_PENDENCIAS_
2026-08-07.md` fica com o item 7 concluído no escopo definido pela
usuária (frases-dado, sob demanda, progressivo); qualquer extensão futura
segue o mesmo padrão — desenho pedagógico aprovado antes do código.
