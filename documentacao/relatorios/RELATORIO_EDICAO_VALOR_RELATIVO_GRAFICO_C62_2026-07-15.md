# Relatório de alteração — edição do valor relativo no gráfico de barras

**Data:** 2026-07-15  
**Ciclo:** C62  
**Tipo de intervenção:** correção funcional e sincronização bidirecional.

## Problema observado
O duplo clique no cartão de **Valor relativo** abria a caixa de edição, mas o valor digitado era armazenado apenas como texto auxiliar do elemento gráfico. Como o cartão era renderizado a partir do estado do eixo, a alteração não era aplicada à relação nem às demais representações.

## Alterações implementadas
- identificação específica do cartão de valor relativo na comparação de medidas;
- validação da entrada como número inteiro com sinal;
- atualização do elemento **Valor relativo** no diagrama de Vergnaud;
- recálculo de **Referido** ou **Referendo**, conforme o termo desconhecido e os valores disponíveis;
- reconstrução do gráfico de barras com as quantidades consistentes;
- reposicionamento do ponto no eixo vertical;
- sincronização do eixo x do universo dos inteiros;
- expansão da escala quando o valor editado ultrapassa o limite anterior;
- preservação do sinal positivo ou negativo na sincronização.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
