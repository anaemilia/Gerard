# Relatório de alteração — análise somente após posicionamento

**Data:** 2026-07-16  
**Ciclo:** C96

## Problema observado
A tela **Análise da modelagem** podia ser aberta logo após o carregamento da situação-problema, mesmo sem qualquer ação de posicionamento no diagrama de Vergnaud. Nessa condição, os protocolos apareciam vazios e a fotografia mostrava apenas a estrutura ainda não modelada.

## Regra implementada
A análise qualitativa da tentativa passa a ficar disponível somente quando existe **ao menos um elemento atualmente posicionado sobre um elemento semântico do diagrama de Vergnaud**.

## Comportamento
- antes do primeiro posicionamento, o botão de análise permanece oculto e desabilitado;
- após o primeiro posicionamento no diagrama de Vergnaud, o botão aparece e é habilitado;
- se a modelagem for restaurada e não restar nenhum elemento posicionado, o botão volta a ficar oculto;
- a própria rotina de abertura possui uma segunda verificação, impedindo que a janela seja exibida sem posicionamentos mesmo por chamada indireta.

## Verificações
- compilação Ant/NetBeans;
- teste Swing específico antes/depois do posicionamento;
- regressão geral do projeto.
