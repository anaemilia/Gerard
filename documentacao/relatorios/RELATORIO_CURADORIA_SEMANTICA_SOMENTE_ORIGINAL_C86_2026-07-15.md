# Relatório de alteração — curadoria semântica somente na versão original

**Data:** 2026-07-15  
**Ciclo:** C86  
**Tipo de intervenção:** consistência teórica, metodológica e operacional.

## Regra implementada
A versão original é a única fonte de verdade para os dados semânticos da situação-problema. As traduções mantêm apenas o enunciado no idioma selecionado e o seu estado de validação linguística.

## Comportamento da tela
Quando a versão aberta é uma tradução:

- o enunciado permanece editável;
- o idioma da versão permanece selecionável;
- a validação linguística permanece disponível;
- fonte, subtipo, personagens, valores, sinais, papel desconhecido, representação visual e observações ficam bloqueados;
- os campos bloqueados exibem os dados herdados da versão original.

## Persistência
No salvamento de uma tradução, os dados semânticos não são lidos dos controles bloqueados. Eles são copiados novamente da versão original vinculada. Isso impede divergências entre idiomas e elimina a duplicação da curadoria conceitual.

## Consistência entre versões
Os campos `personagem_1`, `personagem_2`, `personagem_3` e `observacoes` passam a integrar explicitamente o conjunto compartilhado entre a versão original e suas traduções.

## Verificações
- compilação Ant/NetBeans;
- teste específico `testar_curadoria_semantica_somente_original.sh`;
- regressão geral do projeto.
