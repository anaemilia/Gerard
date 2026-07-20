# Relatório de alteração — diferenciação dos estados do cursor de pickup

**Data:** 2026-07-16  
**Ciclo:** C115  
**Tipo de intervenção:** refinamento de affordance e consistência visual.

## Síntese
O cursor do pickup passou a diferenciar disponibilidade e deslocamento. A mão nativa utilizada ao lado do gráfico de barras permanece como sinal de que um elemento pode ser pego. Assim que o arraste é iniciado, o cursor muda para o padrão nativo de movimentação.

## Comportamento
- mouseover sobre elemento arrastável: `Cursor.HAND_CURSOR`;
- pickup e arraste ativo: `Cursor.MOVE_CURSOR`;
- soltura sobre elemento arrastável: retorno à mão nativa;
- soltura fora de elemento arrastável: retorno ao cursor padrão.

## Preservação funcional
A alteração não modifica coordenadas, colisões, escala de pickup, sombra, prioridade visual, logs, limites semânticos ou sincronização entre representações.

## Arquitetura
A seleção dos cursores continua encapsulada pelo contrato `FornecedorCursoresPickup`, com implementação concreta em `FornecedorCursoresPickupSwing` e fallback comum na classe abstrata.
