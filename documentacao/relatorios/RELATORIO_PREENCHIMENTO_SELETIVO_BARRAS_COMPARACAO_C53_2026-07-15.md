# Relatório de alteração — preenchimento seletivo das barras na comparação

**Data:** 2026-07-15  
**Ciclo:** C53  
**Tipo de intervenção:** refinamento semântico da sincronização entre diagrama de Vergnaud e gráfico de barras.

## Síntese
Na comparação de medidas, o preenchimento das barras do gráfico não deve antecipar o valor do outro papel semântico apenas porque um dos números foi posicionado no diagrama de Vergnaud. A barra de **Referido** passa a preencher somente quando o usuário posiciona o referido; a barra de **Referendo** passa a preencher somente quando o usuário posiciona o referendo.

## Alterações implementadas
- remoção do preenchimento automático da barra faltante a partir da curadoria;
- manutenção do valor relativo como informação que pode vir da curadoria;
- preservação do comportamento em que cada barra reflete apenas o valor explicitamente modelado pelo usuário.

## Resultado esperado
- ao posicionar apenas o **Referido**, somente a barra de **Referido** é preenchida;
- ao posicionar apenas o **Referendo**, somente a barra de **Referendo** é preenchida;
- o outro papel permanece visualmente vazio até ser posicionado pelo usuário ou ajustado por interação específica do controle.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
