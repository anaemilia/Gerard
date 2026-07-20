# Relatório de alteração — estabilidade do eixo dos inteiros

**Data:** 2026-07-15  
**Ciclo:** C55  
**Tipo de intervenção:** correção de estabilidade visual e de interação.

## Problema observado
Ao reduzir o valor relativo pelo ponto azul, a escala do eixo dos inteiros era recalculada com base no valor corrente. Por exemplo, ao passar de `+8` para `+5`, os extremos do eixo mudavam de `-8 ... +8` para `-5 ... +5`, alterando as marcas e o espaçamento.

## Alterações implementadas
- a escala do eixo passa a ser definida quando o apoio visual é aberto;
- a redução do valor relativo desloca apenas o ponto azul;
- os extremos, as marcas, o espaçamento e a posição do zero permanecem estáveis;
- a escala somente pode aumentar quando um novo valor externo ultrapassa o limite atual;
- ao ocultar e abrir novamente o apoio visual, uma nova escala pode ser inicializada.

## Exemplo
Com o eixo inicialmente configurado para `-8 ... +8`, mover o ponto de `+8` para `+5` mantém o eixo em `-8 ... +8` e altera apenas o valor selecionado para `+5`.

## Verificações realizadas
- compilação Ant/NetBeans: **aprovada**;
- teste automatizado em modo headless: escala inicial `8`, escala após redução `8`, valor selecionado `5` — **aprovado**.
