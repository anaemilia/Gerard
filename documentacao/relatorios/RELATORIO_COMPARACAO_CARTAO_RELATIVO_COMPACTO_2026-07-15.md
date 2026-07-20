# Relatório de alteração — cartão compacto do valor relativo

**Data:** 2026-07-15  
**Ciclo:** C46  
**Tipo de intervenção:** refinamento visual com intervenção explícita do pesquisador.

## Síntese
A área destinada ao valor relativo na representação de comparação de medidas foi reduzida. Como esse elemento apresenta apenas um número antecedido de sinal, o cartão vertical foi substituído por um quadrado compacto.

## Alterações implementadas
- redução do cartão para aproximadamente 78 × 72 pixels;
- centralização do valor com sinal dentro do quadrado;
- deslocamento do rótulo **Valor relativo** para fora e acima do quadrado;
- manutenção da sincronização com o ponto de controle e com a barra do referendo;
- preservação do espaçamento em relação ao eixo para evitar sobreposição.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**;
- manutenção da interação do eixo e da sincronização do referendo: **preservada**.
