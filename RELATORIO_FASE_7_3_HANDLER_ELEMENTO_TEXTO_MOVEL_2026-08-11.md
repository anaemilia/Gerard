# Fase 7.3 — handler do elemento textual móvel

Data: 2026-08-11

## Objetivo

Continuar o roteiro incremental de `gerard-handlers-de-interacao`: depois da Fase 7.2
(`HandlerInteracaoItemTextoArrastavel`, item já solto no diagrama), extrair de
`Main.TelaGerard` o estado mecânico do gesto de arraste de `ElementoTextoMovel` — o
elemento textual (número ou interrogação) que ainda vive dentro do enunciado, antes de
ser convertido em item do diagrama.

## Fronteira de conhecimento adotada

`HandlerInteracaoElementoTextoMovel` conhece somente:

- o elemento ativo;
- o deslocamento entre o ponteiro e o elemento no pickup;
- movimento livre (sem restrição) e movimento limitado pela geometria recebida (não
  calcula a geometria, só aplica os limites que `TelaGerard` fornece);
- conclusão, cancelamento e identificação de foco local do elemento.

Continuam em `Main.TelaGerard`, junto dos subsistemas responsáveis:

- decisão de quando um elemento textual deve ser convertido em item do diagrama
  (`converterElementoTextoEmItemDiagrama`);
- cálculo da geometria de limites (`GeometriaAreaEnunciado`);
- escolha do alvo semântico, scaffolding, som, sincronização das representações,
  confirmação da incógnita, conclusão da modelagem, auditoria e logs;
- renderização (`desenharElementoTextoMovel`) e despacho dos eventos Swing.

Mesma divisão de responsabilidade da Fase 7.2: o handler não decide semântica nem
pedagogia, só mecânica do gesto.

## Alterações

- criado `src/gerard/interacao/arraste/HandlerInteracaoElementoTextoMovel.java`;
- `Main.TelaGerard` passou a delegar pickup, movimento livre, movimento limitado,
  conclusão, cancelamento e identificação de foco ao handler, em vez de manter esse
  estado como campos soltos na classe da tela;
- criado teste unitário do contrato do handler
  (`tests/java/TesteHandlerInteracaoElementoTextoMovel.java`), cobrindo pickup,
  movimento livre e movimento limitado pela geometria real de `GeometriaAreaEnunciado`;
- acrescentada proteção estrutural ao verificador de regressão
  (`scripts/verificar_regressao_gerard.py`): confirma a existência da classe e do fio de
  chamadas (`iniciar`/`moverLivrePara`/`moverDentroDosLimites`/`concluir`) em `Main.java`.

## Verificação

Reaproveitado o mesmo ciclo de verificação da Fase 7.2, já que as duas extrações
convivem no mesmo build e são exercitadas pelo mesmo fluxo real de arraste (um
elemento textual do enunciado só vira item do diagrama depois de passar pelo pickup e
movimento tratados por este handler):

- `ant clean jar`: **BUILD SUCCESSFUL**.
- verificador de regressão arquitetural: aprovado integralmente, incluindo a nova
  proteção estrutural desta fase.
- harness Robot real (`TesteMonkeyGuiadoPorCasosReais`), rodado em 2026-08-11 — ver
  `RELATORIO_VALIDACAO_ROBOT_HANDLER_ITEM_TEXTO_2026-08-11.md`. Esse relatório documenta
  a validação em detalhe e sua conclusão cita explicitamente
  `HandlerInteracaoElementoTextoMovel` como parte do que passou sem regressão. Resumo do
  resultado (log `%USERPROFILE%\Gerard\logs\monkey_casos_reais_20260811_133757.log`):
  14 episódios reais do catálogo, 45 passos, 0 divergências, sem exceção não tratada,
  `MouseListener=1 MouseMotionListener=1` (nenhum listener duplicado ou perdido pelas
  duas extrações).
- Todos os 14 episódios exercitam o pickup do elemento textual a partir do enunciado
  (`origem=texto_enunciado`) antes da conversão em item do diagrama — é exatamente o
  trecho do fluxo coberto por este handler. Nenhuma divergência de destino, soltura ou
  idempotência foi observada nos 45 passos.

## Conclusão

A extração de `HandlerInteracaoElementoTextoMovel` está concluída e validada pelo mesmo
harness Robot real que validou a Fase 7.2, já que ambas as extrações são exercitadas
pelos mesmos 14 episódios reais do catálogo. Não há necessidade de uma nova rodada do
harness dedicada só a esta fase — o log de 2026-08-11 já cobre os dois handlers
simultaneamente ativos no build.

Próximo passo do roteiro (`gerard-handlers-de-interacao`, passo 3): escolher, com
autorização explícita, qual dos protocolos restantes extrair em seguida —
quadradinhos do diagrama Venn, eixo de inteiros ou elementos de Vergnaud.
