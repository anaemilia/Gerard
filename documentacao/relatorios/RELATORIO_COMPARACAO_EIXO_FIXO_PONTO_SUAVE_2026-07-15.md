# Relatório de alteração — eixo fixo e ponto com arraste suave na comparação de medidas

**Data:** 2026-07-15  
**Ciclo:** C44  
**Tipo de intervenção:** correção de interação e estabilidade visual com intervenção explícita do pesquisador.

## Síntese
Foi corrigido o comportamento em que o eixo do valor relativo diminuía junto com o ponto azul. Agora o eixo permanece fixo com base no valor relativo curado. Além disso, o ponto passou a ser movimentado de forma suave ao longo de toda a extensão do eixo, deixando de depender apenas de saltos discretos visuais.

## Alterações implementadas
- fixação da altura do eixo com base no valor relativo curado da situação;
- desacoplamento entre o valor máximo do eixo e o valor momentâneo do ponto;
- introdução de posição contínua (proporcional) para o ponto azul;
- manutenção do valor inteiro exibido no cartão por arredondamento da posição do ponto;
- preservação da sincronização das barrinhas com o valor inteiro corrente.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**;
- eixo do valor relativo preservado ao mover o ponto: **corrigido**.
