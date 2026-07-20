# Relatório de alteração — padronização do cursor de mão

**Data:** 2026-07-16  
**Ciclo:** C114  
**Tipo de intervenção:** consistência visual e operacional.

## Síntese
Os cursores vetoriais personalizados de mão aberta e mão fechada foram removidos. Todas as interações que utilizam mãozinha passam a empregar o mesmo cursor nativo de mão já usado nos controles laterais do gráfico de barras.

## Alterações implementadas
- uso uniforme de `Cursor.HAND_CURSOR`;
- remoção do desenho vetorial de cursores personalizados;
- preservação dos contratos `FornecedorCursoresPickup` e da herança existente;
- manutenção dos efeitos de pickup: elevação, escala, sombra e prioridade visual;
- atualização do teste automatizado para exigir o mesmo cursor nas duas fases da interação.

## Resultado esperado
A mãozinha apresentada no texto, nos diagramas, nos quadradinhos e nos pontos de controle possui exatamente o mesmo padrão visual do cursor usado ao lado do gráfico de barras.
