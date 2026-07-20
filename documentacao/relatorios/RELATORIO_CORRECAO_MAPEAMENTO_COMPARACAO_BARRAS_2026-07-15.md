# Relatório de correção — mapeamento semântico do gráfico de comparação

**Data:** 2026-07-15  
**Ciclo:** C37  
**Natureza:** correção de consistência entre curadoria humana e representação gráfica.

## Problema observado pelo pesquisador
Na situação curada “Paulo tem 6 bolas. José tem 8 bolas a mais que Paulo. Quantas bolas tem José?”, os metadados são:

- referido: 6;
- valor relativo: +8;
- referendo: ?;
- termo desconhecido: referendo.

O gráfico de barras apresentava 8 como referente, 0 como referido e 6 como relação. A representação estava usando a ordem superficial dos números no enunciado e a antiga ordem interna das figuras, em vez dos papéis semânticos validados na curadoria.

## Correção implementada
O gráfico de comparação passou a receber os valores na ordem semântica explícita:

1. referido;
2. valor relativo;
3. referendo.

Os rótulos específicos do gráfico agora são “Referido”, “Valor relativo” e “Referendo”. Quando um dos termos é desconhecido, o sistema calcula internamente a extensão visual necessária para construir a barra, mas mantém “?” no rótulo até que o sujeito preencha esse papel no diagrama.

Para o caso observado, a representação inicial passa a ser:

- barra Referido: 6 unidades;
- barra Referendo: 14 unidades representadas, com rótulo “?”;
- Valor relativo: +8;
- expressão: 6 + 8 = ?.

## Barrinhas manipuláveis
As unidades são organizadas verticalmente com uma escala comum às duas barras. Foi removido o limite anterior de 12 unidades especificamente para a comparação, permitindo representar corretamente valores como 14. O tamanho e o espaçamento das barrinhas são ajustados de acordo com a maior medida da situação.

## Verificações
- compilação Ant/NetBeans: aprovada;
- regressão estrutural do Gérard: aprovada;
- teste da aba Construir situação-problema: 99 verificações aprovadas;
- internacionalização: aprovada;
- mapeamento esperado do caso 6, +8, ?: conferido no código e na geração da cena.
