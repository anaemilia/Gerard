# Relatório de alteração — arraste contínuo do ponto e barra do referendo sincronizada

**Data:** 2026-07-15  
**Ciclo:** C45  
**Tipo de intervenção:** correção funcional de interação com intervenção explícita do pesquisador.

## Problema identificado
O ponto azul respondia apenas ao pressionamento inicial do mouse. O tratamento de `mouseDragged` não encaminhava o movimento para o controle da comparação. Por isso, o ponto aparentava saltar entre valores discretos, em vez de acompanhar continuamente o cursor.

## Alterações implementadas
- inclusão do tratamento do arraste contínuo do ponto azul em `mouseDragged`;
- atualização da posição proporcional do ponto a cada movimento do mouse;
- manutenção do eixo fixo de `0` ao valor relativo curado;
- sincronização da quantidade de quadradinhos da barra do **Referendo** com o valor inteiro indicado pelo ponto;
- atualização das barrinhas somente quando o ponto atravessa o limite de um novo valor inteiro, evitando reconstruções desnecessárias durante o movimento contínuo;
- preservação do valor mostrado no cartão do valor relativo.

## Comportamento esperado
O ponto percorre o eixo de forma contínua. Como a quantidade de quadradinhos é inteira, a barra do Referendo aumenta ou diminui uma unidade quando o ponto ultrapassa a faixa correspondente ao próximo número do eixo.

## Verificação realizada
- compilação do projeto com Ant/NetBeans: **aprovada**;
- presença do fluxo de arraste contínuo em `mouseDragged`: **verificada**;
- sincronização inteira da barra do Referendo: **implementada**.
