# Relatório de alteração — ajuste do ponto de controle e remoção de sobreposição

**Data:** 2026-07-15  
**Ciclo:** C43  
**Tipo de intervenção:** correção de interação e de layout com intervenção explícita do pesquisador.

## Síntese
Foi corrigido o problema em que o ponto azul do eixo de comparação estava difícil de manipular e o eixo/cartão do valor relativo apareciam sobrepostos.

## Alterações implementadas
- ampliação da área de captura do ponto azul;
- possibilidade de iniciar o arraste tanto no ponto quanto ao longo da escala do eixo;
- reposicionamento do cartão do valor relativo para evitar sobreposição;
- reposicionamento dos rótulos numéricos da escala para o lado esquerdo do eixo.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**;
- correção do layout do gráfico de comparação: **aprovada**.
