# Relatório de alteração — bidirecionalidade entre quadradinhos do Venn e valores do diagrama de Vergnaud

**Data:** 2026-07-15  
**Ciclo:** C64  
**Tipo de intervenção:** reforço de consistência bidirecional entre representações.

## Síntese
As quantidades representadas por quadradinhos nas áreas do diagrama de Venn passaram a atualizar diretamente os valores correspondentes no diagrama de Vergnaud. Essa sincronização acontece em sentido bidirecional: alterações no diagrama de Vergnaud continuam reconstruindo o Venn, e movimentações/remoções de quadradinhos no Venn passam a atualizar o Vergnaud.

## Alterações implementadas
- sincronização Venn -> Vergnaud para **Composição de medidas/coleções**;
- sincronização Venn -> Vergnaud para **Transformação de medidas** (incluindo cálculo da transformação por diferença entre estado final e inicial);
- sincronização Venn -> Vergnaud para **Comparação de medidas** (incluindo atualização do valor relativo);
- atualização disparada ao soltar quadradinhos após arraste;
- atualização disparada ao remover quadradinhos com Delete/Backspace.

## Resultado esperado
Ao mover quadradinhos entre as regiões do diagrama de Venn:
- os números de **Parte 1**, **Parte 2** e **Todo** passam a refletir as contagens reais em composição;
- os números de **Estado inicial**, **Transformação** e **Estado final** passam a refletir as contagens reais em transformação;
- os números de **Referido**, **Valor relativo** e **Referendo** passam a refletir as contagens reais em comparação.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
