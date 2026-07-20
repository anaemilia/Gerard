# Relatório de alteração — sincronização do eixo vertical com o diagrama de Vergnaud

**Data:** 2026-07-15  
**Ciclo:** C60  
**Tipo de intervenção:** refinamento de consistência entre representações com intervenção explícita do pesquisador.

## Síntese
As alterações realizadas pelo usuário no eixo vertical da comparação de medidas passam a se propagar imediatamente para o diagrama de Vergnaud, mantendo consistência entre as quantidades e a relação.

## Alterações implementadas
- ao mover o ponto do eixo vertical do gráfico de barras, o **Valor relativo** do diagrama de Vergnaud é atualizado;
- as quantidades de **Referido** e **Referendo** representadas nas barras são transferidas para os elementos correspondentes do diagrama de Vergnaud;
- a atualização também mantém consistente o eixo x do universo dos inteiros quando ele estiver visível.

## Resultado esperado
Mudanças provocadas pela navegabilidade no gráfico de barras passam a refletir simultaneamente:
- na barra do **Referido**;
- na barra do **Referendo**;
- no número do **Valor relativo**;
- nos elementos equivalentes do diagrama de Vergnaud;
- no eixo x do universo dos inteiros associado ao número relativo.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
