# Relatório de alteração — sincronização bidirecional do valor relativo na comparação

**Data:** 2026-07-15  
**Ciclo:** C61  
**Tipo de intervenção:** reforço de consistência entre representações.

## Síntese
O valor relativo da comparação de medidas passou a ser sincronizado em todas as ações, nas duas direções: do diagrama de Vergnaud para a área do gráfico de barras e da área do gráfico de barras de volta para o diagrama de Vergnaud.

## Alterações implementadas
- após cada reconstrução/sincronização do gráfico de barras, o valor relativo passa a atualizar automaticamente:
  - o ponto do eixo vertical;
  - o cartão do valor relativo;
  - o eixo x do universo dos inteiros, quando visível;
- as alterações feitas no eixo vertical continuam atualizando o diagrama de Vergnaud;
- as alterações feitas no diagrama de Vergnaud ou no eixo x agora também reposicionam corretamente os controles do gráfico de barras.

## Resultado esperado
Em qualquer ação de edição, arraste ou navegabilidade, o valor relativo permanece consistente entre:
- círculo/elemento de valor relativo no diagrama de Vergnaud;
- cartão de valor relativo no gráfico de barras;
- eixo vertical do gráfico de barras;
- eixo x do universo dos inteiros.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
