# Estado verificado da identidade visual

Leia esta referência ao auditar a implementação da paleta ou os testes da
curadoria narrativa. Ela registra evidências datadas, não novos princípios de
identidade visual.

## Plataformas — 2026-07-20

O lado Windows/Swing foi conferido contra `gerard/ui/UITemaGerard.java`. O
lado mobile não pôde ser verificado porque este repositório não contém código
mobile; afirmações sobre o protótipo mobile são relato da usuária. A paleta
confirmada nesta data era neutra quente, não cinza (ver atualização abaixo), e
as cores de feedback vivem no mesmo arquivo da paleta neutra, embora
pertençam a outra responsabilidade.

## Migração para paleta fria do Design System do site — 2026-09-30

`UITemaGerard.java` foi atualizado no lugar (mesmas constantes, novos valores
hex) para adotar a paleta cinza-azulada do site institucional, descrita em
`prompt-claude-code-cores.md`. Decisão explícita: manter um único arquivo em
vez de criar `GerardPalette`/`GerardFeedbackColors` novos, para não deixar
dois sistemas de cor ativos em paralelo enquanto os 31 arquivos consumidores
não fossem migrados. `COR_SUCESSO` passou a compartilhar o mesmo azul de
`COR_ACAO` (novo token, ações/foco/botões primários) — ver `FeedbackMode` para
a opção B (verde) alternativa. Build (622 fontes) e suíte Java (151/151)
aprovados após a mudança. Pendente desta mesma tarefa, ainda não feito: fontes
IBM Plex embutidas, `GerardButton`, e migração de cor "quente" residual
específica de cada tela (Composição/Comparação/Transformação de medidas,
Construir situação-problema, Curadoria, Visão do pesquisador, Análise da
modelagem, Chat do Gérard).

## Curadoria narrativa rica — 2026-08-30

`TesteDialogoCuradoriaNarrativaRica` protege sete abas nominais, controle
textual de promoção humana, tooltip, nome e descrição acessíveis, fundo e
texto da paleta neutra quente. Também confirma que a marca histórica de
validação tabular não aparece como seleção automática. Essa evidência visual
não substitui a validação semântica do agregado e do conversor.
