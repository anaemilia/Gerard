# Estado verificado da extração dos handlers

Leia esta referência ao continuar a migração, auditar uma fase existente ou
verificar quais protocolos ainda permanecem concentrados em `Main.TelaGerard`.
As regras arquiteturais permanecem no `SKILL.md`; o procedimento incremental
fica aqui porque só é carregado quando uma fase de extração está em escopo.

## Procedimento incremental

Nenhuma etapa está autorizada a começar sem confirmação explícita da usuária:

1. escolha um protocolo como prova de conceito e extraia somente esse handler;
2. compare o comportamento anterior e posterior com um harness de regressão
   real, como `TesteMonkeySemiGuiado` ou equivalente dirigido por `Robot`;
3. depois da validação, repita para outro protocolo, um de cada vez;
4. trate cada extração como mudança arquitetural explícita, com relatório
   próprio conforme a convenção `RELATORIO_*.md`.

`mousePressed` foi medido com 245 linhas após a Fase 7.10 (350 após a 7.8) e continua representando risco alto
para uma mudança única. Extrações amplas não devem ser presumidas como melhoria
sem autorização e verificação de consistência; o roteiro permanece incremental.

## Fases e decisões verificadas

- **Fase 7.2 — validada.** Prova de conceito: extração de
  `HandlerInteracaoItemTextoArrastavel` de `Main.TelaGerard`
  (`RELATORIO_FASE_7_2_HANDLER_ITEM_TEXTO_2026-08-09.md`). Validada em
  2026-08-11 com `TesteMonkeyGuiadoPorCasosReais`: Robot real, 14 episódios
  reais do catálogo de doutorado, 45 passos, zero divergências e nenhuma
  exceção não tratada. Ver
  `RELATORIO_VALIDACAO_ROBOT_HANDLER_ITEM_TEXTO_2026-08-11.md`.
- **P1.1 — gesto separado da ação no item textual.** O handler encerra a
  trajetória física em `ResumoGestoArraste`, sem conhecer alvo semântico.
  `ItemTextoArrastavel` produz o registro factual, a tela fornece somente a
  classificação derivada da geometria real e uma porta separada o persiste.
  O rastreador granular legado deixou de receber esse protocolo.
- **Fase 7.3 — validada.** O protocolo de `ElementoTextoMovel` foi extraído
  para `HandlerInteracaoElementoTextoMovel` e validado pelo mesmo harness
  Robot real da Fase 7.2. Ver
  `RELATORIO_FASE_7_3_HANDLER_ELEMENTO_TEXTO_MOVEL_2026-08-11.md`.
- **Fase 7.4 — validada.** Elementos e conectores do diagrama de Vergnaud
  foram extraídos para `HandlerInteracaoElementosDiagramaVergnaud`, conforme
  decisão explícita da usuária em 2026-08-16. Ver
  `RELATORIO_FASE_7_4_HANDLER_ELEMENTOS_DIAGRAMA_VERGNAUD_2026-08-16.md`.
  Em decisão posterior, o reposicionamento livre de `ElementoVergnaud` deixou
  de constituir ação permitida.
- **P4.2 — protocolo do conector tornado portátil.** O protocolo ainda válido
  usa `HandlerInteracaoArrasteIncremental`, dependente somente de
  `AlvoMovelIncremental` e `LimitesMovimento`. A tradução desktop entre
  `Rectangle` e o contrato neutro pertence a
  `AdaptadorMovimentoConectorVergnaud`. O antigo handler misto foi removido.
- **Fase 7.5 — implementada e verificada isoladamente.** O protocolo dos
  quadradinhos do diagrama de Venn foi transferido para
  `HandlerInteracaoQuadradinhoVenn`. A tela faz o hit-testing e encaminha
  início, movimento, conclusão e cancelamento. `TesteAffordancePickupUI` usa
  o acesso encapsulado `obterQuadradinhoAtivo()`.
- **Fase 7.6 — retirada em 2026-09-01, não mantida.** O protocolo do eixo
  flutuante único havia sido validado em 2026-08-28, mas o mecanismo desktop
  envolvido revelou-se código morto. A regra de 2026-08-18, segundo a qual
  todo número relativo ou transformação carrega uma lupa, é aplicada pelos
  seis renderizadores canônicos; o guard tornou o mecanismo inalcançável.
  Isso foi confirmado por leitura estática e por dois runs do harness Robot,
  cobrindo cinco das seis categorias, com zero ocorrências. O protocolo, as
  classes de instância única e o teste foram removidos. `Main.java`, o
  verificador completo e a bateria de testes foram reaprovados. Ver
  `LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md`. Os painéis individuais
  das categorias de Relações não foram afetados.
- **Fase 7.7 — validada.** O protocolo dos painéis individuais de eixo das
  categorias de Relações foi transferido para
  `HandlerInteracaoPaineisEixosRelacoes`, dependente somente de
  `AlvoInteracaoPaineisEixosRelacoes`. A tradução de `Rectangle`, dimensões
  reais, hit-testing e estado visual pertence a
  `AdaptadorInteracaoPaineisEixosRelacoes` e
  `FonteGeometriaInteracaoPaineisEixosRelacoes`. Em 2026-08-30 passaram o
  build de 533 fontes, o verificador completo, 96 testes executáveis e o Robot
  de 60 segundos, com 58 iterações e zero erros.
