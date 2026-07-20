# Relatório de alteração — correção do travamento no fechamento da curadoria

**Data:** 2026-07-15  
**Ciclo:** C85  
**Tipo de intervenção:** correção funcional e de usabilidade.

## Problema observado
Na janela de curadoria detalhada, o botão de fechamento e o X podiam parecer travados, especialmente quando a tradução selecionada estava marcada como validada, mas continha valores numéricos diferentes dos valores da situação original.

## Causa
O fechamento executava duas persistências sucessivas: uma gravação intermediária da tradução e uma gravação final de toda a curadoria. Além disso, o fluxo podia abrir uma validação modal durante o fechamento, mantendo a janela principal bloqueada e dando a impressão de que os comandos não funcionavam.

## Alterações implementadas
- o botão passou a se chamar **Salvar e fechar**;
- a tradução atualmente selecionada é incorporada ao modelo sem gravação intermediária;
- toda a curadoria é persistida uma única vez ao final;
- traduções com valores numéricos divergentes são salvas como **não validadas**, sem bloquear o fechamento;
- após o fechamento, o usuário recebe um aviso claro sobre a retirada da validação;
- o botão e o X utilizam o mesmo fluxo de salvamento;
- em caso de erro, o botão é reabilitado e a janela permanece aberta para nova tentativa.

## Resultado esperado
O botão **Salvar e fechar** e o X respondem normalmente. A curadoria não perde o texto digitado e não permanece bloqueada por uma validação numérica oculta.

## Verificações realizadas
- compilação Ant/NetBeans;
- teste específico do fluxo de fechamento;
- regressão geral do projeto.
