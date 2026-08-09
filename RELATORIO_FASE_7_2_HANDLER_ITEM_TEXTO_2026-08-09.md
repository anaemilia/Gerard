# Fase 7.2 — handler do item textual arrastável

Data: 2026-08-09

## Objetivo

Retirar de `Main.TelaGerard` o estado mecânico local do gesto de arraste de
`ItemTextoArrastavel`, sem alterar o comportamento da aplicação e sem criar um
coordenador com conhecimento global.

## Fronteira de conhecimento adotada

`HandlerInteracaoItemTextoArrastavel` conhece somente:

- o item ativo;
- o deslocamento entre o ponteiro e o item no pickup;
- a posição de origem usada para distinguir movimento real de clique parado;
- início, movimento, cancelamento, conclusão e foco local do item.

Continuam em `Main.TelaGerard`, próximos dos subsistemas responsáveis:

- escolha do alvo semântico e avaliação do posicionamento;
- centralização magnética e feedback de proximidade;
- som, tremor, tip persistente e demais scaffolds;
- sincronização das representações e do gráfico dos inteiros;
- confirmação da incógnita, conclusão da modelagem, auditoria e logs;
- renderização e despacho dos eventos Swing.

Essa divisão segue as skills de modelo de domínio, objetos orientados a
conhecimento, handlers de interação, consistência de estado, scaffolding,
posicionamento relativo e, principalmente, localidade do conhecimento. O
handler não é um coordenador de representações e não conhece regras
pedagógicas ou semânticas.

## Alterações

- criado `src/gerard/interacao/arraste/HandlerInteracaoItemTextoArrastavel.java`;
- removidos de `Main.TelaGerard` o item selecionado e as coordenadas duplicadas
  do pickup;
- encaminhados ao handler pickup, deslocamento, soltura, cancelamento e hover;
- adaptados os testes de interface e o harness gráfico para observar o handler;
- criado teste unitário do contrato do handler;
- acrescentada proteção estrutural ao verificador de regressão.

## Verificação

- linha de base Windows: build aprovado, 450 fontes compiladas, 72 testes
  compilados, 68 testes determinísticos aprovados e 4 gráficos reconhecidos;
- verificador de regressão arquitetural: aprovado integralmente;
- harness `TesteMonkeyGuiadoPorCasosReais`: compilado com sucesso;
- `git diff --check`: sem erros.

O Robot gráfico completo não foi executado automaticamente porque assume o
controle exclusivo do mouse e do teclado e manipula temporariamente arquivos
reais do perfil. Os testes determinísticos de interface já exercitaram os
eventos reais de pickup, arraste e soltura afetados por esta fase.
