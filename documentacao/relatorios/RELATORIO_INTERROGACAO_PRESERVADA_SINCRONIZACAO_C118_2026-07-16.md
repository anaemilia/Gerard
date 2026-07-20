# C118 — Preservação da interrogação durante a sincronização textual

## Decisão teórica

A interrogação não é apenas um valor ausente. No Gérard, ela marca o item desconhecido da situação-problema e pode ocupar qualquer papel semântico previsto nas estruturas aditivas. Por esse motivo, o resultado obtido nos diagramas não deve substituir automaticamente o `?` exibido no enunciado.

## Correção

A sincronização introduzida na C117 foi restringida: valores conhecidos do enunciado continuam acompanhando o estado dos diagramas, mas qualquer elemento cuja origem semântica seja `?` permanece visualmente inalterado.

A regra abrange:

- a interrogação ainda integrada à frase;
- a interrogação deslocada pelo usuário, desde que permaneça na área do enunciado;
- qualquer papel semântico ocupado pela incógnita, sem depender da categoria aditiva.

O vínculo entre a incógnita e seu papel semântico permanece ativo. Assim, logs, validações, modelagem e atualização das demais representações continuam reconhecendo qual elemento é desconhecido, mas o texto não revela automaticamente a resposta.

## Arquitetura

O contrato `ElementoSemanticoTexto` passou a declarar `representaIncognitaOriginal()`. As implementações `ElementoTextoMovel` e `ItemTextoArrastavel` identificam a incógnita de acordo com seu valor original. `SincronizadorElementosSemanticosTextoAbstrato` usa o contrato polimorficamente para excluir a interrogação da substituição de valores.

## Comportamento esperado

No exemplo de comparação:

- enunciado inicial: `6`, `8`, `?`;
- estado atualizado dos diagramas: `4`, `8`, `12`;
- elementos visuais do enunciado: `4`, `8`, `?`.

## Regressão

Devem permanecer intactos:

- sincronização dos valores conhecidos;
- pontuação e fluxo textual;
- marcadores deslocados no enunciado;
- diagramas de Vergnaud e representações complementares;
- limites semânticos e operações com quadradinhos;
- pickup, cursores, sombra, elevação, primeiro plano, origem fantasma e seguimento elástico;
- logs e identificação do papel semântico da incógnita.

## Verificações executadas

- compilação `ant clean jar` aprovada;
- teste unitário da sincronização textual aprovado para `4`, `8`, `?`;
- teste integrado da `TelaGerard` aprovado com a interrogação na frase e deslocada na área do enunciado;
- regressão estrutural geral aprovada;
- 210 versões e 72 grupos conceituais preservados;
- incremento unitário da comparação aprovado;
- leitura dos valores curados no gráfico de barras aprovada;
- contratos de criação, remoção e limite de unidades aprovados;
- renderização 2,5D, pickup, cursores e arraste físico aprovados;
- feedback multissensorial, ajuda contextual, inicialização sem categoria e disponibilidade da análise aprovados.
