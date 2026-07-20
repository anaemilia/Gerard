# Relatório de alteração — atualização dos personagens após salvar a curadoria

**Data:** 2026-07-15  
**Ciclo:** C70  
**Tipo de intervenção:** correção de sincronização entre curadoria e representação diagramática.

## Problema observado

Após alterar `personagem_1`, `personagem_2` e `personagem_3` na curadoria de uma situação de composição de medidas, os nomes não eram atualizados nos nós já renderizados do diagrama de Vergnaud.

## Causa

O fluxo de recarga atualizava o objeto da situação e os rótulos semânticos, mas não reaplicava os subtítulos de personagens nos elementos existentes. Assim, os campos eram persistidos, porém a representação mantinha os subtítulos anteriores.

## Alterações implementadas

- reconstrução da definição semântica após a recarga da situação curada;
- reaplicação dos nomes de personagens nos elementos já renderizados;
- preservação das posições, valores e demais estados da modelagem do usuário;
- manutenção da curadoria como fonte dos campos `personagem_1`, `personagem_2` e `personagem_3`.

## Resultado esperado

Em composição de medidas:

- `personagem_1` aparece sob **Parte 1**;
- `personagem_2` aparece sob **Parte 2**;
- `personagem_3` aparece sob **Todo**.

A atualização ocorre imediatamente após salvar e aplicar a curadoria, sem necessidade de sortear outra situação ou reiniciar o programa.