- **Fase 7.8 — validada.** A sequência dos dois
  seletores de operação das Relações saiu de `TelaGerard.mousePressed` para
  `HandlerInteracaoSeletoresOperacaoRelacoes`. O segundo seletor continua
  inacessível até o primeiro estar correto. `CriterioOperacaoModelagem`, no
  domínio, produz `ResultadoEscolhaOperacaoModelagem`; o handler não conhece
  Swing, feedback ou
  persistência; e `CasoDeUsoSelecaoOperacoesRelacoes` sequencia portas estreitas
  de feedback, persistência e ciclo da tela, sem conhecer Swing, som ou logger.
  O resultado semântico decide somente o feedback abstrato (`NENHUM` ou
  `SOM_ERRO`); adaptadores concretos e independentes materializam esse feedback
  e a persistência. O resultado usa `OperacaoAditiva` e não depende da opção
  de curadoria. A realização textual compartilhada por desktop e web foi
  separada da avaliação, que não conhece mais i18n nem montagem de frases. O
  build de 598 fontes, o teste dirigido de prioridade/ordem
  e as verificações
  estruturais passaram em 2026-09-28. Em 2026-09-28 recebeu validação Robot própria
  (`tests/graphical/TesteRobotSeletoresOperacaoRelacoes`): 36 verificações,
  zero falhas, nas três categorias de Relações — prioridade do primeiro
  seletor, bloqueio do segundo antes do primeiro correto, um registro C/E por
  escolha consumida, zero exceções e `MouseListener` único. Ver
  `RELATORIO_VALIDACAO_ROBOT_FASE_7_8_SELETORES_2026-09-28.md`; execução em
  Linux/Xvfb, repetição no Windows recomendada para a parte visual.

- **Fase 7.10 — validada.** Autorizada pela usuária em 2026-09-28. O clique
  nos controles +/− do material concreto saiu de `mousePressed` para
  `HandlerInteracaoControlesUnidades` (pacote `gerard.interacao.unidades`),
  dependente só de `AlvoControlesUnidades<C>`: remover tem prioridade;
  liberação pela modelagem antes do limite; limite antes da aplicação. O
  adaptador desktop traduz hit-test, representação editável e valor
  assinado; `apresentarResultadoControleUnidades` só materializa o desfecho.
  Ratchet 350 → 245. Robot A/B (build anterior × posterior, mesmas situações)
  com traços idênticos em quatro categorias. O ramo "bloqueado pela
  modelagem" parece inalcançável pela interface (material concreto só
  aparece após a escalada); preservado até decisão. Ver
  `RELATORIO_FASE_7_10_CONTROLES_UNIDADES_2026-09-28.md`.
- **Fase 7.11 — validada.** Autorizada pela
  usuária em 2026-09-29. A sequência de seleção de um marcador matemático do
  enunciado saiu de `mousePressed` para
  `HandlerInteracaoSelecaoMarcadorTexto`: ausência não consome o clique,
  ocorrência já posicionada não cria outro proxy e uma ocorrência disponível
  abre a sessão por cópia representacional, sem remover a origem textual. O
  hit-test e a materialização de tooltip, logs, foco, cursor e efeitos visuais
  permanecem na tela. Ratchet de `mousePressed`: 245 → 197. Build, teste
  dirigido e verificações estruturais aprovados. O Robot próprio passou no
  Windows com seleção real, preservação semântica da origem textual e bloqueio
  de uma segunda ocorrência já posicionada.

- **Fase 7.12 — validada.** Autorizada pela usuária em 2026-09-29, com diff
  prévio. A sequência do pressionamento da barra de Comparação de Medidas
  saiu de `mousePressed` para `HandlerInteracaoControleComparacao.pressionar`:
  fora do ponto de controle e da escala o clique não é consumido; sobre eles,
  sem liberação pela modelagem, o gesto é bloqueado sem iniciar; liberado, o
  gesto inicia. O hit-test chega pela porta `AlvoControleComparacao`
  (geometria concreta na tela); a liberação chega como booleano, como nos
  painéis de Relações; aviso, registro granular, fantasma, cursor, valor e
  arraste elástico são materializados por
  `apresentarPressionamentoControleComparacao`. Ratchet de `mousePressed`:
  197 → 188. Build de 617 fontes, teste dirigido, suíte 151/151 e Robot A/B
  serial (Linux/Xvfb) com traços idênticos em duas situações de Comparação.
  Ver `RELATORIO_FASE_7_12_PRESSIONAMENTO_CONTROLE_COMPARACAO_2026-09-29.md`.

- **Fase 7.13 — validada (apresentação, não mecânica).** Autorizada pela
  usuária em 2026-09-29, com diff prévio. A inspeção mostrou que a mecânica da
  lupa de Relações já estava localizada (`PaineisEixosRelacoes` para o
  hit-test, `ControleVisibilidadeEixoPapel` para a transição); nenhum handler
  foi criado, para não dividir esse proprietário. Só a apresentação (preparar
  painel, log `LUPA_EIXO_RELACAO`, foco, repaint) saiu do corpo de
  `mousePressed` para `apresentarRevelacaoEixoRelacaoPelaLupa`. Ratchet
  188 → 174, registrado como organização do roteamento. Robot A/B serial
  (Linux/Xvfb) com traços idênticos em duas situações de Relações. Ver
  `RELATORIO_FASE_7_13_APRESENTACAO_LUPA_RELACOES_2026-09-29.md`.

## Risco ainda aberto

`mousePressed`, com 174 linhas na verificação registrada (Fase 7.13), ainda concentra
risco alto para uma única mudança. Isso não autoriza uma extração ampla:
qualquer nova fase exige autorização explícita, escolha de um protocolo e
validação proporcional ao comportamento afetado.
