# Relatório de alteração — remoção do campo “subtipo” da curadoria

**Data:** 2026-07-15  
**Ciclo:** C87  
**Tipo de intervenção:** simplificação da curadoria e prevenção de redundância semântica.

## Problema
O campo **subtipo** repetia informações já definidas pelos campos estruturados da curadoria, como categoria, papéis semânticos, sinais e termo desconhecido. Sua edição manual poderia criar contradições.

## Alteração
- o campo **subtipo** foi retirado da tela de curadoria;
- não há mais preenchimento manual desse campo;
- a estrutura interna legada foi preservada para compatibilidade com os arquivos existentes e rotinas antigas;
- nenhum outro campo da curadoria foi removido ou reposicionado por esta alteração.

## Resultado esperado
O pesquisador realiza a curadoria somente por meio dos campos semânticos estruturados. A interface deixa de apresentar uma informação redundante.

## Verificações
- teste específico de ausência do campo na interface;
- compilação Ant/NetBeans;
- regressão geral do projeto.
