# Relatório: botões de sorteio colados nos grupos de categoria

Data: 2026-08-07

## Contexto

Continuação direta da divisão do botão único de sorteio em dois
(`RELATORIO_DIVISAO_BOTAO_SORTEIO_2026-08-07.md`, commit `ce39ae2`), que
tinha colocado os dois botões juntos no cabeçalho, diferenciados só por
um pequeno distintivo de letra (M/R) no ícone. A usuária perguntou o que
eu achava de colocar cada botão ao lado do seu próprio grupo de ícones em
vez dos dois juntos no topo — concordei (proximidade comunica melhor a
relação do que a letrinha) e ela confirmou.

## O que mudou

### `Main.java`

- Os dois botões (`botaoFerramentaSortearMedidas`/`Relacoes`) saíram de
  `criarBotoesCabecalhoEmbutidos` (cabeçalho, ao lado de "Comparar
  categorias") e passaram a ser criados dentro de
  `criarPainelAtalhoCategoria`, um logo depois do 3º ícone de cada grupo.
- `reposicionarPainelAtalhoCategoria` foi reescrito para incluir os dois
  botões novos no cálculo de centralização horizontal (`larguraTotal`) e
  posicioná-los **fora** da caixa delimitadora de cada grupo
  (`areaGrupoMedidas`/`areaGrupoRelacoes`, que continua envolvendo só os
  3 ícones de resposta) — decisão deliberada para não parecer uma 4ª
  opção de resposta do quiz de adivinhação. Duas constantes novas,
  `LARGURA_BOTAO_SORTEIO` (34, mesmo tamanho dos botões do cabeçalho) e
  `GAP_BOTAO_SORTEIO` (16), documentam o espaçamento.
- `criarIconeFerramentaSortear` voltou a ser o ícone de dado simples, sem
  distintivo de letra — com a posição já diferenciando os dois botões
  (cada um colado no seu grupo, com o rótulo "Medidas"/"Relações" logo
  acima), o distintivo ficou redundante.
- `caixaIndicadorAgenteMonitor` (LEDs dos 3 agentes) voltou para x=100 no
  cabeçalho, já que os dois botões que ocupavam aquele espaço saíram de
  lá.
- Tooltips (`ui.tooltip.random.measures`/`relations`, sem mudança nas
  chaves i18n do commit anterior) agora são atualizados dentro de
  `atualizarTextosFixosDaInterface`, junto com os outros botões de
  tooltip fixo — necessário porque esses dois botões são criados só uma
  vez (diferente dos 6 ícones de resposta, cujo tip é recalculado a cada
  passar do mouse).

## Verificação

- Compilação completa: **0 erros**.
- Boot real sob Xvfb + screenshot: os dois dados aparecem pequenos, cada
  um logo depois do seu grupo de 3 ícones, fora da moldura — visualmente
  claro que são uma ação relacionada mas distinta das respostas do quiz.
- Teste pontual via Robot (feito e apagado depois de usar — não faz parte
  do projeto): confirmou por coordenada que `sortearMedidas` fica
  exatamente 16px depois do fim de `botaoAtalhoComparacao`, e
  `sortearRelacoes` 16px depois de `botaoAtalhoComposicaoRelacoes`, ambos
  centralizados na mesma linha vertical dos ícones grandes. 10 cliques
  (5 por botão): **0 exceções**, cada botão sorteando só dentro do seu
  próprio grupo, igual à verificação anterior.

## Escopo

Só `Main.java` — nenhuma mudança nas chaves i18n (já existiam do commit
anterior) nem em qualquer lógica de domínio/piloto.
