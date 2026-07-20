# Relatório de alteração — rótulos de personagens no diagrama de Vergnaud

**Data:** 2026-07-15  
**Ciclo:** C56  
**Tipo de intervenção:** refinamento representacional com intervenção explícita do pesquisador.

## Síntese
Foram adicionados subtítulos com os nomes dos personagens/objetos diretamente nos nós do diagrama de Vergnaud, de forma análoga ao que já ocorre no gráfico de barras.

## Alterações implementadas
- inclusão de um subtítulo abaixo do rótulo semântico de cada nó;
- leitura dos nomes a partir dos campos `personagem_1`, `personagem_2` e `personagem_3` da curadoria;
- tratamento específico para comparação de medidas, exibindo os nomes em **Referido** e **Referendo**;
- manutenção do valor numérico no interior das figuras e do rótulo semântico logo abaixo.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
