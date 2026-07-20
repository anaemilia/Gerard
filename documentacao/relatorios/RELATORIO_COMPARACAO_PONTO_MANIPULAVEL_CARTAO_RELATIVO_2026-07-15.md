# Relatório de alteração — ponto de controle manipulável e cartão do valor relativo restaurado

**Data:** 2026-07-15  
**Ciclo:** C42  
**Tipo de intervenção:** refinamento visual e de interação com intervenção explícita do pesquisador.

## Síntese
Na representação de comparação de medidas, o ponto azul do segmento graduado passou a ser manipulável por arraste. Além disso, foi restaurado o quadrado/cartão do valor relativo, agora com o valor exibido sincronizado com o ponto do eixo e com a diferença entre as barrinhas.

## Alterações implementadas
- ativação do arraste do ponto azul no segmento do valor relativo;
- atualização dinâmica das barrinhas da comparação a partir do ponto de controle;
- restauração do cartão do valor relativo à direita;
- sincronização do valor mostrado no cartão com o eixo e com a diferença observável entre as barras;
- preservação dos nomes de personagens abaixo de Referido e Referendo.

## Verificação realizada
- compilação do projeto em Ant/NetBeans: **aprovada**;
- interação por arraste do ponto azul: **implementada**;
- sincronização visual do cartão do valor relativo: **implementada**.
