# Relatório de alteração — diagrama de Venn inicia vazio e é preenchido a partir do Vergnaud

**Data:** 2026-07-15  
**Ciclo:** C65  
**Tipo de intervenção:** refinamento da lógica de inicialização e sincronização entre representações.

## Síntese
O diagrama de Venn passa a iniciar vazio. Os quadradinhos deixam de ser gerados automaticamente a partir dos valores originais do enunciado/curadoria e passam a surgir apenas quando o usuário posiciona ou preenche cada elemento correspondente no diagrama de Vergnaud.

## Alterações implementadas
- remoção do preenchimento inicial automático do Venn com base nos valores originais;
- sincronização Vergnaud -> Venn baseada apenas em valores efetivamente modelados pelo usuário;
- quando um elemento do Vergnaud ainda não foi preenchido, a região correspondente no Venn permanece vazia.

## Resultado esperado
- ao abrir a situação, o Venn aparece vazio;
- ao posicionar/preencher **Parte 1**, surgem quadradinhos apenas em **Parte 1**;
- ao posicionar/preencher **Parte 2**, surgem quadradinhos apenas em **Parte 2**;
- ao posicionar/preencher **Todo**, surgem quadradinhos apenas em **Todo**;
- o mesmo princípio vale para as demais categorias que usam o diagrama de Venn.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
