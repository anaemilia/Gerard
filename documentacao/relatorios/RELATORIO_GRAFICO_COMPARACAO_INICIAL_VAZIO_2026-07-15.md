# Relatório de alteração — gráfico de comparação inicialmente vazio

**Data:** 2026-07-15  
**Ciclo:** C50  
**Tipo de intervenção:** refinamento pedagógico e de apresentação com intervenção explícita do pesquisador.

## Síntese
Na categoria de comparação de medidas, o gráfico de barras não deve antecipar visualmente as quantidades da curadoria antes que o usuário comece a posicionar números no diagrama de Vergnaud. A representação manipulável passa a iniciar vazia.

## Alterações implementadas
- enquanto nenhum número tiver sido modelado pelo usuário no diagrama formal de comparação, o gráfico de barras permanece vazio;
- os valores curados continuam disponíveis internamente, mas deixam de preencher automaticamente a representação manipulável inicial;
- após o primeiro posicionamento de número pelo usuário, o gráfico volta a sincronizar as quantidades e derivações necessárias.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
