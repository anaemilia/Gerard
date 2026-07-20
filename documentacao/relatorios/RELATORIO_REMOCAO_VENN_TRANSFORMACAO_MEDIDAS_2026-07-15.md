# Relatório de alteração — remoção do diagrama de Venn em transformação de medidas

**Data:** 2026-07-15  
**Ciclo:** C49  
**Tipo de intervenção:** refinamento representacional com intervenção explícita do pesquisador.

## Síntese
Na categoria **Transformação de medidas**, o diagrama complementar de Venn foi retirado. A tela passa a apresentar somente o diagrama formal de Vergnaud, que ocupa toda a largura disponível da área de diagramas.

## Alterações implementadas
- ocultação do diagrama de Venn quando a categoria selecionada é `TRANSFORMACAO_MEDIDAS`;
- remoção do divisor vertical entre os diagramas nessa categoria;
- expansão da área do diagrama de Vergnaud para toda a largura disponível;
- limpeza das estruturas internas do diagrama complementar enquanto ele permanece oculto;
- preservação das representações complementares nas demais categorias.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**;
- preservação do gráfico de barras em comparação de medidas: **mantida**;
- preservação dos rótulos dos nós do diagrama de Vergnaud: **mantida**.
